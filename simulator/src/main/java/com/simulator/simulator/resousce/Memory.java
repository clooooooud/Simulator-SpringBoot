package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.timeCnter.myTime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memory {
    Map<DataInstance,Object> map = new ConcurrentHashMap<>();
    int myClusterId;
    static int id = 0;
    int memId = 0;
    int hitTime = 0;
    int globalTime = 0;
    int transportSpeed = 10;

    DataInstance data7 = null;

    public Memory(int myClusterId) {
        this.myClusterId = myClusterId;
    }

    public boolean getData(DataInstance data) {

        //update hit time
        updateHitTime();
        //transport data
        transportData(data);

        if(!map.containsKey(data)) {
            if(data.dataName.equals("data0") || data.dataName.equals("data7") || data.dataName.equals("data9") || data.dataName.equals("data4")){

            }else{
                System.out.println(data.dataName + ":" + data.data_inst_idx + " not find");
            }
        }

        return(map.containsKey(data));
    }

    private void transportData(DataInstance data) {
        int remainBusCapacity = ResourcesManager.getResourcesManager().getRemainBusCapacity(myClusterId);
        int speed = Math.max(transportSpeed,remainBusCapacity);

        int transportTime = data.total_size / speed;

        int testTime = 0;
        updateTime(testTime);

    }

    private void updateTime(int time){
        globalTime = myTime.getTime();
        globalTime += time;
        if(globalTime > myTime.getTime()){
            myTime.setTime(globalTime);
        }
    }

    private void updateHitTime() {

        updateTime(hitTime);
    }

    public int getSize() {
        return map.size();
    }

    public int getMemoryId() {
        return memId;
    }

    public int getHitTime() {
        return hitTime;
    }

    synchronized public void save(DataInstance data) {
        transportData(data);
        map.put(data,new Object());
//        System.out.println(data.dataName+ "be saved");
    }
}
