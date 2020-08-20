package com.simulator.simulator.XMLLoader.task;

import java.util.LinkedList;

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
        int[][] dag = new int[taskNum+1][taskNum+1];
        taskDependencies.add(new LinkedList<Integer>());

        for(int i = 0;i < taskNum;i++){
            Task task = GlobalTaskList.get(i);
//            System.out.println(task.job_inst_idx + "||"+ i);
            //dependency of task i
            taskDependencies.add(new LinkedList<Integer>());
            //标记依赖有几个了
            int index = 0;
            int id = task.getJob_inst_idx();

//            LinkedList<DataForTask> dataIn = task.getDataIn();
//            for(DataForTask d:dataIn){
//                for(int j = 0;j < taskNum;j++){
//                    if(i == j)continue;
//                    LinkedList<DataForTask> checkDependency = GlobalTaskList.get(j).getDataOut();
//                    int dependencyId = GlobalTaskList.get(j).getJob_inst_idx();
//                    if(checkDependency.contains(d)){
//                        //i依赖j
//                        dag[task.getJob_inst_idx()][index] = dependencyId;
//                        index++;
//
//                        //save in taskDependencies
//                        taskDependencies.get(task.getJob_inst_idx()).add(dependencyId);
//                    }
//                }
//            }

            LinkedList<DataInstance> dataIn = task.getDataInsIn();
            for(DataInstance d:dataIn){
                for(int j = 0;j < taskNum;j++){
                    if(i == j)continue;
                    int dependencyId = GlobalTaskList.get(j).getJob_inst_idx();
                    //任务输出的数据实例
                    LinkedList<DataInstance> checkDependency = GlobalTaskList.get(j).getDataInsOut();
                    if(checkDependency.contains(d)){
                        //i依赖j
                        dag[task.getJob_inst_idx()][index] = dependencyId;
                        index++;

                        //save in taskDependencies
                        taskDependencies.get(task.getJob_inst_idx()).add(dependencyId);
                    }
                }
            }
        }

        StringBuilder taskInstanceDag = new StringBuilder();
        taskInstanceDag.append("{").append('\n');
        for(int i = 0;i <taskNum + 1;i++){
            StringBuilder sb = new StringBuilder();
            sb.append("{{");
            for(int j = 0;j < taskNum +1;j++){
                if(j != taskNum){
                    sb.append(dag[i][j]).append(",");
                }else {
                    sb.append(dag[i][j]);
                    if(i != taskNum)sb.append("}},");
                    else sb.append("}}");
                }
            }
            taskInstanceDag.append(sb).append('\n');
        }
        taskInstanceDag.append("};").append('\n');
        return taskInstanceDag.toString();
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
}
