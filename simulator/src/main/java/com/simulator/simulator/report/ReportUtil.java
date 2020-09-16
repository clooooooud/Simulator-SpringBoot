package com.simulator.simulator.report;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.check.CheckUtil;
import com.simulator.simulator.resousce.*;
import com.simulator.simulator.scheduleManager.TaskGraphSubmitted;
import com.simulator.simulator.timeCnter.NewTimer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReportUtil {

    public static long begin;

    static public void writer(ReportInterFace report,String fileName){
        try {
            String text = report.getReport();
            File file = new File(fileName);
            boolean b = file.createNewFile();
            if(b) {
                Writer out = new FileWriter(file);
                out.write(text);
                out.close();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void graphReport(TaskGraphSubmitted taskGraphSubmitted){
        ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();
        int simulateTime = (int) (System.currentTimeMillis() - begin);

        int totalTaskCost = 0;
        for(Cluster cluster :resourcesManager.getClusterList())totalTaskCost += cluster.totalCost;

//        DspAndDmaReport(resourcesManager,totalTaskCost,simulateTime);
//        clusterReport(resourcesManager,totalTaskCost);
//        taskReport(taskGraphSubmitted);

        StringBuilder stringBuilder = new StringBuilder(taskGraphSubmitted.getTaskGraph().graphName+ "(" + taskGraphSubmitted.getId() + "):" + "\t");
        stringBuilder.append("begin in: " + taskGraphSubmitted.submitTTI + "; " + "\t" + "DDL: " + taskGraphSubmitted.ddl + "\t");
        stringBuilder.append("finish in: " + (double)simulateTime/50000 + "; " + "\t" + (NewTimer.curTTI<taskGraphSubmitted.ddl));


        System.out.println(stringBuilder);
    }

    static void DspAndDmaReport(ResourcesManager resourcesManager,int totalTaskCost,int simulateTime){
        for(Cluster cluster :resourcesManager.getClusterList()){
            List<DSP> dspList = cluster.getDspList();
            for(DSP d : dspList){
//                System.out.println(d.getDspId() + ":空闲时间" + (myTime.getTime() - d.getActiveTime()) + ",执行时间：" +d.getActiveTime());
                double utilization = (double)d.getTotalBusyTime()/simulateTime;
                System.out.println(d.getDspId() + ":执行时间" + d.getTotalBusyTime() + ",空闲时间：" +(simulateTime - d.getTotalBusyTime()) + ",数据等待时间：" + d.getTotalDataWaitingTime());
                System.out.println("使用率：" + utilization*100 + "%");

                //DSP report
                String fileName = "DSP" + d.getDspId();
                ReportUtil.writer(d,fileName);
            }
            for(DMA d : cluster.getDmaList()){
                System.out.println("DMA:" + d.getDmaId() + ":搬运量： " + d.totalDmaSize);

                //DMA report
                String fileName = "DMA" + d.getDmaId();
                ReportUtil.writer(d,fileName);
            }
        }
    }


    static void clusterReport(ResourcesManager resourcesManager,int totalTaskCost){

        for(Cluster cluster :resourcesManager.getClusterList()){
            double utilization = 100 * (double)cluster.totalCost/totalTaskCost;
            System.out.println("cluster" + cluster.clusterId + "执行任务量：" + cluster.totalCost + " 占比：" + utilization + "%");

            //Cluster report
            String fileName = "Cluster" + cluster.clusterId;
            ReportUtil.writer(cluster,fileName);
        }
    }

    static void taskReport(TaskGraphSubmitted taskGraphSubmitted){
        LinkedList<Task> globalTaskList = taskGraphSubmitted.getTaskGraph().getGlobalTaskList();
        for(Task t:globalTaskList){
            System.out.println(t.taskReport);
        }
    }

    /**
     * 1.	DSP平均实时负载方差
     * 2.	CLUSTER平均总负载方差
     * 3.	片外访存总量
     * 4.	任务图接受率
     * 5.	任务图平均等待时间
     * 6.	DSP空闲时间：（平均）
     * 7.	DMA等待时间：数据等待时间
     * 8.	MEM使用峰值
     * 9.	MEM使用实时值
     */
    static String reportV1(){
        ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();
        StringBuilder stringBuilder = new StringBuilder();

        //DSP平均负载方差
        double dspVariance = 0,clusterVariance = 0;
        double clusterAve = 0,dspAve = 0;
        //计算dsp当前负载方差(/k cycle)
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(DSP dsp:cluster.getDspList()){
                dspAve += (double)dsp.curCost/1000;
            }
        }
//        System.out.println(dspAve);
        dspAve /= resourcesManager.getClusterList().size()*4;
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(DSP dsp:cluster.getDspList()){
                dspVariance += Math.pow(((double)dsp.curCost/1000-dspAve),2)/(double)resourcesManager.getClusterList().size()*4;
            }
        }
//        System.out.println(dspVariance);
        stringBuilder.append(dspVariance).append("\t");

        //计算CLUSTER平均总负载方差
        for(Cluster cluster:resourcesManager.getClusterList()){
            clusterAve += (double)cluster.totalCost/1000;
        }
        clusterAve /= resourcesManager.getClusterList().size();
        for(Cluster cluster:resourcesManager.getClusterList()){
            clusterVariance += Math.pow(((double)cluster.totalCost/1000-clusterAve),2)/(double)resourcesManager.getClusterList().size();
        }
        stringBuilder.append(clusterVariance).append("\t");

        //计算片外访存总量
        int offChipMem = 0;
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(DMA dma:cluster.getDmaList()){
                offChipMem += dma.totalDmaSize;
            }
        }
        stringBuilder.append(offChipMem).append("\t");

        //任务图接受率
        stringBuilder.append(CheckUtil.accessRate).append("\t");

        //任务图平均等待时间
        int graphAveWaitTime = 0;
        int cnt = 0;
        List<Map<Integer, TaskGraphSubmitted>> submittedTaskGraph = resourcesManager.getSubmittedTaskGraph();
        for(Map<Integer, TaskGraphSubmitted> map:submittedTaskGraph){
            for(Integer i:map.keySet()){
                graphAveWaitTime += map.get(i).waitTime;
                cnt ++;
            }
        }
        graphAveWaitTime /= cnt;
        stringBuilder.append(graphAveWaitTime).append("\t");

        //DSP空闲时间：（平均）
        int dspIdleAve = 0;
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(DSP dsp:cluster.getDspList()){
                dspIdleAve += dsp.getTotalIdleTime();
            }
        }
        dspIdleAve /= resourcesManager.getClusterList().size()*4;
        stringBuilder.append(dspIdleAve).append("\t");

        //DMA等待时间：数据等待时间
        int dataWaitAve = 0;
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(DSP dsp:cluster.getDspList()){
                dataWaitAve += dsp.getTotalDataWaitingTime();
            }
        }
        dataWaitAve /= resourcesManager.getClusterList().size()*4;
        stringBuilder.append(dataWaitAve).append("\t");

        //MEM使用峰值
        int memPeakVal = 0;
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(Memory memory:cluster.getMemoryList()){
                memPeakVal += memory.peakValue;
                memory.peakValue = 0;
            }
        }
        memPeakVal /= resourcesManager.getClusterList().size() * 1;
        stringBuilder.append(memPeakVal).append("\t");

        //MEM使用实时值
        int memCurVal = 0;
        for(Cluster cluster:resourcesManager.getClusterList()){
            for(Memory memory:cluster.getMemoryList()){
                memCurVal += memory.curCapacity;
            }
        }
        memCurVal /= resourcesManager.getClusterList().size() * 1;
        stringBuilder.append(memCurVal).append("\t");

        return stringBuilder.toString();
    }



}
