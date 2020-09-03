package com.simulator.simulator.scheduleAlgorithm.graphSchedule;

import com.simulator.simulator.XMLLoader.task.DataForTask;
import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;

import java.util.*;

class ClusterNode{
    int totalsize = 0;
    int load = 0;
    int id = 0;
    static int idCnt = 0;

    public ClusterNode() {
        totalsize = 0;
        id = idCnt++;
    }
}

public class OffChipMem implements GraphSchedule{
    private TaskDiagram taskDiagram = null;
    private int clusterNum = 0;
    ClusterNode[] clusterDataCnt = null;
    Map<Integer,ClusterNode> clusterMap = new HashMap<>();

    int cnt = 0;

    @Override
    public void schedule(TaskDiagram taskDiagram,int clusterNum) {
        this.taskDiagram = taskDiagram;
        this.clusterNum = clusterNum;
        this.clusterDataCnt = new ClusterNode[clusterNum];
        for(int i = 0;i < clusterNum;i++){
            ClusterNode clusterNode = new ClusterNode();
            clusterDataCnt[i] = clusterNode;
            clusterMap.put(i,clusterNode);
        }

        LinkedList<Task> globalTaskList = new LinkedList<>();
        for(Task t:taskDiagram.getGlobalTaskList()){
            globalTaskList.add(t);
        }


        globalTaskList.sort((t1, t2) -> {
            int dataIn1 = 0, dataIn2 = 0;
            for (DataInstance d : t1.getDataInsIn()) dataIn1 += d.total_size;
            for (DataInstance d : t2.getDataInsIn()) dataIn2 += d.total_size;

            //大到小
            return dataIn2 - dataIn1;
        });

        for(int i = 0;i < globalTaskList.size();i++){
            if(globalTaskList.get(i).clusterId != -1)continue;
            setCluster(globalTaskList.get(i));
        }

    }

    private int setCluster(Task task){
        if(task.clusterId != -1)return task.clusterId;

        LinkedList<Integer> dependentTask = taskDiagram.getTaskDependencies().get(task.getJob_inst_idx());
        LinkedList<DataInstance> dataIn = task.getDataInsIn();
        LinkedList<Task> globalTaskList  = taskDiagram.getGlobalTaskList();

        clearClusterNode();
        for(DataInstance dataInstance:dataIn){
            Task producer = null;
            for(int taskId:dependentTask){
                Task tmp = globalTaskList.get(taskId);
                if(tmp.getDataInsOut().contains(dataInstance)){
                    producer = tmp;
                    break;
                }
            }
            //没有生产者
            if(producer == null)continue;
            //获取数据生产者的cluster
            int proCluster = setCluster(producer);
            clusterMap.get(proCluster).totalsize += dataInstance.total_size;
        }

        Arrays.sort(clusterDataCnt,(c1,c2)->{
            if(c1.load > c2.load * 3)return 1;
            else if(c2.load > c1.load * 3)return -1;

            if(c1.totalsize != c2.totalsize){
                return c2.totalsize - c1.totalsize;
            }else if(c1.load != c2.load){
                return c1.load - c2.load;
            }else{
                return (cnt++)%2;
            }
        });

        task.clusterId = clusterDataCnt[0].id;
        clusterMap.get(task.clusterId).load += task.cost;
        return task.clusterId;

    }

    private void clearClusterNode(){
        for(int i = 0;i < clusterDataCnt.length;i++){
            clusterDataCnt[i].totalsize = 0;
        }
    }


}
