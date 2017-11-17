package Tools;

import main.EachInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class readRFIDFile {
    public static ArrayList<EachInfo> readFile(String fileName){
        ArrayList<EachInfo> infoList = new ArrayList<EachInfo>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                String[] strs = tempString.split(" ");
                String epc = strs[0]+strs[1];
                double rssi = Double.parseDouble(strs[2]);
                double phase = Double.parseDouble(strs[3]);
                long time = Long.parseLong(strs[4]);
                infoList.add(new EachInfo(epc,rssi,phase,time));
                //System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return infoList;
    }
}
