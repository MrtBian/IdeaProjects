package main;

import Tools.*;

import java.util.ArrayList;

public class MainClass {
    public static void main(String args[]) {
        ArrayList<EachInfo> infos = readRFIDFile.readFile(".\\Data\\1108_3.txt");
        int num = infos.size();
        double[] xTime = new double[num];
        double[] yRssi = new double[num];
        double[] yphase = new double[num];
        int start = 0;
        for (int i = 0; i < num; i++) {
            EachInfo info = infos.get(i);
            xTime[i] = info.getTime();
            yRssi[i] = info.getRSSI();
            yphase[i] = info.getPhase();
        }
        //double a[] = mypolyfit.PolyFit(xTime, yRssi, num, 2);
        LeastSquareMethod leastSquareMethod = new LeastSquareMethod(xTime, yRssi, 3);
        double a[] = leastSquareMethod.getCoefficient();
            /*if(a[2]>0)
                start++;*/
        System.out.println(start+" "+( - a[1] / (a[2] * 2)));

        System.out.println(a[0] + " " + a[1] + " " + a[2]);
    }

}
