package Read;

import Tools.LeastSquareMethod;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import static java.lang.Math.max;
import static java.lang.Thread.sleep;


public class ReadTags implements TagReportListenerImplementation.OnGetStartTimeCallback {
    public static PrintWriter writer;
    public static List<String> infoList;
    private String path = ".\\Data";
    private String dateStr = "1110";
    private String dataFile = dateStr + "_1.txt";
    private ReaderSettings setting;
    //给定时间间隔
    private int during = 5;

    private long startTime = 0;
    private long endTime = 0;
    private String timeFile = dateStr + "_time.txt";

    private ReadTags() {
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        setting = new ReaderSettings();
    }

    private void reading() {
        try {
            String hostname = setting.hostname;
            ImpinjReader reader = new ImpinjReader();
            reader.connect(hostname);
            reader.applySettings(Settings.load(setting.settingsFilePath));

            writer = new PrintWriter(path+"\\"+dataFile, "UTF-8");
            infoList = new ArrayList<>();
            reader.setTagReportListener(new TagReportListenerImplementation(this));
            reader.start();

            waitPolyfit();
            int toMidTimes = polyFit();
            long midTime = startTime + toMidTimes;
            endTime = midTime + during * 1000;
            sleep(endTime - System.currentTimeMillis());

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

/*    *//**
     * 等待直到endtime
     *
     * @param endTime 终止时间
     *//*
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
    }*/

    /**
     * 拟合数据，返回中线对应时间
     *
     * @return midTime
     */
    private int polyFit() {
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

    private void waitPolyfit() {
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
    private boolean isPolyfit() {
         /*条件*/
        int num = infoList.size();
        if(num<5){
            return false;
        }
        double[] xTime = new double[num];
        double[] yRssi = new double[num];
        double[] yphase = new double[num];
        double maxRssi = 0.0;
        for (int i = 0; i < num; i++) {
            String[] info = infoList.get(i).split(" ");
            xTime[i] = Long.parseLong(info[4]);
            yRssi[i] = Double.parseDouble(info[2]);
            yphase[i] = Double.parseDouble(info[3]);
            if (yRssi[i] > maxRssi) {
                maxRssi = yRssi[i];
            }
        }

        if (yRssi[num - 1] > yRssi[num - 2] || yRssi[num - 1] > maxRssi - 5) {
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
