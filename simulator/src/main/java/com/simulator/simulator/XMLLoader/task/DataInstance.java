package com.simulator.simulator.XMLLoader.task;

public class DataInstance {
    public String dataName;
    public int mov_dir;
    public int job_inst_idx;
    public int total_size;
    public int data_inst_idx;

    public DataInstance(String dataName, int mov_dir, int job_inst_idx, int total_size, int data_inst_idx) {
        this.dataName = dataName;
        this.mov_dir = mov_dir;
        this.job_inst_idx = job_inst_idx;
        this.total_size = total_size;
        this.data_inst_idx = data_inst_idx;
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
        if(obj instanceof DataInstance){
            DataInstance dataForTask = (DataInstance)obj;
            return this.dataName.equals(dataForTask.dataName) && this.data_inst_idx == dataForTask.data_inst_idx;
        }else{
            return false;
        }
    }
}
