package com.simulator.simulator.report;

public class DMAReport {
    int curSize = 0;
    int totalSize = 0;
    int time = 0;

    public DMAReport(int curSize, int totalSize, int time) {
        this.curSize = curSize;
        this.totalSize = totalSize;
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(time + "\t" + curSize + "\t" + totalSize);
        return stringBuilder.toString();
    }
}
