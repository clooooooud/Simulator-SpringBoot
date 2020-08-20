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

    public static void main(String[] args) {
        TaskDiagram taskDiagram = UppaalReadUtil.uppaalTaskReader();

        LinkedList<Task> globalTaskList = taskDiagram.getGlobalTaskList();

        System.out.println(globalTaskList.get(1).job_inst_idx);
    }

    static public void finishTask(int taskId){
        lock.writeLock().lock();

        globalTaskList.get(taskId).finishTask();

        lock.writeLock().unlock();
    }


    synchronized static public boolean checkDependency(Task task){
        int taskId = task.job_inst_idx;
        LinkedList<Integer> dependencies = taskDiagram.getTaskDependencies().get(taskId);
//        if(taskId == 1){
//            for(int dependencyId : dependencies){
//                System.out.println(dependencyId);
//            }
//
//            System.out.println(task.getDataIn());
//            System.out.println("---------check task1----------");
//        }

        for(int dependencyId : dependencies){
            if(!globalTaskList.get(dependencyId).ifFinish()){
              if(task.name.equals("Task1"))System.out.println(taskId + " wait for " + dependencyId);
                return false;
            }
        }
        return true;
    }

    synchronized static public void schedule(Task task){
        //把任务转交给调度器
        FIFO.getInstance().enQueue(task);
//        System.out.println(task.job_inst_idx + "to FIFO");
    }

    @Test
    public void test01(){
        LinkedList<Integer> dependencies = taskDiagram.getTaskDependencies().get(1);
    }


}
