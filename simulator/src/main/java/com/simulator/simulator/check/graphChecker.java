package com.simulator.simulator.check;

import java.util.concurrent.TimeUnit;

public class graphChecker extends Thread{

    @Override
    public void run() {
        while(true){
            CheckUtil.checkGraph();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
