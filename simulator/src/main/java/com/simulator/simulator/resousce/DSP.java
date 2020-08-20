package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.timeCnter.myTime;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DSP extends Thread {
    private boolean run = true;
    private int globalTime = 0;
    private int idleTime = 0;
    private int myClusterId;
    //单位是GHz
    private double DSPSpeed = 1.3;

    private int newIdleTime = 0;

    private ResourcesStatus resourcesStatus = ResourcesStatus.IDLE;

    private int activeTime = 0;

    private BlockingQueue<Task> queue = new ArrayBlockingQueue<Task>(30);
    private LinkedList<Integer> test = new LinkedList<>();

    static int id = 0;
    private int dspId;

    public DSP(int clusterId){
        dspId = id+1;
        id++;
        myClusterId = clusterId;
    }

    public int getNewIdleTime() {
        return newIdleTime;
    }

    public Queue<Task> getQueue() {
        return queue;
    }

    public void stopDSP(){
        run = false;
    }

    public void addTask(int index){
        test.add(index);
    }

    public int getGlobalTime() {
        return globalTime;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public int getDspId() {
        return dspId;
    }

    private void getData(Task task){
        ResourcesManager.getResourcesManager().getDate(myClusterId,task);
    }

    private void execute(Task task){
        //Refresh clock
        globalTime = myTime.getTime();
        try {
            double sleepTime = (double)task.cost/DSPSpeed;
            TimeUnit.MILLISECONDS.sleep((int)sleepTime);


//            TimeUnit.SECONDS.sleep(1);

//            System.out.println(task.job_inst_idx + "finish by:" + dspId + "in" + Thread.currentThread().getName());

            activeTime += 10;
            globalTime += 10;
            if(globalTime > myTime.getTime()){
                myTime.setTime(globalTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeBack(Task task){
//        System.out.println(task.name +  ":ins "+ task.job_inst_idx_inside +"(" + task.job_inst_idx + ")" + " try to save data");
        ResourcesManager.getResourcesManager().writeBack(myClusterId,task);
    }

    private long idleCnt = System.currentTimeMillis();
    private void idleBegin(long currentTime){
        idleCnt = currentTime;
    }

    @Override
    public void run() {
        while(run){
            while(true){
                if(!queue.isEmpty()){
                    try {
                        resourcesStatus = ResourcesStatus.BUSY;
                        newIdleTime += System.currentTimeMillis() - idleCnt;

                        Task task = queue.take();
                        getData(task);
                        execute(task);
                        writeBack(task);
                        task.finishTask();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    resourcesStatus = ResourcesStatus.IDLE;
                    idleBegin(System.currentTimeMillis());

                }
            }
        }
        System.out.println("finishDSP");
    }

    public void submit(Task task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
