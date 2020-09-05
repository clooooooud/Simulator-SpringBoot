package com.simulator.simulator.timeCnter;

import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.concurrent.TimeUnit;

public class NewTimer extends Thread{

    static long beginTime = System.currentTimeMillis();

    public static long getBeginTime() {
        return beginTime;
    }

    @Override
    public void run() {
        while(true){
            TaskManager.getInstance().updateTask();
            try {
                TimeUnit.SECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
