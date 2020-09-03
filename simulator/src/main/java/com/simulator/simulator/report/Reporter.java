package com.simulator.simulator.report;

import com.simulator.simulator.resousce.Cluster;
import com.simulator.simulator.resousce.DMA;
import com.simulator.simulator.resousce.DSP;
import com.simulator.simulator.resousce.ResourcesManager;
import com.simulator.simulator.timeCnter.NewTimer;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Reporter extends Thread{

    int time = 0;

    @Override
    public void run() {
        while (true){
            try {
                time = (int) (System.currentTimeMillis() - NewTimer.getBeginTime());
                report();
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void report(){
        ResourcesManager resourcesManager = ResourcesManager.getResourcesManager();

        List<Cluster> clusterList = resourcesManager.getClusterList();
        clusterReport(clusterList);

        for(Cluster c:clusterList){
            List<DSP> dspList = c.getDspList();
            dspReport(dspList);
        }

        for(Cluster c:clusterList){
            List<DMA> dmaList = c.getDmaList();
            dmaReport(dmaList);
        }
    }

    private void dmaReport(List<DMA> dmaList) {
        for(DMA d:dmaList){
            DMAReport dmaReport = new DMAReport(d.curSize, d.totalDmaSize, time);
            d.dmaReports.add(dmaReport);
//            System.out.println(dspReport);
        }
    }

    private void clusterReport(List<Cluster> clusterList) {

        for(Cluster c:clusterList){
            c.clusterReports.add(new ClusterReport(c.totalCost,time));
        }
    }

    private void dspReport(List<DSP> dspList) {
        for(DSP d:dspList){
            DSPReport dspReport = new DSPReport(d.curCost, d.totalCost, time);
            d.dspReports.add(dspReport);
//            System.out.println(dspReport);
        }
    }


}
