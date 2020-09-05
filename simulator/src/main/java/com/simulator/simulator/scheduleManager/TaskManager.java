package com.simulator.simulator.scheduleManager;

import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.resousce.ResourcesManager;

import java.util.LinkedList;

/**
 * 管理当前的所有任务图，不一定所有都被提交，有的可能被重复提交
 */
public class TaskManager {

    /** graph集合 */
    LinkedList<TaskGraph> graphList= new LinkedList<>();

    /**
     * period:图的周期
     */
    int[] period;
    int[] curRound;

    private static TaskManager taskGraph = null;

    public TaskManager() {
        graphList = UppaalReadUtil.uppaalTaskReader();
        period = new int[graphList.size()];
        curRound = new int[graphList.size()];
    }

    public void setPeriod(int[] period) {
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
        //更新TTI
        for (int i = 0; i < graphList.size(); i++) {
            curRound[i]++;
        }

        //更新任务
        for (int i = 0; i < graphList.size(); i++) {
            if(curRound[i] >= period[i]){
                curRound[i] = 0;
                ResourcesManager.getResourcesManager().submitTaskGraph(graphList.get(i).taskDiagram);
            }
        }
    }
}
