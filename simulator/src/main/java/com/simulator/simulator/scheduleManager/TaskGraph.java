package com.simulator.simulator.scheduleManager;

import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import com.simulator.simulator.scheduleAlgorithm.FIFO;
import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 单个图实例
 */
public class TaskGraph {

    TaskDiagram taskDiagram;
    LinkedList<Task> globalTaskList;
    String str;

    public int graphId;
    String graphName;

    public TaskGraph(TaskDiagram taskDiagram, int graphId, String graphName) {
        this.taskDiagram = taskDiagram;
        globalTaskList = taskDiagram.getGlobalTaskList();
        taskDiagram.getDiagramByInstance();

        this.graphId = graphId;
        this.graphName = graphName;
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
                return false;
            }
        }
        return true;
    }

    synchronized public void schedule(Task task){
        //把任务转交给调度器
        FIFO.getInstance().enQueue(task);
    }

}
