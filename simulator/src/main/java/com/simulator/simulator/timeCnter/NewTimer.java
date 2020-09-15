package com.simulator.simulator.timeCnter;

import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.concurrent.TimeUnit;
import java.util.Queue;

public class NewTimer extends Thread{

    static long beginTime = System.currentTimeMillis();

    public static long getBeginTime() {
        return beginTime;
    }

    public static double curTTI = 0;

    public Queue<Integer> queue;
    public NewTimer(Queue<Integer> q){
        queue = q;
    }

    @Override
    public void run() {
        while(true){
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                curTTI = (System.currentTimeMillis() - beginTime)/(double)50000;
                //System.out.println("Poll:"+queue.size()+","+queue.poll());
                if(TaskManager.getInstance().isReady())
                    TaskManager.getInstance().updateTask();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
