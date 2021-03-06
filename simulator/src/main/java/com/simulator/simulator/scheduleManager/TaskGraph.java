package com.simulator.simulator.scheduleManager;

import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.scheduleAlgorithm.AlgorithmManager;
import com.simulator.simulator.scheduleAlgorithm.FIFO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 单个图实例
 */
public class TaskGraph {

    TaskDiagram taskDiagram;
    LinkedList<Task> globalTaskList;

    public int graphId;
    public double submitTime;
    public String graphName;

    public void setId(int id){
        this.graphId = id;
    }
    public void setSubmitTime(double submitTime){
        this.submitTime = submitTime;
    }

    public LinkedList<Integer> dependencyGraph = new LinkedList<>();

    public TaskGraph(TaskDiagram taskDiagram, int graphId, String graphName,LinkedList<Integer> dependencyGraph) {
        this.taskDiagram = taskDiagram;
        globalTaskList = taskDiagram.getGlobalTaskList();
        //生产taskDiagram的依赖表
        taskDiagram.getDiagramByInstance();




        this.graphId = graphId;
        this.graphName = graphName;
        this.dependencyGraph = dependencyGraph;
    }

    public TaskGraph(TaskDiagram taskDiagram, int graphId, String graphName,LinkedList<Integer> dependencyGraph, HashMap<DataInstance, Integer> outMap) {
        this.taskDiagram = taskDiagram;
        globalTaskList = taskDiagram.getGlobalTaskList();
        //生产taskDiagram的依赖表
        taskDiagram.getDiagramByInstanceAndMap(outMap);
        if(AlgorithmManager.resourceManageAlgorithmId == 1){
            ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();
            resourcesManager.schedule(taskDiagram,resourcesManager.getClusterList().size());
        }

        this.graphId = graphId;
        this.graphName = graphName;
        this.dependencyGraph = dependencyGraph;
    }

    /**
     * flag标志是否提供给dependencyGraph
     * @param taskDiagram
     * @param graphId
     * @param graphName
     * @param dependencyGraph
     * @param flag
     */
    public TaskGraph(TaskDiagram taskDiagram, int graphId, String graphName,LinkedList<Integer> dependencyGraph,boolean flag) {
        this.taskDiagram = taskDiagram;
        globalTaskList = taskDiagram.getGlobalTaskList();

        this.graphId = graphId;
        this.graphName = graphName;
        this.dependencyGraph = dependencyGraph;
    }

    static ReadWriteLock lock = new ReentrantReadWriteLock();

    public TaskDiagram getTaskDiagram() {
        return taskDiagram;
    }

    public LinkedList<Task> getGlobalTaskList() {
        return taskDiagram.getGlobalTaskList();
    }


    public void finishTask(int taskId){
        lock.writeLock().lock();

        globalTaskList.get(taskId).finishTask();

        lock.writeLock().unlock();
    }


    synchronized public boolean checkDependency(Task task){
        int taskId = task.job_inst_idx;
//        System.out.println(taskDiagram.getTaskDependencies().size());
        LinkedList<Integer> dependencies = taskDiagram.getTaskDependencies().get(taskId);

//        taskDiagram.report();
//        for(int p = 0;p < getGlobalTaskList().size();p++){
//            Task task1 = getGlobalTaskList().get(p);
//            if(task1.name.equals("Task2")){
//                LinkedList<Integer> dependencies1 = taskDiagram.getTaskDependencies().get(task1.getJob_inst_idx());
//                for(int dependencyId:dependencies1){
//                    System.out.println(task1.name + "|"+task1.job_inst_idx_inside + " wait for " + getGlobalTaskList().get(dependencyId).name+"("+getGlobalTaskList().get(dependencyId).job_inst_idx_inside +")");
//                }
//            }
//        }

        for(int dependencyId : dependencies){
            if(!globalTaskList.get(dependencyId).ifFinish()){
//              if(task.name.equals("Task2"))System.out.println(task.name + "|"+taskId + " wait for " + globalTaskList.get(dependencyId).name+"("+globalTaskList.get(dependencyId).job_inst_idx_inside +")");
//                System.out.println(task.taskName + "|"+taskId + " wait for " + globalTaskList.get(dependencyId).taskName+"("+globalTaskList.get(dependencyId).job_inst_idx_inside +")");
                return false;
            }
        }
        return true;
    }

    synchronized public void schedule(Task task){
        //把任务转交给调度器
        FIFO.getInstance().enQueue(task);
    }

    public void setDependency(int b) {
        dependencyGraph.add(b);
    }


}
