package main;

import Tools.*;

import java.util.ArrayList;

public class MainClass {
    private double [] timeStamp;
    private double [] rssi;
    private double [] phase;
    double THRESHOLD_RSSI_MAX_DIFF_TO_PEAK = 10;


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
    public static void main(String args[]) {
        MainClass mainClass = new MainClass();
        ArrayList<EachInfo> infos = readRFIDFile.readFile(".\\Data\\1120\\13.txt");
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
        mainClass.processData();
        mainClass.processPhase();
        num = mainClass.phase.length;
//        double a[] = mypolyfit.PolyFit(mainClass.timeStamp, mainClass.phase, num, 3);
        LeastSquareMethod leastSquareMethod = new LeastSquareMethod(mainClass.timeStamp, mainClass.phase, 3);
        double a[] = leastSquareMethod.getCoefficient();
            /*if(a[2]>0)
                start++;*/
        System.out.println("Mid: "+( - a[1] / (a[2] * 2)));

        System.out.println(a[0] + " " + a[1] + " " + a[2]);
    }

}
