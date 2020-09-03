package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.report.DMAReport;
import com.simulator.simulator.report.DSPReport;
import com.simulator.simulator.report.ReportInterFace;
import com.simulator.simulator.timeCnter.myTime;

import java.util.LinkedList;
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

public class DMA extends Thread implements ReportInterFace {
    BlockingQueue<DMATask> taskQueue = new LinkedBlockingDeque<>();
    BlockingQueue<DMATask> taskSubmitted = new LinkedBlockingDeque<>();
    int dmaSpeed = 500;
    boolean run = true;

    public LinkedList<DMAReport> dmaReports = new LinkedList<>();
    public int totalDmaSize = 0;
    public int curSize = 0;

    int globalTime = 0;
    int activeTime = 0;

    static int id = 0;
    private int dmaId = 0;
    private int myClusterId = 0;

    synchronized public void submit(DataInstance data, int src, int target)  {
        DMATask dmaTask = new DMATask(data,src,target);
        totalDmaSize += data.total_size;
        curSize += data.total_size;

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
                    TimeUnit.MILLISECONDS.sleep(executeTime);
                    ResourcesManager.getResourcesManager().dmaSave(myClusterId,dmaTask.data);
//                    System.out.println("save:" + dmaTask.data.dataName);

                    //Refresh time
//                    globalTime += executeTime;
                    globalTime += testTime;
                    activeTime += testTime;

                    if(globalTime > myTime.getTime()) myTime.setTime(globalTime);

                    //执行完毕，减去
                    curSize -= dmaTask.data.total_size;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkIfTaskSubmitted(DataInstance DataInstance) {
        return taskQueue.contains(new DMATask(DataInstance,0,0));
    }

    @Override
    public String getReport() {
        StringBuilder sb = new StringBuilder();

        for(DMAReport dmaReport:dmaReports){
            sb.append(dmaReport.toString());
            sb.append('\n');
        }

        return sb.toString();
    }
}
