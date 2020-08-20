package com.simulator.simulator.XMLLoader.Util;

import com.simulator.simulator.XMLLoader.task.TaskDiagram;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileWriter;
import java.io.InputStream;

public class UppaalWriteUtil {
    public static void writeBack(TaskDiagram taskDiagram, String writeFileName){
        InputStream writeBackTest = UppaalWriteUtil.class.getClassLoader().getResourceAsStream("new_demo_autowire_618.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document read = saxReader.read(writeBackTest);
            Element root = read.getRootElement();
            //生成declaration中的DAG
            Element declaration = root.element("declaration");
            String text = declaration.getText();
            String beginTag = "//Task diagram autowire begin";
            String endTag = "//Task diagram autowire end";
            text = uppaalWriteHelper(text,beginTag,endTag,taskDiagram.getDiagramByInstance());

            //生成declaration中的任务数量
            /*
                //TaskNum autowire begin
                const int N = 10; //任务数量
                //TaskNum autowire end
             */
//            System.out.println(text);
            text = uppaalWriteHelper(text,"//TaskNum autowire begin","//TaskNum autowire end",taskDiagram.getInstanceTaskNum());
            declaration.setText(text);

            //生成system中的instance
            Element system = root.element("system");
            String systemText = system.getText();
            systemText = uppaalWriteHelper(systemText,"//Task instance autowire begin","//Task instance autowire end",taskDiagram.getTaskInstance());
            //生成system中的system
            StringBuilder sb = new StringBuilder(systemText);
            String tmp = "//Task instance autowire in system begin";
            int begin = sb.indexOf(tmp);
            int end = sb.indexOf("//Task instance autowire in system end");
            String oldSystem = sb.substring(begin, end);
            int index = oldSystem.indexOf(";");
//            System.out.println(oldSystem.length() + "||" + oldSystem);
            StringBuilder oldSystemSb = new StringBuilder(oldSystem);
            oldSystemSb.replace(index,oldSystem.length(),taskDiagram.getInstanceTaskInSystem());
            sb.replace(begin,end,oldSystemSb.toString());
            systemText = sb.toString();
            system.setText(systemText);

            FileWriter fileWriter = new FileWriter(writeFileName);
            read.write(fileWriter);
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void writeDeclarationDiagram(TaskDiagram taskDiagram){ }

    public static String uppaalWriteHelper(String source,String beginTag,String endTag,String newString){
        StringBuilder sb = new StringBuilder(source);
        int begin = sb.indexOf(beginTag);
        int end = sb.indexOf(endTag);

        sb.replace(begin+beginTag.length()+1,end,newString);
        return sb.toString();
//            System.out.println(begin +"|||" + end+"|||"+sb.length());
//            System.out.println(sb);
//            System.out.println(read);
    }
}
