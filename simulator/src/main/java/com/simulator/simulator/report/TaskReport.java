package com.simulator.simulator.report;

public class TaskReport {
    //任务本身信息
    String taskName;
    int instanceId;
    int instanceIdInside;

    long beginTime = 0;
    long finishTime = 0;

    int dataWaitingTime = 0;
    int executeTime = 0;

    public TaskReport(String taskName, int instanceId, int instanceIdInside) {
        this.taskName = taskName;
        this.instanceId = instanceId;
        this.instanceIdInside = instanceIdInside;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
        executeTime = (int) ((int)this.finishTime -this.beginTime);
    }

    public int getDataWaitingTime() {
        return dataWaitingTime;
    }

    public void setDataWaitingTime(int dataWaitingTime) {
        this.dataWaitingTime = dataWaitingTime;
    }

    public int getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(int executeTime) {
        this.executeTime = executeTime;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(taskName + " 实例" + instanceIdInside + "(" + instanceId + "): ");
        stringBuilder.append("数据等待时间： " + dataWaitingTime);
        stringBuilder.append(" 执行时间： " + executeTime);
        return stringBuilder.toString();
    }
}
