package com.simulator.simulator.XMLLoader.task;

import com.simulator.simulator.report.TaskReport;
import com.simulator.simulator.scheduleManager.TaskManager;
import com.simulator.simulator.timeCnter.NewTimer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;



/**
 * cost：开销
 * instCnt：个数
 * property：优先级
 * knrlType：处理器类型
 */
public class Task extends Thread{
    //taskName,knrlType,instCnt,cost,priority,dataForTask,job_inst_idx,total_size,data_inst_idx
    //任务统一属性
    public String taskName;
    public int knrlType;
    public int instCnt;
    public int cost;
    public int property;
    public int startTime = 0;
    public int job_inst_idx_inside = 0;

    //graphSchedule
    public int clusterId = -1;
    //输入数据总量
    public int dataInCnt = 0;

    //用于采集任务执行数据
    public TaskReport taskReport;

    boolean isFinish = false;
    private TaskStatus taskStatus = TaskStatus.WAIT;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    //任务实例属性
    public int job_inst_idx;

    public LinkedList<DataForTask> dataIn = new LinkedList<>();
    public LinkedList<DataForTask> dataOut = new LinkedList<>();

    //输入输出的data实例
    public List<DataInstance> dataInsIn = new LinkedList<>();
    public LinkedList<DataInstance> dataInsOut = new LinkedList<>();

    public static int id = 0;

    public int taskGraphId;
    public double graphDdl;
    public int submittedGraphId;
    public int submittedTTI;

    public Task() {
    }

    public Task(String name, int knrlType, int instCnt, int cost, int property,int job_inst_idx_inside,int graphId) {
        this.taskName = name;
        this.knrlType = knrlType;
        this.instCnt = instCnt;
        this.cost = cost;
        this.property = property;
        this.job_inst_idx_inside = job_inst_idx_inside;
        this.taskGraphId = graphId;
        job_inst_idx = id;
        id++;

        taskReport = new TaskReport(name,job_inst_idx,job_inst_idx_inside);

    }

    public Task(String name, int knrlType, int instCnt, int cost, int property,int job_inst_idx_inside,int graphId,int job_inst_idx) {
        this.taskName = name;
        this.knrlType = knrlType;
        this.instCnt = instCnt;
        this.cost = cost;
        this.property = property;
        this.job_inst_idx_inside = job_inst_idx_inside;
        this.job_inst_idx = job_inst_idx;
        this.taskGraphId = graphId;

        taskReport = new TaskReport(name,job_inst_idx,job_inst_idx_inside);
    }

    public LinkedList<DataForTask> getDataIn() {
        return dataIn;
    }

    public LinkedList<DataForTask> getDataOut() {
        return dataOut;
    }

    public void setDataIn(LinkedList<DataForTask> dataIn) {
        this.dataIn = dataIn;
    }

    public void setDataOut(LinkedList<DataForTask> dataOut) {
        this.dataOut = dataOut;
    }

    public int getJob_inst_idx() {
        return job_inst_idx;
    }

    public boolean ifFinish(){
        return isFinish;
    }

    public List<DataInstance> getDataInsIn() {
        return dataInsIn;
    }

    public LinkedList<DataInstance> getDataInsOut() {
        return dataInsOut;
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        Task task = new Task(taskName, knrlType, instCnt, cost, property, job_inst_idx_inside, taskGraphId,job_inst_idx);
        task.dataInsOut = this.dataInsOut;
        task.dataInsIn = this.dataInsIn;
        task.clusterId = this.clusterId;
        return task;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + taskName + '\'' +
                ", knrlType=" + knrlType +
                ", instCnt=" + instCnt +
                ", cost=" + cost +
                ", property=" + property +
                ", job_inst_idx=" + job_inst_idx +
                ", dataIn=" + dataIn +
                ", dataOut=" + dataOut +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Task){
            Task tmp = (Task)obj;
            return tmp.job_inst_idx == job_inst_idx && tmp.taskGraphId == taskGraphId && tmp.graphDdl == graphDdl;

        }else{
            return false;
        }
    }

    /**
     * task2 = task(2,0,3,6);
     * id,write,type,time
     */

    public String getUppTask(){
        StringBuilder sb = new StringBuilder();
        sb.append("task").append(job_inst_idx).append(" = ").append("task(");
        //for old task demo
//        sb.append(job_inst_idx+",").append(1+",").append(knrlType+",").append(10+",").append(property);
        //for new task demo
        Random r = new Random();
        sb.append(job_inst_idx+",").append(1+",").append(knrlType+",").append(10+",").append(property+",").append(r.nextInt(2)+",").append(100);

        sb.append(");");
        return sb.toString();
    }
    public String getUppTaskName(){
        StringBuilder sb = new StringBuilder();
        sb.append("task").append(job_inst_idx);
        return sb.toString();
    }

    public void finishTask(){
        long finishTime = System.currentTimeMillis() - NewTimer.getBeginTime();
//        System.out.println(this.name +  ":ins "+ this.job_inst_idx_inside +"(" + this.job_inst_idx + ")" + " finish in time:" + finishTime);
        this.isFinish = true;
    }


    @Override
    public void run() {
        try {
            //waiting for task
            sleep(this.startTime);
            //check dependency
            while(!TaskManager.getInstance().getTaskGraph(taskGraphId).checkDependency(this)){
                TimeUnit.SECONDS.sleep(1);
//                if(job_inst_idx == 12 || job_inst_idx == 3)System.out.println(job_inst_idx + " dependency not satisfy");
            }

//            TimeUnit.SECONDS.sleep(10);
//            if(job_inst_idx == 12)finishTask();

            //Enter Schedule queue
            TaskManager.getInstance().getTaskGraph(taskGraphId).schedule(this);

            //begin run

            //finish
//            TaskManager.finishTask(this.job_inst_idx);
            interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
