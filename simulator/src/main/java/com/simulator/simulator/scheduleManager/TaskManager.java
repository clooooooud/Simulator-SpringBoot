package com.simulator.simulator.scheduleManager;

import com.google.gson.internal.$Gson$Preconditions;
import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.timeCnter.GraphGenerator;
import com.simulator.simulator.timeCnter.NewTimer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 管理当前的所有任务图，不一定所有都被提交，有的可能被重复提交
 */
public class TaskManager {

    /** graph集合 */
    LinkedList<TaskGraph> graphList= new LinkedList<>();
    /** 图生产工厂*/
    List<Queue<TaskGraph>> graphFactory = new CopyOnWriteArrayList<>();

    /**
     * period:图的周期
     */
    double[] period;
    /** 图的下一个播报时间 */
    double[] nextTime;
    double[] ddl;

    private static TaskManager taskGraph = null;

    public TaskManager() {
        graphList = UppaalReadUtil.uppaalTaskReader();
        System.out.println("finish load");
        period = new double[graphList.size()];
        nextTime = new double[graphList.size()];

        for(int i = 0;i < graphList.size();i++){
            nextTime[i] = 0;
            Queue<TaskGraph> q = new LinkedBlockingQueue<>();
            graphFactory.add(q);
        }
        System.out.println("d"+graphFactory.size());
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        threadPool.execute(new GraphGenerator(graphList, graphFactory));
    }

    public boolean isReady(){
        for(int i = 0;i < graphFactory.size();i++){
            if(graphFactory.get(i).size() == 0) return false;
        }
        return true;
    }
    public double[] getDdl() {
        return ddl;
    }

    public void setDdl(double[] ddl) {
        this.ddl = ddl;
    }

    public void setPeriod(double[] period) {
        this.period = period;
    }

    public static TaskManager getInstance(){
        if(taskGraph == null){
            synchronized (TaskManager.class){
                if(taskGraph == null){
                    taskGraph = new TaskManager();
                }
            }
        }
        return taskGraph;
    }

    public TaskGraph getTaskGraph(int id){
        return graphList.get(id);
    }

    public void updateTask() {
        //更新任务
        for (int i = 0; i < graphList.size(); i++) {
            if(nextTime[i] < NewTimer.curTTI){
                //ResourcesManager.getResourcesManager().submitTaskGraph(graphList.get(i));
                ResourcesManager.getResourcesManager().submitTaskGraph(graphFactory.get(i).poll());
                nextTime[i] += period[i];
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("任务图状态（单位：/DSP*TTI："+"\n");
        for (TaskGraph taskGraph:graphList){
            double sumCost = 0;
            for (Task task:taskGraph.getGlobalTaskList()){
                sumCost += task.cost;
            }
            stringBuilder.append("图：" + taskGraph.graphId + " 任务量: "+ (sumCost/650000) + "\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 图a依赖于图b
     * @param a
     * @param b
     */
    public void setDependency(int a,int b) {
        graphList.get(a).setDependency(b);
    }
}
