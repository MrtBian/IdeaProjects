package Read;

import Tools.LeastSquareMethod;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;
import main.EachInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import static java.lang.Math.max;
import static java.lang.Math.rint;
import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;


public class ReadTags implements TagReportListenerImplementation.OnGetStartTimeCallback {
    //public static PrintWriter writer;
    public static List<String> infoList;
    public String path = ".\\Data";
    private String datastr = "1108";
    public String dataFile = datastr + "_8.txt";
    private ReaderSettings setting;
    //时间间隔
    private int during = 5;

    private long startTime = 0;
    private long endTime = 0;
    private String timeFile = datastr + "_time.txt";

    public ReadTags() {
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        setting = new ReaderSettings();
    }

    public void reading() {
        try {
            String hostname = setting.hostname;
            ImpinjReader reader = new ImpinjReader();
            reader.connect(hostname);
            reader.applySettings(Settings.load(setting.settingsFilePath));

            //writer = new PrintWriter(path+"\\"+dataFile, "UTF-8");
            infoList = new ArrayList<>();
            reader.setTagReportListener(new TagReportListenerImplementation(this));
            reader.start();

            waitPolyfit();
            int toMidTimes = polyFit();
            long midTime = startTime + toMidTimes;
            long endTime = midTime + during * 1000;
            waitTimeMillis(endTime);
            System.out.println("Midtime: " + toMidTimes);
            //Scanner s = new Scanner(System.in);
            //s.nextLine();
            /**
             * stop the antenna
             */
            /*...*/
            reader.stop();
            reader.disconnect();
            //writer.close();
            //FileWriter fileWriter = new FileWriter(path+"\\"+timeFile,true);
            //PrintWriter writer1 = new PrintWriter(fileWriter);
            //endTime = System.currentTimeMillis();
            //writer1.append(dataFile+" "+startTime+" "+endTime+" "+(endTime-startTime)+"\n");
            //writer1.close();
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    /**
     * 等待直到endtime
     *
     * @param endTime 终止时间
     */
    public void waitTimeMillis(long endTime) {
        long curTime = System.currentTimeMillis();
        if (curTime > endTime) {
            System.out.println("it is too late!");
            return;
        }
        while (curTime < endTime) {
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            curTime = System.currentTimeMillis();
        }
    }

    /**
     * 拟合数据，返回中线对应时间
     *
     * @return midTime
     */
    public int polyFit() {
        int num = infoList.size();
        double[] xTime = new double[num];
        double[] yRssi = new double[num];
        double[] yphase = new double[num];

        for (int i = 0; i < num; i++) {
            String[] info = infoList.get(i).split(" ");
            xTime[i] = Long.parseLong(info[4]);
            yRssi[i] = Double.parseDouble(info[2]);
            yphase[i] = Double.parseDouble(info[3]);
        }
        LeastSquareMethod leastSquareMethod = new LeastSquareMethod(xTime, yRssi, 3);
        double a[] = leastSquareMethod.getCoefficient();

        return (int) (-a[1] / (a[2] * 2));
    }

    public void waitPolyfit() {
        while (!isPolyfit()) {
            /*wait*/
            System.out.println("waiting start polyfit...");
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否可以拟合
     *
     * @return
     */
    public boolean isPolyfit() {
         /*条件*/
        int num = infoList.size();
        if(num<5){
            return false;
        }
        double[] xTime = new double[num];
        double[] yRssi = new double[num];
        double[] yphase = new double[num];
        double maxRssi = -70.0;
        for (int i = 0; i < num; i++) {
            String[] info = infoList.get(i).split(" ");
            xTime[i] = Long.parseLong(info[4]);
            yRssi[i] = Double.parseDouble(info[2]);
            yphase[i] = Double.parseDouble(info[3]);
            //System.out.println("yRssi: "+yRssi[i]);
            if (yRssi[i] > maxRssi) {
                maxRssi = yRssi[i];
            }
        }
        System.out.println("MaxRssi: "+maxRssi);
        if (yRssi[num - 1] > yRssi[num - 2] || yRssi[num - 1] > maxRssi - 4) {
            return false;
        }
        //连续下降点的个数
        /*int numDe = 5;
        for (int i = num - 1; i >= num - numDe - 1; i--) {
            String[] info = infoList.get(i).split(" ");
            xTime[i] = Long.parseLong(info[4]);
            yRssi[i] = Double.parseDouble(info[2]);
            yphase[i] = Double.parseDouble(info[3]);

            if (i<num - 1&&yRssi[i+1] > yRssi[i]) {
                return false;
            }
        }*/
        return true;
    }

    public static void main(String[] args) {
        ReadTags readTags = new ReadTags();
        readTags.reading();
    }

    @Override
    public void onGetStartTime(long startTime) {
        this.startTime = startTime;
    }
}
