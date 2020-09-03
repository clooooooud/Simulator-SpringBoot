package com.simulator.simulator.report;

public class ClusterReport{
    int totalCost = 0;
    int time = 0;

    public ClusterReport(int totalCost, int time) {
        this.totalCost = totalCost;
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(time + "\t" + totalCost);
        return stringBuilder.toString();
    }
}
