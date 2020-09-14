package com.simulator.simulator.timeCnter;

import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.concurrent.TimeUnit;

public class NewTimer extends Thread{

    static long beginTime = System.currentTimeMillis();

    public static long getBeginTime() {
        return beginTime;
    }

    public static double curTTI = 0;

    @Override
    public void run() {
        while(true){
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                curTTI = (System.currentTimeMillis() - beginTime)/(double)50000;
//                System.out.println(curTTI);
                TaskManager.getInstance().updateTask();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
