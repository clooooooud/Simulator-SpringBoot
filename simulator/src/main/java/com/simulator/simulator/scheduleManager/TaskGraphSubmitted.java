package com.simulator.simulator.scheduleManager;

import com.google.gson.Gson;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import com.simulator.simulator.resousce.ResourcesManager;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class TaskGraphSubmitted {
    TaskGraph taskGraph;

    /**
     * -1:未就绪
     * 0：就绪
     * 1：执行完毕
     * 2: 播报完毕
     */
    int taskGraphStatus;
    int taskGraphSubmittedId;

    /**
     * 提交时间、时限
     */
    double submitTime;
    public double submitTTI;
    public double ddl;

    /** 就绪等待时间*/
    public long waitTime = 0;

    public TaskGraphSubmitted(TaskGraph taskGraph, int id,double submitTime) {
//        this.taskGraph = new TaskGraph(taskGraph.taskDiagram,taskGraph.graphId,taskGraph.graphName,taskGraph.dependencyGraph);
//        this.taskGraph = taskGraph;

        try {
            this.taskGraph = new TaskGraph(taskGraph.taskDiagram.clone(),taskGraph.graphId,taskGraph.graphName,taskGraph.dependencyGraph);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        this.taskGraphSubmittedId = id;
        this.submitTime = submitTime;
        this.submitTTI = submitTime/50000;
        ddl = TaskManager.getInstance().getDdl()[this.taskGraph.graphId] + submitTTI;
        taskGraphStatus = -1;

        LinkedList<Task> globalTaskList = this.taskGraph.globalTaskList;
        for(int i = 0;i < globalTaskList.size();i++){
            Task t = globalTaskList.get(i);
            t.graphDdl = ddl;
            t.submittedGraphId = id;
            t.submittedTTI = (int)submitTTI;
        }

        System.out.println(taskGraph.graphName + "  提交：" + submitTTI);
    }

    public TaskGraph getTaskGraph() {
        return taskGraph;
    }

    public int getStatus() {
        return taskGraphStatus;
    }

    public int getId() {
        return taskGraphSubmittedId;
    }

    public void setTaskGraphStatus(int taskGraphStatus) {
        this.taskGraphStatus = taskGraphStatus;
    }

    public boolean checkDependency(){
        for(int id:taskGraph.dependencyGraph){
            if (!ResourcesManager.getResourcesManager().getSubmittedTaskGraph().get((int)submitTTI).containsKey(id)){
                return false;
            }
            if(ResourcesManager.getResourcesManager().getSubmittedTaskGraph().get((int)submitTTI).get(id).getStatus() != 2)return false;
        }
        return true;
    }
}
