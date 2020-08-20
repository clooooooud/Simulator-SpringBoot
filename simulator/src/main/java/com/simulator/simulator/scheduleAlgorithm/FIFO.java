package com.simulator.simulator.scheduleAlgorithm;

import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.resousce.ResourcesManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FIFO extends Thread{

        /**
         * 对保存实例的变量添加volatile的修饰
         */
        private volatile static FIFO instance = null;
        Queue<Task> queue;
        final ReadWriteLock lock = new ReentrantReadWriteLock();

        private FIFO(){
            queue = new ConcurrentLinkedQueue<>();
        }

        public void enQueue(Task task){
            queue.add(task);
        }

         public Task deQueue(){
            Task tmp = queue.remove();
            return tmp;
        }

        public static FIFO getInstance(){
            //先检查实例是否存在，如果不存在才进入下面的同步块
            if(instance == null){
            //同步块，线程安全的创建实例
                synchronized(FIFO.class){
            //再次检查实例是否存在，如果不存在才真的创建实例
                    if(instance == null){
                        instance = new FIFO();
                    }
                }
            }
            return instance;
        }

        public boolean finish(){
            return queue.isEmpty();
        }

    @Override
    public void run() {
            while(true){
                if(!queue.isEmpty()){
                    Task task = deQueue();

                    ResourcesManager.getResourcesManager().submit(task);
                }
            }

    }
}
