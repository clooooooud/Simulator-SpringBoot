package com.simulator.simulator.main;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.report.ReportUtil;
import com.simulator.simulator.report.Reporter;
import com.simulator.simulator.resousce.Cluster;
import com.simulator.simulator.resousce.DMA;
import com.simulator.simulator.resousce.DSP;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.scheduleAlgorithm.FIFO;
import com.simulator.simulator.scheduleManager.TaskManager;
import com.simulator.simulator.scheduleManager.TaskUtils;
import com.simulator.simulator.timeCnter.NewTimer;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.Queue;
import com.simulator.simulator.timeCnter.GraphGenerator;

public class Main {
    public static void main(String[] args){
        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        NewTimer.getBeginTime();

        ReportUtil.begin = System.currentTimeMillis();

        //多线程运行task
//        for (Task t :globalTaskList) {
//            threadPool.execute(t);
//        }
        ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();
        resourcesManager.setClusterNum(16);

        //设置图周期
        TaskManager.getInstance().setPeriod(new double[]{100,1,1,4,1,100});
        TaskManager.getInstance().setDdl(new double[]{100,1.5,1.5,4,1,100});
        //设置图依赖
        TaskManager.getInstance().setDependency(1,4);
        TaskManager.getInstance().setDependency(2,4);
        TaskManager.getInstance().setDependency(3,4);


        System.out.println(TaskManager.getInstance());

        threadPool.execute(resourcesManager);
        threadPool.execute(FIFO.getInstance());

        //start all resources
        List<Thread> components = resourcesManager.getComponents();
        for(Thread thread:components){
            threadPool.execute(thread);
        }

        //start reporter
        threadPool.execute(new Reporter());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //提交任务,改为按照时序提交
//        resourcesManager.submitTaskGraph(TaskManager.getInstance().getTaskGraph(1).getTaskDiagram());

        Queue<Integer> q = new LinkedList<Integer>();;
        //threadPool.execute(new GraphGenerator(q));
        threadPool.execute(new NewTimer(q));

        System.out.println("===========================================================================================================================");
    }




    @Test
    public void test01(){
//        double utilization = (double)124234/324234;
//        System.out.println("使用率" + utilization);
    }
}
