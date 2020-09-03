package com.simulator.simulator.scheduleManager;

import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import com.simulator.simulator.scheduleAlgorithm.FIFO;
import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class TaskManager {

    static TaskDiagram taskDiagram = UppaalReadUtil.uppaalTaskReader();
    static LinkedList<Task> globalTaskList = taskDiagram.getGlobalTaskList();
    static String str = taskDiagram.getDiagramByInstance();
//    static {
//        System.out.println(str);
//    }

    static ReadWriteLock lock = new ReentrantReadWriteLock();

    public static TaskDiagram getTaskDiagram() {
        return taskDiagram;
    }

    public static LinkedList<Task> getGlobalTaskList() {
        return taskDiagram.getGlobalTaskList();
    }


    static public void finishTask(int taskId){
        lock.writeLock().lock();

        globalTaskList.get(taskId).finishTask();

        lock.writeLock().unlock();
    }


    synchronized static public boolean checkDependency(Task task){
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

    synchronized static public void schedule(Task task){
        //把任务转交给调度器
        FIFO.getInstance().enQueue(task);
    }

}
