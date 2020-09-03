package com.simulator.simulator.report;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class ReportUtil {

    static public void writer(ReportInterFace report,String fileName){
        try {
            String text = report.getReport();
            File file = new File(fileName);
            boolean b = file.createNewFile();
            if(b) {
                Writer out = new FileWriter(file);
                out.write(text);
                out.close();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
