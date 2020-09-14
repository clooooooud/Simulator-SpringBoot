package com.simulator.simulator.XMLLoader.task;

/**
 * <data consumer_count="1" is_global="False" mem_type="1" name="Data0" producer="Task0">
 * <size ref_count="1" value="15360" />
 * <consumer name="Task2" />
 *
 * consumer_count:消费者数量
 * is_global：是否全局数据
 * mem_type：数据所在的内存，如 0 表示 L1，1 表示 L2，等
 * name：数据名称
 * producer:生产者。string
 *
 *      size.ref_count：被引用次数
 *      size.value:大小
 *
 * 可能多个
 * consumer：消费者。string
 *
 */
public class Data {
    public int consumer_count;
    public boolean is_global;
    public int mem_type;
    public String name;
    public String producer;

    public int ref_count;
    public int size;

    public String conusmer;

    public Data(int consumer_count, boolean is_global, int mem_type, String name, String producer, String conusmer) {
        this.consumer_count = consumer_count;
        this.is_global = is_global;
        this.mem_type = mem_type;
        this.name = name;
        this.producer = producer;
        this.conusmer = conusmer;
    }
    public int getProducerId(){
        int id = 0;
        int cnt = 1;
        for(int i = producer.length()-1;i >= 4;i--){
            id += (producer.charAt(i)-'0') * cnt;
            cnt *= 10;
        }
        return  id+1;
    }

    @Override
    public String toString() {
        return "Data{" +
                "consumer_count=" + consumer_count +
                ", is_global=" + is_global +
                ", mem_type=" + mem_type +
                ", name='" + name + '\'' +
                ", producer='" + producer + '\'' +
                ", ref_count=" + ref_count +
                ", size=" + size +
                ", conusmer='" + conusmer + '\'' +
                '}';
    }

    @Override
    public Data clone() throws CloneNotSupportedException {
        return new Data(consumer_count,is_global,mem_type,name,producer, conusmer);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Data) {
            Data data = (Data)obj;
            return this.name.equals(data.name);
        }
        return false;
    }


}
