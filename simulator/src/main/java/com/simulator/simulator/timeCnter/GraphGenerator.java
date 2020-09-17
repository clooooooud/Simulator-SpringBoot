package com.simulator.simulator.timeCnter;

import com.simulator.simulator.scheduleManager.TaskGraph;
import com.simulator.simulator.scheduleManager.TaskManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class GraphGenerator extends Thread{
    public List<TaskGraph> graphList;

    public List<Queue<TaskGraph>> graphFactory;
    public GraphGenerator(LinkedList<TaskGraph> gL, List<Queue<TaskGraph>> gF){
        System.out.println("工厂启动");
        graphList = gL;
        graphFactory = gF;
    }

    @Override
    public void run() {
        while(true){
            try {
                TimeUnit.SECONDS.sleep(1);
                for(int i = 0;i < graphList.size();i++){
                    try{
                        if(this.graphFactory.get(i).size() >= 5)continue;
                        TaskGraph taskGraph = graphList.get(i);
                        TaskGraph newTaskGraph = new TaskGraph(taskGraph.getTaskDiagram().clone(),taskGraph.graphId,taskGraph.graphName,taskGraph.dependencyGraph,true);
                        //System.out.println("Factory generation: "+i);
                        this.graphFactory.get(i).offer(newTaskGraph);
//                        System.out.println("图"+i+"生产完毕");
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