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
import com.simulator.simulator.scheduleAlgorithm.AlgorithmManager;
import com.simulator.simulator.scheduleAlgorithm.graphSchedule.OffChipMem;
import com.simulator.simulator.scheduleManager.TaskGraph;
import com.simulator.simulator.scheduleManager.TaskGraphSubmitted;
import com.simulator.simulator.timeCnter.NewTimer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    /** 图候选队列与执行队列 */
    List<TaskGraphSubmitted> graphList = new CopyOnWriteArrayList<>();
    List<TaskGraphSubmitted> candidateGraphList= new CopyOnWriteArrayList<>();

    /**
     * 记录已提交的graph
     *      submittedTaskGraph：存储list
     */
    List<Map<Integer,TaskGraphSubmitted>> submittedTaskGraph = new CopyOnWriteArrayList<>();
    private int submitedGraphNum = 0;

    /**
     * 将候选队列中满足依赖的图刷新进队列
     */
    public void updateGraph() {
        for(int i = 0;i < candidateGraphList.size();i++){
            TaskGraphSubmitted taskGraphSubmitted = candidateGraphList.get(i);
            if(taskGraphSubmitted.checkDependency()){
//                System.out.println("graph " + taskGraphSubmitted.getTaskGraph().graphId + "满足依赖且入队");
                taskGraphSubmitted.waitTime = System.currentTimeMillis() - NewTimer.getBeginTime();

                LinkedList<Task> globalTaskList = taskGraphSubmitted.getTaskGraph().getGlobalTaskList();
                candidateGraphList.remove(i--);

                candidateQueue.addAll(globalTaskList);
                updateQueue();
            }
        }

    }

    public List<Map<Integer,TaskGraphSubmitted>> getSubmittedTaskGraph() {
        return submittedTaskGraph;
    }

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

    /**
     * 用于获取RM的实例
     * @return RM
     */
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
     * @param taskGraph
     */
    synchronized public void submitTaskGraph(TaskGraph taskGraph){

        TaskGraphSubmitted taskGraphSubmitted = new TaskGraphSubmitted(taskGraph, submitedGraphNum++, ((double)System.currentTimeMillis()-(double)NewTimer.getBeginTime()));

        //        for (Task t:taskGraphSubmitted.getTaskGraph().getGlobalTaskList()){
//            System.out.println(t.graphDdl);
//        }

        TaskDiagram taskDiagram = taskGraphSubmitted.getTaskGraph().getTaskDiagram();
        LinkedList<Task> globalTaskList = taskDiagram.getGlobalTaskList();

//        System.out.println("before 11");

        //片外访存
//        schedule(taskDiagram,clusterList.size());
//        System.out.println(11);

        if(submittedTaskGraph.size() > (int) taskGraphSubmitted.submitTTI){
            Map<Integer,TaskGraphSubmitted> taskGraphSubmittedInTTI = submittedTaskGraph.get((int) taskGraphSubmitted.submitTTI);
            taskGraphSubmittedInTTI.put(taskGraph.graphId,taskGraphSubmitted);
        }else{
            Map<Integer,TaskGraphSubmitted> tmp = new ConcurrentHashMap<>();
            tmp.put(taskGraph.graphId,taskGraphSubmitted);
            submittedTaskGraph.add(tmp);
        }

        //图加入候选图队列
        candidateGraphList.add(taskGraphSubmitted);
        //更新任务队列
        updateGraph();

        //待调度队列入队
//        candidateQueue.addAll(globalTaskList);
//        updateQueue();

        //qos保障
//        for(Cluster cluster:clusterList){
//            for(DSP dsp:cluster.dspList){
//                dsp.qosSort();
//            }
//        }
    }

    /**
     * 片外访存调度，接收整个图后分配cluster
     * @param taskDiagram
     * @param clusterNum
     */
    public void schedule(TaskDiagram taskDiagram, int clusterNum){
        OffChipMem offChipMem = new OffChipMem();
//        offChipMem.schedule(taskDiagram,clusterNum);
        offChipMem.schedule(taskDiagram,clusterNum);
    }

    synchronized public void updateQueue(){
        System.out.println(candidateQueue.size() + "候选队列长度");
         for(Task task:candidateQueue){
             if(!task.getTaskStatus().equals(TaskStatus.WAIT))continue;
             if(submittedTaskGraph.get(task.submittedTTI).get(task.taskGraphId).getTaskGraph().checkDependency(task)){
//                 System.out.println(task.submittedGraphId);
                 queue.add(task);
                 candidateQueue.remove(task);
                 task.setTaskStatus(TaskStatus.EXECUTE);
             }
         }
    }

    int indexTest = 0;
    private void execute(Task task){
        Cluster cluster = null;
        switch (AlgorithmManager.resourceManageAlgorithmId){
            case 0:
                //轮训
                cluster = clusterList.get((indexTest++)%16);
                cluster.submit(task);
                break;
            case 1:
                //片外访存
                cluster = clusterList.get(task.clusterId);
                cluster.submit(task);
                break;
            case 2:
                //贪心
                int[] clusterCnt = new int[clusterList.size()];

                for(DataInstance d:task.getDataInsIn()){
                    for(Cluster c:clusterList){
                        if(c.checkData(d)){
                            clusterCnt[c.clusterId] += d.total_size;
                            break;
                        }
                    }
                }
                int tmpIdx = -1,tmpSize = 0;
                for(int i = 0;i < clusterList.size();i++){
                    if(clusterCnt[i] > tmpSize){
                        tmpSize = clusterCnt[i];
                        tmpIdx = i;
                    }
                }
                if(tmpIdx == -1)tmpIdx = (indexTest++)%2;

                cluster = clusterList.get(tmpIdx);
                cluster.submit(task);
            default:
                if(cluster == null) System.out.println("请指定算法");
        }





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
        components.clear();
        //重置cluster，DSP，DMA，Mem编号
        Cluster.ID = 0;Memory.id = 0;DSP.id=0;DMA.id = 0;
        for (int i = 0; i < num; i++) {
            clusterList.add(new Cluster(componentStructure.getMap().get("SUB_SYS")));
        }

        for(Cluster cluster: clusterList){
            components.addAll(cluster.getDmaList());
            components.addAll(cluster.getDspList());
            components.addAll(cluster.getFpgaList());
        }

        System.out.println("finish setClusterNum");
        for(Cluster cluster:clusterList){
            System.out.println(cluster);
        }
    }
}
