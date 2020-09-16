package com.simulator.simulator.scheduleAlgorithm;

public class AlgorithmManager {
    /**
     * 0:轮询
     * 1：片外访存
     * 2：贪心
     */
    public static int resourceManageAlgorithmId = 0;

    /**
     * 0:无
     * 1：入队抢占
     */
    public static int taskScheduleAlgorithmId = 0;
}
