package com.simulator.simulator.main;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.report.ReportUtil;
import com.simulator.simulator.report.Reporter;
import com.simulator.simulator.resousce.Cluster;
import com.simulator.simulator.resousce.DMA;
import com.simulator.simulator.resousce.DSP;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.scheduleAlgorithm.FIFO;
import com.simulator.simulator.timeCnter.NewTimer;
import org.junit.Test;
import com.simulator.simulator.scheduleManager.TaskManager;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args){
        ExecutorService threadPool = Executors.newCachedThreadPool();
        LinkedList<Task> globalTaskList = TaskManager.getGlobalTaskList();
        NewTimer.getBeginTime();

        long begin = System.currentTimeMillis();

        //多线程运行task
//        for (Task t :globalTaskList) {
//            threadPool.execute(t);
//        }


        ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();
        resourcesManager.submitTaskGraph(TaskManager.getTaskDiagram());

        for(int i = 0;i < globalTaskList.size();i++){
            System.out.println(i + "||" + globalTaskList.get(i).clusterId);
        }


        threadPool.execute(resourcesManager);
        threadPool.execute(FIFO.getInstance());


        //start all resources
        List<Thread> components = resourcesManager.getComponents();
        for(Thread thread:components){
            threadPool.execute(thread);
        }

        //start reporter
        threadPool.execute(new Reporter());

        while(!allTaskFinish()){
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int simulateTime = (int) (System.currentTimeMillis() - begin);

        int totalTaskCost = 0;
        for(Cluster cluster :resourcesManager.getClusterList()){
            List<DSP> dspList = cluster.getDspList();
            totalTaskCost += cluster.totalCost;
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

        for(Cluster cluster :resourcesManager.getClusterList()){
            double utilization = 100 * (double)cluster.totalCost/totalTaskCost;
            System.out.println("cluster" + cluster.clusterId + "执行任务量：" + cluster.totalCost + " 占比：" + utilization + "%");

            //Cluster report
            String fileName = "Cluster" + cluster.clusterId;
            ReportUtil.writer(cluster,fileName);
        }

        for(Task t:globalTaskList){
            System.out.println(t.taskReport);
        }

        System.out.println(simulateTime);

        threadPool.shutdown();
        System.out.println("end");




    }

    static private boolean allTaskFinish(){
        LinkedList<Task> globalTaskList = TaskManager.getGlobalTaskList();
        for(Task t:globalTaskList){
            if(!t.ifFinish())return false;
        }
        return true;
    }


    @Test
    public void test01(){
//        double utilization = (double)124234/324234;
//        System.out.println("使用率" + utilization);
    }
}
