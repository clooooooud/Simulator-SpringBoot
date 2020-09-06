package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.report.DSPReport;
import com.simulator.simulator.report.ReportInterFace;
import com.simulator.simulator.timeCnter.myTime;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;



public class DSP extends Thread implements ReportInterFace {
    private boolean run = true;
    private int globalTime = 0;
    private int idleTime = 0;
    private int myClusterId;
    //单位是GHz
    private double DSPSpeed = 1.3;

    /**
     * 时间统计：
     *      totalIdleTime：闲置时间
     *      totalBusyTime：工作时间
     *      totalDataWaitingTime：数据等待时间
     */
    private int totalIdleTime = 0;
    private int totalBusyTime = 0;
    private int totalDataWaitingTime = 0;
    public LinkedList<DSPReport> dspReports = new LinkedList<>();

    /**
     * 开销统计（cycle）：
     *      totalCost：总开销
     *      curCost：当前负载
     */
    public int totalCost = 0;
    public int curCost = 0;

    private ResourcesStatus resourcesStatus = ResourcesStatus.IDLE;

    private int activeTime = 0;

    private BlockingQueue<Task> queue = new ArrayBlockingQueue<Task>(30);
    private LinkedList<Integer> test = new LinkedList<>();

    /**
     * dsp总编号
     */
    static int id = 0;
    private int dspId;

    /**
     * cluster内部DSP编号
     */
    public static int idInCluster = 0;
    private int dspIdInCluster;

    public DSP(int clusterId){
        dspId = id;dspIdInCluster = idInCluster;
        id++;idInCluster++;

        myClusterId = clusterId;
    }

    public int getTotalBusyTime() {
        return totalBusyTime;
    }

    public int getTotalDataWaitingTime() {
        return totalDataWaitingTime;
    }

    public int getTotalIdleTime() {
        return totalIdleTime;
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
            TimeUnit.MILLISECONDS.sleep((int)sleepTime/10);


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
                        Task task = queue.take();

                        resourcesStatus = ResourcesStatus.BUSY;
                        totalIdleTime += System.currentTimeMillis() - idleCnt;
                        //计算运行时间
                        long beginTime = System.currentTimeMillis();
                        task.taskReport.setBeginTime(beginTime);

                        //获取数据并计算时间
                        getData(task);
                        int dataWaitingTime = (int) (System.currentTimeMillis() - beginTime);
                        totalDataWaitingTime += dataWaitingTime;
                        task.taskReport.setDataWaitingTime(dataWaitingTime);

                        execute(task);
                        writeBack(task);
                        task.finishTask();
                        ResourcesManager.getResourcesManager().updateQueue();

                        //执行完毕，计算运行时间（后续封装）
                        totalBusyTime += System.currentTimeMillis() - beginTime;
                        task.taskReport.setFinishTime(System.currentTimeMillis());
                        //执行完毕，减去cost
                        curCost -= task.cost;
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
            totalCost += task.cost;
            curCost += task.cost;

            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getReport() {
        StringBuilder sb = new StringBuilder();

        for(DSPReport dspReport:dspReports){
            sb.append(dspReport.toString());
            sb.append('\n');
        }

        return sb.toString();
    }
}
