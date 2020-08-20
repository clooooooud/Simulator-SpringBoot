package com.simulator.simulator.XMLLoader;

import com.simulator.simulator.XMLLoader.System.ComponentStructure;
import com.simulator.simulator.XMLLoader.Util.UppaalReadUtil;
import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class Dom4jTest {
    InputStream in = null;
    InputStream inHardware = null;
    InputStream writeBackTest = null;

    @Before
    public void init(){
        in = Dom4jTest.class.getClassLoader().getResourceAsStream("dagTest.xml");
        inHardware = Dom4jTest.class.getClassLoader().getResourceAsStream("Hardware.xml");
        writeBackTest = Dom4jTest.class.getClassLoader().getResourceAsStream("new_demo_autowire.xml");
//        System.out.println(in);
    }


    //建立任务结构
    @Test
    public void test02(){
        TaskDiagram taskDiagram = UppaalReadUtil.uppaalTaskReader();
        String taskInstanceDag = taskDiagram.getDiagramByInstance();

        System.out.println(taskInstanceDag);

//        System.out.println(taskInstanceDag);
//        UppaalWriteUtil.writeBack(taskDiagram,"new_demo_autowire01.xml");
//        System.out.println(taskDiagram.getTaskInstance());
    }


    //获取硬件信息
    @Test
    public void test04(){
        ComponentStructure componentStructure = UppaalReadUtil.uppaalComponentReader();
//        componentStructure.print();
        System.out.println(componentStructure.getMainSystem());

    }


    //写回测试
    @Test
    public void test05(){
        SAXReader saxReader = new SAXReader();
        try {
            Document read = saxReader.read(writeBackTest);
            Element root = read.getRootElement();
            Element testElement = root.element("test");
            String text = testElement.getText();
            StringBuilder sb = new StringBuilder(text);
            int begin = sb.indexOf("//insert cpu begin");
            int end = sb.indexOf("//insert cpu end");
            sb.replace(begin+18,end,"\n" + "        "+"cpu(dddddddddddddd) \n"+"        ");
//            System.out.println(begin +"|||" + end+"|||"+sb.length());
            System.out.println(sb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void tmp(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 75; i++) {
            sb.append("task" + i + ".finish and ");
        }
        System.out.println(sb);
    }

}
/**
 //以model为单位构建dag
 int size = modelTaskList.size();
 int[][] modelDag = new int[size+1][size+1];

 for(int i = 0;i <size;i++){
 int index = 0;
 TaskModel taskModel = modelTaskList.get(i);
 LinkedList<DataForTask> dataIn = taskModel.getDataIn();
 for(DataForTask d : dataIn){
 for(Data dm :dataModelList){
 if(d.dataName.equals(dm.name)){
 modelDag[taskModel.getModelId()][index] = dm.getProducerId();
 index++;
 }
 }
 }
 }


 //        System.out.println(modelTaskList);
 //输出mdelDag
 System.out.println("{");
 int taskModelNum;
 taskModelNum = modelTaskList.size();
 for(int i = 0;i <taskModelNum + 1;i++){
 StringBuilder sb = new StringBuilder();
 sb.append("{");
 for(int j = 0;j < taskModelNum +1;j++){
 if(j != taskModelNum){
 sb.append(modelDag[i][j]).append(",");
 }else {
 sb.append(modelDag[i][j]);
 sb.append("},");
 }
 }
 System.out.println(sb);
 }
 System.out.println("},");
 //        System.out.println(dataModelList);


 //输出dag
 StringBuilder taskInstanceDag = new StringBuilder();
 //        System.out.println("{");
 taskInstanceDag.append("{").append('\n');
 for(int i = 0;i <taskNum + 1;i++){
 StringBuilder sb = new StringBuilder();
 sb.append("{");
 for(int j = 0;j < taskNum +1;j++){
 if(j != taskNum){
 sb.append(dag[i][j]).append(",");
 }else {
 sb.append(dag[i][j]);
 sb.append("},");
 }
 }
 //            System.out.println(sb);
 taskInstanceDag.append(sb).append('\n');
 }
 //        System.out.println("}");
 taskInstanceDag.append("}").append('\n');
 System.out.println(taskInstanceDag);
 */