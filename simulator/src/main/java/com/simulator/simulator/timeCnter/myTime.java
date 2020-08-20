package com.simulator.simulator.timeCnter;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class myTime {
    static int time = 0;

    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static int getTime() {
        try {
            readWriteLock.readLock().lock();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            readWriteLock.readLock().unlock();
        }
        return -1;
    }

    public static void setTime(int time) {
        try {
            readWriteLock.writeLock().lock();
            myTime.time = time;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void addTime(int time){
        try {
            readWriteLock.writeLock().lock();
            myTime.time += time;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
