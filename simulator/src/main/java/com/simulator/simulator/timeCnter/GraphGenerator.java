package com.simulator.simulator.timeCnter;

import com.simulator.simulator.scheduleManager.TaskGraph;
import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class GraphGenerator extends Thread{
    public LinkedList<TaskGraph> graphList;

    public LinkedList<Queue<TaskGraph>> graphFactory;
    public GraphGenerator(LinkedList<TaskGraph> gL, LinkedList<Queue<TaskGraph>> gF){
        graphList = gL;
        graphFactory = gF;
    }

    @Override
    public void run() {
        while(true){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                for(int i = 0;i < graphList.size();i++){
                    try{
                        TaskGraph taskGraph = graphList.get(i);
                        TaskGraph newTaskGraph = new TaskGraph(taskGraph.getTaskDiagram().clone(),taskGraph.graphId,taskGraph.graphName,taskGraph.dependencyGraph);
                        //System.out.println("Factory generation: "+i);
                        this.graphFactory.get(i).offer(newTaskGraph);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}