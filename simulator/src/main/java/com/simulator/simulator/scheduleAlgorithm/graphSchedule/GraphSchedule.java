package com.simulator.simulator.scheduleAlgorithm.graphSchedule;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;

import java.util.LinkedList;

public interface GraphSchedule {
    void schedule(TaskDiagram taskDiagram,int clusterNum);
}
