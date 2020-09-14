package com.simulator.simulator.scheduleManager;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.report.ReportUtil;
import com.simulator.simulator.resousce.Cluster;
import com.simulator.simulator.resousce.DMA;
import com.simulator.simulator.resousce.DSP;
import com.simulator.simulator.resousce.ResourcesManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskUtils {
    synchronized static public boolean graphFinish(TaskGraphSubmitted taskGraphSubmitted){
        LinkedList<Task> globalTaskList = taskGraphSubmitted.taskGraph.getGlobalTaskList();
        for(Task t:globalTaskList){
            if(!t.ifFinish()){
//                System.out.println(" " + t + " 未完成 " + Thread.currentThread().getName());
//                report();
                return false;
            }
        }
        return true;
    }

    public static void report(){
        for(Cluster cluster : ResourcesManager.getResourcesManager().getClusterList()){
            List<DSP> dspList = cluster.getDspList();
            for(DSP d : dspList){
                System.out.println(d.getDspId() + "||  "+ d.getQueue().size()+ "||  "+ d.candidateQueue.size());
            }
        }
    }


    /**
     * 启动柜DSP排序机制
     */
    public void DSPsort(){
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Cluster cluster : ResourcesManager.getResourcesManager().getClusterList()){
            List<DSP> dspList = cluster.getDspList();
            for(DSP d : dspList){
                d.qosSort();
            }
        }
    }
}