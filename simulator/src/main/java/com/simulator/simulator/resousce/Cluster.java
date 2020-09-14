package com.simulator.simulator.resousce;

import com.simulator.simulator.XMLLoader.System.ModuleElement;
import com.simulator.simulator.XMLLoader.System.SubSystem;
import com.simulator.simulator.XMLLoader.task.DataInstance;
import com.simulator.simulator.XMLLoader.task.Task;
import com.simulator.simulator.report.ClusterReport;
import com.simulator.simulator.report.ReportInterFace;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Cluster implements ReportInterFace {
    List<Memory> memoryList = new CopyOnWriteArrayList<>();
    List<DSP> dspList = new CopyOnWriteArrayList<>();
    List<FPGA> fpgaList = new CopyOnWriteArrayList<>();
    List<DMA> dmaList = new CopyOnWriteArrayList<>();

    public static int ID = 0;
    public int clusterId = 0;

    public LinkedList<ClusterReport> clusterReports = new LinkedList<>();
    public int totalCost = 0;

    public Cluster() {
        dmaList.add(new DMA(clusterId));
    }

    public Cluster(SubSystem subSystem) {
        clusterId = ID++;
        //重置DSP编号
        DSP.idInCluster = 0;
        LinkedList<ModuleElement> elements = subSystem.getElements();

        for(ModuleElement moduleElement:elements){
            if(moduleElement.getType().equals("DSP")){
                dspList.add(new DSP(clusterId));
            }else if(moduleElement.getType().equals("MEMORY")){
                memoryList.add(new Memory(clusterId));
            }
        }

        dmaList.add(new DMA(clusterId));
    }

    public void setMemoryList(List<Memory> memoryList) {
        this.memoryList = memoryList;
    }

    public void setDspList(List<DSP> dspList) {
        this.dspList = dspList;
    }

    public void setFpgaList(List<FPGA> fpgaList) {
        this.fpgaList = fpgaList;
    }

    public void setDmaList(List<DMA> dmaList) {
        this.dmaList = dmaList;
    }

    public List<DSP> getDspList() {
        return dspList;
    }

    public List<FPGA> getFpgaList() {
        return fpgaList;
    }

    public List<DMA> getDmaList() {
        return dmaList;
    }

    public List<Memory> getMemoryList() {
        return memoryList;
    }

    int remainBusCapacity = 10;

    //
     synchronized public void getData(Task task){
        //try to get data in memlist,if doesn't find generate dma task to move data

        for(DataInstance DataInstance: task.getDataInsIn()){
            if(DataInstance == null)break;

            Memory hitMemory = getDataInMem(DataInstance);
            if(hitMemory == null){
//                System.out.println(DataInstance.dataName+" : not find");
                if(!checkIfTaskSubmitted(DataInstance)){
                    generateDmaTask(DataInstance);
                }
            }
            while(getDataInMem(DataInstance) == null){
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    synchronized public boolean checkData(DataInstance dataInstance){
        Memory hitMemory = getDataInMem(dataInstance);
        return hitMemory != null;
    }

    private boolean checkIfTaskSubmitted(DataInstance DataInstance) {
        for(DMA dma:dmaList){
            if(dma.checkIfTaskSubmitted(DataInstance))return true;
        }
        return false;
    }


    synchronized private void generateDmaTask(DataInstance data) {
        int source = 0;
        int target = memSchedule();
        DMA dma = dmaSchedule();
        dma.submit(data,source,target);

    }

    private int memSchedule() {
        int min = Integer.MAX_VALUE;
        int target = 0;
        for(Memory memory:memoryList){
            if(min > memory.getSize()){
                min = memory.getSize();
                target = memory.getMemoryId();
            }
        }

        return target;
    }

    private DMA dmaSchedule() {
        return dmaList.get(0);
    }

    private Memory getDataInMem(DataInstance data){
        for(Memory memory:memoryList){
            if(memory.getData(data))return memory;
        }



        return null;
    }

    public int getRemainBusCapacity() {

        return remainBusCapacity;
    }

    public void saveData(Task task) {

        for(DataInstance data:task.getDataInsOut()){
            int memId = memSchedule();
            Memory memory = memoryList.get(memId);
            memory.save(data);
        }

    }

    int i = 0;
    public void submit(Task task) {
        totalCost += task.cost;

//        Collections.sort(dspList, Comparator.comparingInt(d -> d.getQueue().size()));
//        dspList.get(0).submit(task);

        dspList.get(i++%4).submit(task);
    }

    public void dmaSave(DataInstance data){
        int memId = memSchedule();
        Memory memory = memoryList.get(memId);
        memory.save(data);
    }

    @Override
    public String getReport() {
         StringBuilder sb = new StringBuilder();

         for(ClusterReport clusterReport:clusterReports){
             sb.append(clusterReport.toString());
             sb.append('\n');
         }

         return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cluster: " + clusterId + "状态");

        for(DSP dsp:dspList){
            stringBuilder.append(dsp);
        }

        return stringBuilder.toString();

    }
}
