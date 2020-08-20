package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.timeCnter.myTime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

class DMATask {
    DataInstance data;
    int src;
    int target;

    public DMATask(DataInstance data, int src, int target) {
        this.data = data;
        this.src = src;
        this.target = target;
    }

    @Override
    public int hashCode() {
        String hash = data.dataName;
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DMATask){
            DMATask dmaTask = (DMATask) obj;
            return data.equals(dmaTask.data);
        }
        return false;
    }

    public DataInstance getData() {
        return data;
    }

    public int getSrc() {
        return src;
    }

    public int getTarget() {
        return target;
    }
}

public class DMA extends Thread{
    BlockingQueue<DMATask> taskQueue = new LinkedBlockingDeque<>();
    BlockingQueue<DMATask> taskSubmitted = new LinkedBlockingDeque<>();
    int dmaSpeed = 1;
    boolean run = true;

    int globalTime = 0;
    int activeTime = 0;

    static int id = 0;
    private int dmaId = 0;
    private int myClusterId = 0;

    synchronized public void submit(DataInstance data, int src, int target)  {
        DMATask dmaTask = new DMATask(data,src,target);


        try {
            if(taskSubmitted.contains(dmaTask))return;
            taskSubmitted.put(dmaTask);

            taskQueue.put(dmaTask);
            System.out.println("submmit: "+ data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DMA(int myClusterId){
        dmaId = id++;
        this.myClusterId = myClusterId;
    }

    public void execute(){

    }

    public int getActiveTime() {
        return activeTime;
    }

    public int getDmaId() {
        return dmaId;
    }

    @Override
    public void run() {
        while(run){
            if(!taskQueue.isEmpty()){
                try {
                    DMATask dmaTask = taskQueue.take();

                    //Refresh time
                    globalTime = myTime.getTime();

                    //calculate data handle time
                    int executeTime = dmaTask.getData().total_size / dmaSpeed;
                    int testTime = 1;

                    //execute
                    TimeUnit.SECONDS.sleep(2);
                    ResourcesManager.getResourcesManager().dmaSave(myClusterId,dmaTask.data);
//                    System.out.println("save:" + dmaTask.data.dataName);

                    //Refresh time
//                    globalTime += executeTime;
                    globalTime += testTime;
                    activeTime += testTime;

                    if(globalTime > myTime.getTime()) myTime.setTime(globalTime);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkIfTaskSubmitted(DataInstance DataInstance) {
        return taskQueue.contains(new DMATask(DataInstance,0,0));
    }
}
