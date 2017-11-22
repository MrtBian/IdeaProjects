package cn.edu.nju.iq.fitter;

import cn.edu.nju.iq.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class RealTimeFitter {
    private List<String> infoList;

    private double [] timeStamp;
    private double [] rssi;
    private double [] phase;

    public RealTimeFitter() {
        infoList = new ArrayList<String>();
    }

    public void addData(String data) {
        infoList.add(data);
    }

    public void clear() {
        infoList.clear();
    }

    public int fit(int fitWay) {
        wait(fitWay);
        return polyFit(fitWay);
    }

    private void initData() {
        int num = infoList.size();
        timeStamp = new double[num];
        rssi = new double[num];
        phase = new double[num];

        for (int i = 0; i < num; i++) {
            String [] info = infoList.get(i).split(" ");
            timeStamp[i] = Long.parseLong(info[4]);
            rssi[i] = Double.parseDouble(info[2]);
            phase[i] = Double.parseDouble(info[3]);
        }

        eliminatePhaseJumps();
    }

    /**
     * 拟合数据，返回中线对应时间
     *
     * @return midTime
     */
    private int polyFit(int fitWay) {
        initData();

        System.out.println("num: " + infoList.size());
        LeastSquareMethod leastSquareMethod = null;
        switch (fitWay) {
            case Constants.RSSI_FIT:
                processRssi();
                leastSquareMethod = new LeastSquareMethod(timeStamp, rssi, 3);
                break;
            case Constants.PHASE_FIT:
                processPhase();
                leastSquareMethod = new LeastSquareMethod(timeStamp, phase, 3);
                break;
        }

        double [] coe = leastSquareMethod.getCoefficient();
        return (int) (-coe[1] / (coe[2] * 2));
    }

    private void processRssi() {
        int num = infoList.size();
        double max = Constants.MIN_RSSI;
        int maxIndex = 0;
        for (int i = 0; i < num; i++) {
            max = max > rssi[i] ? max : rssi[i];
            maxIndex = max > rssi[i] ? maxIndex : i;
        }

        int startI = maxIndex - 1;
        for(;max - rssi[startI] <= Constants.THRESHOLD_RSSI_MAX_DIFF_TO_PEAK;startI--);

        int newLen = num - startI;
        double [] newTime = new double[newLen];
        double [] newRssi = new double[newLen];
        for(int j = 0, i = startI;j < newLen;i++, j++) {
            newTime[j] = timeStamp[i];
            newRssi[j] = rssi[i];
        }

        timeStamp = new double[newLen];
        rssi = new double[newLen];
        for(int i = 0;i < newLen;i++) {
            timeStamp[i] = newTime[i];
            rssi[i] = newRssi[i];
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

    private void wait(int fitWay) {
        while (true) {
            synchronized (this) {
                try {
                    if(!isPolyFit(fitWay)) {
                        System.out.println("waiting for polyfit...");
                        this.wait();
                    }
                    System.out.println("start polyfit...");
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断是否可以拟合
     *
     * @return
     */
    public boolean isPolyFit(int fitWay) {
        initData();

        boolean result = false;
        if(infoList.size() >= Constants.THRESHOLD_MIN_FIT_POINTS_NUM){
            switch (fitWay) {
                case Constants.RSSI_FIT:
                    result = isPolyFitRssi();
                    break;
                case Constants.PHASE_FIT:
                    result = isPolyFitPhase();
                    break;
            }
        }

        return result;
    }

    private boolean isPolyFitRssi() {
        double max = rssi[0];
        int maxIndex = 0;
        int len = rssi.length;
        for(int i = 1;i < len;i++) {
            max = max > rssi[i] ? max : rssi[i];
            maxIndex = max > rssi[i] ? maxIndex : i;
        }

        return len - maxIndex >= Constants.THRESHOLD_RSSI_MOST_POINTS_NUM_TO_PEAK
                && max - rssi[len - 1] >= Constants.THRESHOLD_MIN_FLUCTUATION;
    }

    private boolean isPolyFitPhase() {
        double max = phase[0];
        int maxIndex = 0;
        int len = phase.length;
        for(int i = 1;i < len;i++) {
            max = max > phase[i] ? max : phase[i];
            maxIndex = max > phase[i] ? maxIndex : i;
        }

        return len - maxIndex >= Constants.THRESHOLD_RSSI_MOST_POINTS_NUM_TO_PEAK;
    }
}
