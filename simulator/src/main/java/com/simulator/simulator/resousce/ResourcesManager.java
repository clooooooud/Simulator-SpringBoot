package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.System.Component;
import com.simulator.simulator.XMLLoader.System.ComponentStructure;
import com.simulator.simulator.XMLLoader.System.MainSystem;
import com.simulator.simulator.XMLLoader.System.SubSystem;
import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import com.simulator.simulator.XMLLoader.task.TaskStatus;
import com.simulator.simulator.scheduleAlgorithm.graphSchedule.OffChipMem;
import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResourcesManager extends Thread{

    private static ResourcesManager resourcesManager;
    List<Cluster> clusterList = new CopyOnWriteArrayList<>();

    /**
     * 用于启动component
     */
    List<Thread> components = new ArrayList<>();
    static ComponentStructure componentStructure = UppaalReadUtil.uppaalComponentReader();

    Queue<Task> queue = new ConcurrentLinkedQueue<>();
    Queue<Task> candidateQueue= new ConcurrentLinkedQueue<>();

     public void getDate(int myClusterId, Task task) {
        Cluster cluster = clusterList.get(myClusterId);
        cluster.getData(task);
    }

    public void writeBack(int myClusterId, Task task) {
        Cluster cluster = clusterList.get(myClusterId);
        cluster.saveData(task);
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

    /**
     * 接收提交的任务
     * @param taskDiagram
     */
    public void submitTaskGraph(TaskDiagram taskDiagram){
        LinkedList<Task> globalTaskList = taskDiagram.getGlobalTaskList();
        System.out.println(clusterList.size());
        schedule(taskDiagram,clusterList.size());

        //待调度队列入队
        candidateQueue.addAll(globalTaskList);
        updateQueue();
    }

    /**
     * 片外访存调度，接收整个图后分配cluster
     * @param taskDiagram
     * @param clusterNum
     */
    private void schedule(TaskDiagram taskDiagram,int clusterNum){
        OffChipMem offChipMem = new OffChipMem();
//        offChipMem.schedule(taskDiagram,clusterNum);
        offChipMem.schedule(taskDiagram,clusterNum);
    }

    public void updateQueue(){
         for(Task task:candidateQueue){
             if(!task.getTaskStatus().equals(TaskStatus.WAIT))continue;
             if(TaskManager.getInstance().getTaskGraph(task.graphId).checkDependency(task)){
                 queue.add(task);
                 task.setTaskStatus(TaskStatus.EXECUTE);
             }
         }
    }

    int indexTest = 0;
    private void execute(Task task){

        //负载均衡
//        Arrays.sort(dsps,(d1,d2)->{
//            return d1.getQueue().size() - d2.getQueue().size();
//        });
//
//        dsps[0].submit(task);

        //轮训
        Cluster cluster = clusterList.get((indexTest++)%2);

        //全部在1
//        Cluster cluster = clusterList.get(0);

        //片外访存
//        Cluster cluster = clusterList.get(task.clusterId);
        cluster.submit(task);

        //贪心
//        int[] clusterCnt = new int[clusterList.size()];
//
//        for(DataInstance d:task.getDataInsIn()){
//            for(Cluster c:clusterList){
//                if(c.checkData(d)){
//                    clusterCnt[c.clusterId] += d.total_size;
//                    break;
//                }
//            }
//        }
//
//        int tmpIdx = -1,tmpSize = 0;
//        for(int i = 0;i < clusterList.size();i++){
//            if(clusterCnt[i] > tmpSize){
//                tmpSize = clusterCnt[i];
//                tmpIdx = i;
//            }
//        }
//        if(tmpIdx == -1)tmpIdx = (indexTest++)%2;
//
//        Cluster cluster = clusterList.get(tmpIdx);
//        cluster.submit(task);

    }

    @Override
    public void run() {
        while(true){
            while(!queue.isEmpty()){
                Task task = queue.remove();
                execute(task);
            }
//            updateQueue();
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

    /**
     * 将Resources中cluster数量增加到num。subSystem类型默认（“SUB_SYS”）
     * @param num
     */
    public void setClusterNum(int num) {
        clusterList.clear();
        for (int i = 0; i < num; i++) {
            clusterList.add(new Cluster(componentStructure.getMap().get("SUB_SYS")));
        }

        for(Cluster cluster: clusterList){
            components.addAll(cluster.getDmaList());
            components.addAll(cluster.getDspList());
            components.addAll(cluster.getFpgaList());
        }

    }
}
