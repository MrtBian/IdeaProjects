package main;

import Tools.*;

import java.util.ArrayList;

public class MainClass {
    private double [] timeStamp;
    private double [] rssi;
    private double [] phase;

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
            int newLen = len - rightMaxIndex + leftMaxIndex;
            double [] newPhase = new double[newLen];
            double [] newTime =new double[newLen];
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
        mainClass.processPhase();
        //double a[] = mypolyfit.PolyFit(xTime, yRssi, num, 2);
        LeastSquareMethod leastSquareMethod = new LeastSquareMethod(mainClass.timeStamp, mainClass.phase, 3);
        double a[] = leastSquareMethod.getCoefficient();
            /*if(a[2]>0)
                start++;*/
        System.out.println("Mid: "+( - a[1] / (a[2] * 2)));

        System.out.println(a[0] + " " + a[1] + " " + a[2]);
    }

}
