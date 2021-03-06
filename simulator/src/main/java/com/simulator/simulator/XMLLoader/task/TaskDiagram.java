package com.simulator.simulator.XMLLoader.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TaskDiagram {

    //任务实例列表
    public LinkedList<Task> GlobalTaskList = new LinkedList<>();
    //任务模板列表
    LinkedList<TaskModel> modelTaskList = new LinkedList<>();
    //data模板列表
    LinkedList<Data> dataModelList = new LinkedList<>();
    //task依赖列表
    public LinkedList<LinkedList<Integer>> taskDependencies = new LinkedList<>();

    public TaskDiagram(LinkedList<Task> globalTaskList, LinkedList<TaskModel> modelTaskList, LinkedList<Data> dataModelList) {
        GlobalTaskList = globalTaskList;
        this.modelTaskList = modelTaskList;
        this.dataModelList = dataModelList;
    }

    @Override
    public TaskDiagram clone() throws CloneNotSupportedException {
        LinkedList<Task> globalTaskListClone = new LinkedList<>();
        for(Task t:GlobalTaskList){
            globalTaskListClone.add(t.clone());
        }

        LinkedList<TaskModel> modelTaskListClone = new LinkedList<>();
        for(TaskModel t:modelTaskList){
            modelTaskListClone.add(t.clone());
        }

        LinkedList<Data> dataModelListClone = new LinkedList<>();
        for(Data d:dataModelList){
            dataModelListClone.add(d.clone());
        }

        TaskDiagram taskDiagram = new TaskDiagram(globalTaskListClone, modelTaskListClone, dataModelListClone);
        taskDiagram.taskDependencies = this.taskDependencies;

        return taskDiagram;
    }

    public LinkedList<LinkedList<Integer>> getTaskDependencies() {
        return taskDependencies;
    }

    public LinkedList<Task> getGlobalTaskList() {
        return GlobalTaskList;
    }

    public LinkedList<TaskModel> getModelTaskList() {
        return modelTaskList;
    }

    public LinkedList<Data> getDataModelList() {
        return dataModelList;
    }
    public String getDiagramByModel(){
        //以model为单位构建dag
        int size = modelTaskList.size();
        int[][] modelDag = new int[size+1][size+1];
        for(int i = 0;i <size;i++){
            int index = 0;
            TaskModel taskModel = modelTaskList.get(i);
            LinkedList<DataForTask> dataIn = taskModel.getDataIn();
            for(DataForTask d : dataIn){
                for(Data dm :dataModelList){
                    if(d.dataName.equals(dm.name)){
                        modelDag[taskModel.getModelId()][index] = dm.getProducerId();
                        index++;
                    }
                }
            }
        }

        //输出mdelDag
        StringBuilder modelDAG = new StringBuilder();
        modelDAG.append("{").append('\n');
        int taskModelNum;
        taskModelNum = modelTaskList.size();
        for(int i = 0;i <taskModelNum + 1;i++){
            StringBuilder sb = new StringBuilder();
            sb.append("{{");
            for(int j = 0;j < taskModelNum +1;j++){
                if(j != taskModelNum){
                    sb.append(modelDag[i][j]).append(",");
                }else {
                    sb.append(modelDag[i][j]);

                }
            }
            if(i != taskModelNum)sb.append("}},");
            else sb.append("}}");
            modelDAG.append(sb).append('\n');
        }
        modelDAG.append("};").append('\n');

        return modelDAG.toString();
    }

    public String getDiagramByInstance(){
        /**
         * 以实例为单位构建dag
         */
        int taskNum = GlobalTaskList.size();
//        int[][] dag = new int[taskNum+1][taskNum+1];
//        LinkedList<LinkedList<Integer>> dag = new LinkedList<>();

        System.out.println("生成dag" + taskNum);
        for(int i = 0;i < taskNum;i++){
            Task task = GlobalTaskList.get(i);
            //dependency of task i
            LinkedList<Integer> singleTaskDependenciesList = new LinkedList<>();
            //标记依赖有几个了
//            int index = 0;

            //task的输入datains
            List<DataInstance> dataIn = task.getDataInsIn();
//            System.out.println(dataIn.size());
            for(DataInstance d:dataIn){
                for(int j = 0;j < taskNum;j++){
//                    System.out.println(j);
                    if(i == j)continue;
                    int dependencyId = GlobalTaskList.get(j).getJob_inst_idx();
                    //任务输出的数据实例
                    LinkedList<DataInstance> taskDataInsOut = GlobalTaskList.get(j).getDataInsOut();
                    if(taskDataInsOut.contains(d)){
                        //i依赖j
//                        dag[task.getJob_inst_idx()][index] = dependencyId;
//                        index++;

                        //save in taskDependencies
                        singleTaskDependenciesList.add(dependencyId);
//                        if(task.name.equals("Task2") && GlobalTaskList.get(dependencyId).name.equals("Task8")){
//                            System.out.println(d.dataName);
//                        }
                    }
                }
            }
            taskDependencies.add(singleTaskDependenciesList);
            System.out.println("生成序号" + i);
        }

//        taskInstanceDag.append("{").append('\n');
//        for(int i = 0;i <taskNum + 1;i++){
//            StringBuilder sb = new StringBuilder();
//            sb.append("{{");
//            for(int j = 0;j < taskNum +1;j++){
//                if(j != taskNum){
//                    sb.append(dag[i][j]).append(",");
//                }else {
//                    sb.append(dag[i][j]);
//                    if(i != taskNum)sb.append("}},");
//                    else sb.append("}}");
//                }
//            }
//            taskInstanceDag.append(sb).append('\n');
//        }
//        taskInstanceDag.append("};").append('\n');

        StringBuilder taskInstanceDag = new StringBuilder();
        System.out.println("生成dag结束" + taskNum);
        return "图塞不下";
    }

    public String getDiagramByInstanceAndMap(HashMap<DataInstance, Integer> outMap){
        /**
         * 以实例为单位构建dag
         */
        int taskNum = GlobalTaskList.size();
        LinkedList<LinkedList<Integer>> dag = new LinkedList<>();

        System.out.println("生成dag" + taskNum);
        for(int i = 0;i < taskNum;i++){
            Task task = GlobalTaskList.get(i);
            LinkedList<Integer> singleTaskDependenciesList = new LinkedList<Integer>();
            List<DataInstance> dataIn = task.getDataInsIn();
            for(DataInstance d:dataIn){
                if(outMap.containsKey(d)) {
                    int dependencyId = outMap.get(d);
                    singleTaskDependenciesList.add(dependencyId);
                }
            }
            taskDependencies.add(singleTaskDependenciesList);
//            System.out.println("生成序号" + i);
        }
        System.out.println(taskDependencies.size());

        StringBuilder taskInstanceDag = new StringBuilder();
        System.out.println("生成dag结束" + taskNum);
        return "图塞不下";
    }

    //获取Task在System中实例化的语句
    public String getTaskInstance(){
        int taskNum = GlobalTaskList.size();
        StringBuilder taskDeclaration = new StringBuilder();
        //输出upp system
        for (int i = 0; i < taskNum; i++) {
            taskDeclaration.append(GlobalTaskList.get(i).getUppTask()).append('\n');
        }
        return taskDeclaration.toString();
    }

    public String getInstanceTaskNum(){
        //const int N = 10; //任务数量
        StringBuilder sb = new StringBuilder();
        sb.append("const int N = ").append(GlobalTaskList.size()).append(";");
        sb.append("\n");
        return sb.toString();
    }

    public String getInstanceTaskInSystem(){
        int taskNum = GlobalTaskList.size();
        StringBuilder instanceTaskInSystem = new StringBuilder();
        instanceTaskInSystem.append(",");
        //输出upp system
        for (int i = 0; i < taskNum; i++) {
            instanceTaskInSystem.append(GlobalTaskList.get(i).getUppTaskName());
            if(i != taskNum-1)instanceTaskInSystem.append(",");
            else instanceTaskInSystem.append(";");
        }
        instanceTaskInSystem.append('\n');
        return instanceTaskInSystem.toString();
    }

    public void report(){
        for(int p = 0;p < getGlobalTaskList().size();p++){
            Task task1 = getGlobalTaskList().get(p);
            if(task1.taskName.equals("Task2")){
                LinkedList<Integer> dependencies = getTaskDependencies().get(task1.getJob_inst_idx());
                for(int dependencyId:dependencies){
                    System.out.println(task1.taskName + "|"+task1.job_inst_idx_inside + " wait for " + getGlobalTaskList().get(dependencyId).taskName +"("+getGlobalTaskList().get(dependencyId).job_inst_idx_inside +")");
                }
            }
        }
    }
}
