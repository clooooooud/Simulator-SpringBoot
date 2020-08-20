package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.System.Component;
import com.simulator.simulator.XMLLoader.System.ComponentStructure;
import com.simulator.simulator.XMLLoader.System.MainSystem;
import com.simulator.simulator.XMLLoader.System.SubSystem;
import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.XMLLoader.task.Task;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResourcesManager extends Thread{

    DSP[] dsps;
    private static ResourcesManager resourcesManager;
    List<Cluster> clusterList = new CopyOnWriteArrayList<>();
    List<Thread> components = new ArrayList<>();
    static ComponentStructure componentStructure = UppaalReadUtil.uppaalComponentReader();
    static {
        System.out.println(componentStructure);
    }

    Queue<Task> queue = new ConcurrentLinkedQueue<>();

     public void getDate(int myClusterId, Task task) {
        Cluster cluster = clusterList.get(myClusterId);
        cluster.getData(task);
    }

    public void writeBack(int myClusterId, Task task) {
        Cluster cluster = clusterList.get(myClusterId);
        cluster.saveData(task);
    }

    public DSP[] getDsps() {
        return dsps;
    }

    ResourcesManager(ComponentStructure componentStructure){
        //top system,equals to resourceManager
        MainSystem mainSystem = componentStructure.getMainSystem();
        Map<String, SubSystem> map = componentStructure.getMap();

        //component.type means the name of subsystem
        List<Component> componentList = mainSystem.getComponentList();

        for(Component component:componentList){
            SubSystem subSystem = map.get(component.getType());
            clusterList.add(new Cluster(subSystem));
        }

        for(Cluster cluster: clusterList){
            components.addAll(cluster.getDmaList());
            components.addAll(cluster.getDspList());
            components.addAll(cluster.getFpgaList());
        }
    }

    public void dmaSave(int clusterId, DataInstance data){
        Cluster cluster = clusterList.get(clusterId);
        cluster.dmaSave(data);
    }

//    private ResourcesManager(int dspNum){
//        dsps = new DSP[dspNum];
//        Cluster cluster = new Cluster();
//        for (int i = 0; i < dspNum; i++) {
//            dsps[i] = new DSP(cluster);
//        }
//        cluster.setDspList(Arrays.asList(dsps));
//
//        Memory memory = new Memory();
//        memory.myCluster = cluster;
//        cluster.memoryList.add(memory);
//
//
//        queue = new ConcurrentLinkedQueue<>();
//    }

    public static ResourcesManager getResourcesManager() {
        if(resourcesManager == null){
            synchronized(ResourcesManager.class){
                if(resourcesManager == null){

                    resourcesManager = new ResourcesManager(componentStructure);
                }
            }
        }
        return resourcesManager;
    }

    public void submit(Task task){

        queue.add(task);
    }

    int indexTest = 0;
    private void execute(Task task){
//        Arrays.sort(dsps,(d1,d2)->{
//            return d1.getQueue().size() - d2.getQueue().size();
//        });
//
//        dsps[0].submit(task);

//        Cluster cluster = clusterList.get((indexTest++)%2);
        Cluster cluster = clusterList.get(0);
        cluster.submit(task);

    }

    @Override
    public void run() {
        while(true){
            while(!queue.isEmpty()){
                Task task = queue.remove();
                execute(task);
            }
        }
    }

    public int getRemainBusCapacity(int myClusterId) {
        Cluster cluster = clusterList.get(myClusterId);
        return cluster.getRemainBusCapacity();
    }

    public List<Thread> getComponents() {
        return components;
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }
}
