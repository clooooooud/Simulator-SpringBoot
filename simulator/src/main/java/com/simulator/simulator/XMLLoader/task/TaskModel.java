package com.simulator.simulator.XMLLoader.task;

import java.util.LinkedList;

public class TaskModel {
    public String name;
    public int knrlType;
    public int instCnt;
    public int cost;
    public int property;

    public LinkedList<DataForTask> dataIn = new LinkedList<>();
    public LinkedList<DataForTask> dataOut = new LinkedList<>();

    public LinkedList<DataForTask> getDataIn() {
        return dataIn;
    }

    public void setDataIn(LinkedList<DataForTask> dataIn) {
        this.dataIn = dataIn;
    }

    public LinkedList<DataForTask> getDataOut() {
        return dataOut;
    }

    public void setDataOut(LinkedList<DataForTask> dataOut) {
        this.dataOut = dataOut;
    }

    public int getModelId(){
        int id = 0;
        int cnt = 1;
        for(int i = name.length()-1;i >= 4;i--){
            id += (name.charAt(i)-'0') * cnt;
            cnt *= 10;
        }
        return  id+1;
    }

    public TaskModel(String name, int knrlType, int instCnt, int cost, int property) {
        this.name = name;
        this.knrlType = knrlType;
        this.instCnt = instCnt;
        this.cost = cost;
        this.property = property;
    }

    @Override
    public TaskModel clone() throws CloneNotSupportedException {
        return new TaskModel( name,  knrlType,  instCnt,  cost,  property);
    }

    @Override
    public String toString() {
        return "TaskModel{" +
                "name='" + name + '\'' +
                ", knrlType=" + knrlType +
                ", instCnt=" + instCnt +
                ", cost=" + cost +
                ", property=" + property +
                ", dataIn=" + dataIn +
                ", dataOut=" + dataOut +
                '}';
    }
}
