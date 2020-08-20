package com.simulator.simulator.XMLLoader.task;

import java.util.LinkedList;

/**
 * 一个任务实例包含：
 *      统一属性：data_name、mov_dir、id(lue)
 *      实例属性：job_inst_idx，total_size，data_inst_idx
 * 通过job_inst_idx赋值给对应的任务实例
 */
public class DataForTask {
    public String dataName;
    public int mov_dir;
    public int job_inst_idx;
    public int total_size;
    public LinkedList<Integer> data_inst_idx;

    public DataForTask(String dataName, int mov_dir, int job_inst_idx, int total_size, LinkedList<Integer> data_inst_idx) {
        this.dataName = dataName;
        this.mov_dir = mov_dir;
        this.job_inst_idx = job_inst_idx;
        this.total_size = total_size;
        this.data_inst_idx = data_inst_idx;
    }

    public DataForTask(String dataName, int mov_dir) {
        this.dataName = dataName;
        this.mov_dir = mov_dir;
    }

    @Override
    public String toString() {
        return "DataForTask{" +
                "dataName='" + dataName + '\'' +
                ", mov_dir=" + mov_dir +
                ", job_inst_idx=" + job_inst_idx +
                ", total_size=" + total_size +
                ", data_inst_idx=" + data_inst_idx +
                '}';
    }

    @Override
    public int hashCode() {
        return 31 * (dataName.charAt(dataName.length()-1) - '0');
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DataForTask){
            DataForTask dataForTask = (DataForTask)obj;
            return this.dataName.equals(dataForTask.dataName) && this.data_inst_idx == dataForTask.data_inst_idx;
//            return this.dataName.equals(dataForTask.dataName);
        }else{
            return false;
        }
    }
}
