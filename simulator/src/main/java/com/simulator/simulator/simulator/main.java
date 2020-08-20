package com.simulator.simulator.simulator;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.resousce.Cluster;
import com.simulator.simulator.resousce.DMA;
import com.simulator.simulator.resousce.DSP;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.scheduleAlgorithm.FIFO;
import com.simulator.simulator.timeCnter.NewTimer;
import com.simulator.simulator.timeCnter.myTime;
import org.junit.Test;
import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class main {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        LinkedList<Task> globalTaskList = TaskManager.getGlobalTaskList();
        NewTimer.getBeginTime();

        long begin = System.currentTimeMillis();


        for (Task t :globalTaskList) {
            threadPool.execute(t);
        }

        ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();

        threadPool.execute(resourcesManager);
        threadPool.execute(FIFO.getInstance());

        //start all resources
        List<Thread> components = resourcesManager.getComponents();
        for(Thread thread:components){
            threadPool.execute(thread);
        }

        while(!allTaskFinish()){
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int simulateTime = (int) (System.currentTimeMillis() - begin);

        for(Cluster cluster :resourcesManager.getClusterList()){
            List<DSP> dspList = cluster.getDspList();
            for(DSP d : dspList){
//                System.out.println(d.getDspId() + ":空闲时间" + (myTime.getTime() - d.getActiveTime()) + ",执行时间：" +d.getActiveTime());
                double utilization = (double)(simulateTime - d.getNewIdleTime())/simulateTime;
                System.out.println(d.getDspId() + ":执行时间" + (simulateTime - d.getNewIdleTime()) + ",空闲时间：" +d.getNewIdleTime());
                System.out.println("使用率：" + utilization*100 + "%");
            }
            for(DMA d : cluster.getDmaList()){
                System.out.println("DMA:" + d.getDmaId() + ":空闲时间" + (myTime.getTime() - d.getActiveTime()) + " shu,执行时间：" +d.getActiveTime());
            }
        }

        System.out.println(simulateTime);

        try {
            // 向学生传达“问题解答完毕后请举手示意！”
            threadPool.shutdown();

            // 向学生传达“XX分之内解答不完的问题全部带回去作为课后作业！”后老师等待学生答题
            // (所有的任务都结束的时候，返回TRUE)
            if(!threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS)){
                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
            System.out.println("awaitTermination interrupted: " + e);
            threadPool.shutdownNow();
        }

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
