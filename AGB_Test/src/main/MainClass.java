package main;

import Tools.*;

import javax.naming.NameNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainClass {
    private double [] timeStamp;
    private double [] rssi;
    private double [] phase;
    double THRESHOLD_RSSI_MAX_DIFF_TO_PEAK = 10;
    double V = 0.044179;
    double FIXED_FREQUENCY = 920.625;


    private void processData() {
        int num = phase.length;
        double max = -70;
        int maxIndex = 0;
        for (int i = 0; i < num; i++) {
            max = max > rssi[i] ? max : rssi[i];
            maxIndex = max > rssi[i] ? maxIndex : i;
        }

        int startI = maxIndex - 1;
        for(;startI >= 0 && max - rssi[startI] <= THRESHOLD_RSSI_MAX_DIFF_TO_PEAK;startI--);
        System.out.println("startI: " + startI);

        if(startI > 0) {
            int newLen = num - startI;
            double [] newTime = new double[newLen];
            double [] newRssi = new double[newLen];
            double [] newPhase = new double[newLen];
            for(int j = 0, i = startI;j < newLen;i++, j++) {
                newTime[j] = timeStamp[i];
                newRssi[j] = rssi[i];
                newPhase[j] = phase[i];
            }

            timeStamp = new double[newLen];
            rssi = new double[newLen];
            phase = new double[newLen];
            for(int i = 0;i < newLen;i++) {
                timeStamp[i] = newTime[i];
                rssi[i] = newRssi[i];
                phase[i] = newPhase[i];
            }
        }
    }

    private void processPhase() {
        int len = phase.length;
        int left = 0, right = len - 1;
        int leftMaxIndex = left, rightMaxIndex = right;
        double leftMax = phase[leftMaxIndex], rightMax = phase[rightMaxIndex];

        final int THRESHOLD = 5;

        for(;left < len;left++) {
            leftMaxIndex = leftMax > phase[left] ? leftMaxIndex : left;
            leftMax = leftMax > phase[left] ? leftMax : phase[left];
            if(left - leftMaxIndex > THRESHOLD) {
                break;
            }
        }
        for(;right >= 0;right--) {
            rightMaxIndex = rightMax > phase[right] ? rightMaxIndex : right;
            rightMax = rightMax > phase[right] ? rightMax : phase[right];
            if(rightMaxIndex - right > THRESHOLD) {
                break;
            }
        }

        if(leftMaxIndex != rightMaxIndex) {
            final int THRESHOLD1 = 5;
            leftMaxIndex = Math.max(0, leftMaxIndex - THRESHOLD1);
            rightMaxIndex = Math.min(len - 1, rightMaxIndex + THRESHOLD1);
            System.out.println("left: " + leftMaxIndex + "\nright: " + rightMaxIndex);
            int newLen = len - rightMaxIndex + leftMaxIndex - 1;
            double [] newPhase = new double[newLen];
            double [] newTime = new double[newLen];
            int i = 0;
            for(;i < leftMaxIndex;i++) {
                newPhase[i] = phase[i];
                newTime[i] = timeStamp[i];
            }
            for(int j = rightMaxIndex + 1;j < len;j++, i++) {
                newPhase[i] = phase[j];
                newTime[i] = timeStamp[j];
            }
            phase = new double[newLen];
            timeStamp = new double[newLen];
            for(int j = 0;j < newLen;j++) {
                phase[j] = newPhase[j];
                timeStamp[j] = newTime[j];
            }
        }
    }
    /**
     * 消除相位跳变
     */
    private void eliminatePhaseJumps() {
        int len = phase.length;
        for(int i = 1;i < len;i++) {
            double tmp = Math.abs(phase[i - 1] - phase[i]);
            if(tmp > 2 && tmp < 5) {
                if(phase[i - 1] > phase[i]) {
                    for(int j = i;j < len;j++) {
                        phase[j] += Math.PI;
                    }
                } else {
                    for(int j = i;j < len;j++) {
                        phase[j] -= Math.PI;
                    }
                }
            }
            if(tmp > 5) {
                if(phase[i - 1] > phase[i]) {
                    for(int j = i;j < len;j++) {
                        phase[j] += 2 * Math.PI;
                    }
                } else {
                    for(int j = i;j < len;j++) {
                        phase[j] -= 2 * Math.PI;
                    }
                }
            }
        }
    }


    /**
     * 使用三个时间段的信息计算出书的所在的时间位置
     * @param t1
     * @param t2
     * @param t3
     * @param phase1
     * @param phase2
     * @param phase3
     * @return 书的时间点
     */
    public double computeTime(double t1, double t2, double t3, double phase1, double phase2, double phase3){
        int C = 299700000;
        double cos1 =  Math.abs((phase1 - phase2)*C/((t2-t1)*V*FIXED_FREQUENCY*1000*4*Math.PI));
        double cos2 =  Math.abs((phase2 - phase3)*C/((t3-t2)*V*FIXED_FREQUENCY*1000*4*Math.PI));
        double tan1 = getTan(cos1);
        double tan2 = getTan(cos2);
        double x = (t2-t1)*tan1/(tan2-tan1);
        return x + t2;
    }

    /**
     * 通过余弦值获得正切值
     * @param cos 余弦值
     * @return
     */
    public double getTan(double cos){
        return Math.sqrt(1-cos*cos);
    }

    public double getLocation(){
        List<Double> bookTimeList = new ArrayList<>();
        int interval = 15;
        int num = 0;
        double averageTime = 0.0,sum =0.0;
        for(int i=0;i+interval*2<300;i++){
            double bTime = computeTime(timeStamp[i],
                    timeStamp[i+interval],
                    timeStamp[i+interval*2],
                    phase[i],
                    phase[i+interval],
                    phase[i+interval*2]);
            if(bTime > 0) {
                sum += bTime;
                num++;
            }
            bookTimeList.add(bTime);
            System.out.println("time="+timeStamp[i]+" "+bTime);
        }
        averageTime = sum/num;
        return averageTime;
    }
    public static void main(String args[]) {
        MainClass mainClass = new MainClass();
        ArrayList<EachInfo> infos = readRFIDFile.readFile(".\\Data\\1121\\11.txt");
        int num = infos.size();
        mainClass.timeStamp = new double[num];
        mainClass.rssi = new double[num];
        mainClass.phase = new double[num];

        int start = 0;
        for (int i = 0; i < num; i++) {
            EachInfo info = infos.get(i);
            mainClass.timeStamp[i] = info.getTime();
            mainClass.rssi[i] = info.getRSSI();
            mainClass.phase[i] = info.getPhase();
        }

        mainClass.eliminatePhaseJumps();
        double newphase[] = new double[num];
        Smooth.linearSmooth3(mainClass.phase,newphase,num);
        mainClass.phase = newphase;
        double bookTime = mainClass.getLocation();
        System.out.println(bookTime);


//        mainClass.eliminatePhaseJumps();
//        mainClass.processData();
//        mainClass.processPhase();
//        num = mainClass.phase.length;
////        double a[] = mypolyfit.PolyFit(mainClass.timeStamp, mainClass.phase, num, 3);
//        LeastSquareMethod leastSquareMethod = new LeastSquareMethod(mainClass.timeStamp, mainClass.phase, 3);
//        double a[] = leastSquareMethod.getCoefficient();
//            /*if(a[2]>0)
//                start++;*/
//        System.out.println("Mid: "+( - a[1] / (a[2] * 2)));
//
//        System.out.println(a[0] + " " + a[1] + " " + a[2]);
    }

}
