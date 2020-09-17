package com.simulator.simulator.check;

import com.simulator.simulator.report.ReportUtil;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.scheduleManager.TaskGraphSubmitted;
import com.simulator.simulator.scheduleManager.TaskUtils;
import com.simulator.simulator.timeCnter.NewTimer;

import java.util.List;
import java.util.Map;

public class CheckUtil {
    /**
     * accessGraphNum：接收的任务数量
     * denyGraphNum：拒绝的任务数
     * accessRate：接受率
     */
    public static int accessGraphNum = 0;
    public static int denyGraphNum = 0;
    public static double accessRate = 1;

    public static void checkGraph(){
        List<Map<Integer, TaskGraphSubmitted>> submittedTaskGraph = ResourcesManager.getResourcesManager().getSubmittedTaskGraph();
        for(Map<Integer,TaskGraphSubmitted> taskGraphSubmittedInTTI:submittedTaskGraph){
            for(Integer id:taskGraphSubmittedInTTI.keySet()){
                TaskGraphSubmitted taskGraphSubmitted = taskGraphSubmittedInTTI.get(id);
                if(taskGraphSubmitted.getStatus() != 2 && TaskUtils.graphFinish(taskGraphSubmitted)){
                    if(NewTimer.curTTI < taskGraphSubmitted.ddl){
                        accessGraphNum++;
                    }else {
                        denyGraphNum++;
                    }
                    accessRate = 1 - ((double)denyGraphNum/(denyGraphNum + accessGraphNum));

                    ReportUtil.graphReport(taskGraphSubmitted);
                    taskGraphSubmittedInTTI.get(id).setTaskGraphStatus(2);
                    ResourcesManager.getResourcesManager().updateGraph();
                }
            }
        }
    }
}
