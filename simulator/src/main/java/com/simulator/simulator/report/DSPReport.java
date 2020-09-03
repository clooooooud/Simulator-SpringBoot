package com.simulator.simulator.report;

public class DSPReport{
    int curCost = 0;
    int totalCost = 0;
    int time = 0;

    public DSPReport(int curCost, int totalCost, int time) {
        this.curCost = curCost;
        this.totalCost = totalCost;
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(time + "\t" + curCost + "\t" + totalCost);
        return stringBuilder.toString();
    }
}
