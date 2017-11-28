package cn.edu.nju.iq.fitter;

import cn.edu.nju.iq.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class RealTimeFitter {
    private List<String> infoList;

    private double[] timeStamp;
    private double[] rssi;
    private double[] phase;

    private int maxDataIndex;
    private long startTime;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public RealTimeFitter() {
        infoList = new ArrayList<String>();
    }

    public void addData(String data) {
        infoList.add(data);
    }

    public void clear() {
        infoList.clear();
    }

    public int getSize() {
        return infoList.size();
    }

    /**
     * 使用三个时间段的信息计算出书的所在的时间位置
     *
     * @param t1
     * @param t2
     * @param t3
     * @param phase1
     * @param phase2
     * @param phase3
     * @return 书的时间点
     */
    public double computeTime(double t1, double t2, double t3, double phase1, double phase2, double phase3) {
        int C = 299792458;
        double cos1 = Math.abs((phase1 - phase2) * C / ((t2 - t1) * Constants.V * Constants.FIXED_FREQUENCY * 1000 * 4 * Math.PI));
        double cos2 = Math.abs((phase2 - phase3) * C / ((t3 - t2) * Constants.V * Constants.FIXED_FREQUENCY * 1000 * 4 * Math.PI));
        double tan1 = getTan(cos1);
        double tan2 = getTan(cos2);
        double x = (t2 - t1) * Constants.V * tan1 / (tan2 - tan1);
        return x / Constants.V + t2;
    }

    /**
     * 通过余弦值获得正切值
     *
     * @param cos 余弦值
     * @return
     */
    public double getTan(double cos) {
        return Math.sqrt(1 - cos * cos);
    }

    public double getLocation() {
        List<Double> bookTimeList = new ArrayList<>();
        int interval = 5;
        int num = timeStamp.length;
        double averageTime = 0.0, sum = 0.0;
        for (int i = 0; i + interval * 2 < 200; i++) {
            double bTime = computeTime(timeStamp[i],
                    timeStamp[i + interval],
                    timeStamp[i + interval * 2],
                    phase[i],
                    phase[i + interval],
                    phase[i + interval * 2]);
            if (bTime > 0) {
                sum += bTime;
            }
            bookTimeList.add(bTime);
            System.out.println("i=" + i + " " + bTime);
        }
        averageTime = sum / 200;
        return averageTime;
    }

    public int fit(int fitWay) {
        return polyFit(fitWay);
    }

    private void initData() {
        int num = infoList.size();
        timeStamp = new double[num];
        rssi = new double[num];
        phase = new double[num];

        for (int i = 0; i < num; i++) {
            String[] info = infoList.get(i).split(" ");
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
        wait(fitWay);
        synchronized (this) {
            initData();

            System.out.println("num: " + infoList.size());
            LeastSquareMethod leastSquareMethod = null;
            switch (fitWay) {
                case Constants.RSSI_FIT:
                    processData(fitWay);
                    leastSquareMethod = new LeastSquareMethod(timeStamp, rssi, 3);
                    break;
                case Constants.PHASE_FIT:
                    processData(fitWay);
//                    processPhase();
                    leastSquareMethod = new LeastSquareMethod(timeStamp, phase, 3);
                    break;
            }

            double[] coe = leastSquareMethod.getCoefficient();
            return (int) (-coe[1] / (coe[2] * 2));
        }
    }

    private void processData(int fitWay) {
        int num = infoList.size();
        int startIndex = Math.max(0, maxDataIndex - (num - maxDataIndex));

//        double max = Constants.MIN_RSSI;
//        int maxIndex = 0;
//        if(Constants.RSSI_FIT == fitWay) {
//            maxIndex = maxDataIndex;
//        } else {
//            for (int i = 0; i < num; i++) {
//                max = max > rssi[i] ? max : rssi[i];
//                maxIndex = max > rssi[i] ? maxIndex : i;
//            }
//        }
//        int startIndex = maxIndex - 1;
//        for(;startIndex >= 0 && max - rssi[startIndex] <= Constants.THRESHOLD_RSSI_MAX_DIFF_TO_PEAK;startIndex--);

        if (startIndex > 0) {
            System.out.println("startIndex: " + startIndex);
            int newLen = num - startIndex;
            double[] newTime = new double[newLen];
            double[] newRssi = new double[newLen];
            double[] newPhase = new double[newLen];
            for (int j = 0, i = startIndex; j < newLen; i++, j++) {
                newTime[j] = timeStamp[i];
                newRssi[j] = rssi[i];
                newPhase[j] = phase[i];
            }

            timeStamp = new double[newLen];
            rssi = new double[newLen];
            phase = new double[newLen];
            for (int i = 0; i < newLen; i++) {
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

        for (; left < len; left++) {
            leftMaxIndex = leftMax > phase[left] ? leftMaxIndex : left;
            leftMax = leftMax > phase[left] ? leftMax : phase[left];
            if (left - leftMaxIndex > THRESHOLD) {
                break;
            }
        }
        for (; right >= 0; right--) {
            rightMaxIndex = rightMax > phase[right] ? rightMaxIndex : right;
            rightMax = rightMax > phase[right] ? rightMax : phase[right];
            if (rightMaxIndex - right > THRESHOLD) {
                break;
            }
        }

        if (leftMaxIndex != rightMaxIndex) {
            final int THRESHOLD1 = 5;
            leftMaxIndex = Math.max(0, leftMaxIndex - THRESHOLD1);
            rightMaxIndex = Math.min(len - 1, rightMaxIndex + THRESHOLD1);
            System.out.println("left: " + leftMaxIndex + "\nright: " + rightMaxIndex);
            int newLen = len - rightMaxIndex + leftMaxIndex - 1;
            double[] newPhase = new double[newLen];
            double[] newTime = new double[newLen];
            int i = 0;
            for (; i < leftMaxIndex; i++) {
                newPhase[i] = phase[i];
                newTime[i] = timeStamp[i];
            }
            for (int j = rightMaxIndex + 1; j < len; j++, i++) {
                newPhase[i] = phase[j];
                newTime[i] = timeStamp[j];
            }
            phase = new double[newLen];
            timeStamp = new double[newLen];
            for (int j = 0; j < newLen; j++) {
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
        for (int i = 1; i < len; i++) {
            double tmp = Math.abs(phase[i - 1] - phase[i]);
            if (tmp > 2 && tmp < 5) {
                if (phase[i - 1] > phase[i]) {
                    for (int j = i; j < len; j++) {
                        phase[j] += Math.PI;
                    }
                } else {
                    for (int j = i; j < len; j++) {
                        phase[j] -= Math.PI;
                    }
                }
            }
            if (tmp > 5) {
                if (phase[i - 1] > phase[i]) {
                    for (int j = i; j < len; j++) {
                        phase[j] += 2 * Math.PI;
                    }
                } else {
                    for (int j = i; j < len; j++) {
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
                    if (!isPolyFit(fitWay)) {
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
        System.out.println("maxTime: " + timeStamp[maxDataIndex]);
        try {
            Thread.sleep(Constants.THRESHOLD_TIME_READ_DATA_AFTER_PEAK_APPEAR - System.currentTimeMillis() + startTime + (int) timeStamp[maxDataIndex]);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        if (infoList.size() >= Constants.THRESHOLD_MIN_FIT_POINTS_NUM) {
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
        for (int i = 1; i < len; i++) {
            max = max > rssi[i] ? max : rssi[i];
            maxIndex = max > rssi[i] ? maxIndex : i;
        }

        if (len - maxIndex >= Constants.THRESHOLD_RSSI_MOST_POINTS_NUM_TO_PEAK
                && max - rssi[len - 1] >= Constants.THRESHOLD_MIN_FLUCTUATION) {
            maxDataIndex = maxIndex;
            return true;
        }
        return false;
    }

    private boolean isPolyFitPhase() {
        double max = phase[0];
        int maxIndex = 0;
        int len = phase.length;
        for (int i = 1; i < len; i++) {
            max = max > phase[i] ? max : phase[i];
            maxIndex = max > phase[i] ? maxIndex : i;
        }

        if (len - maxIndex >= Constants.THRESHOLD_RSSI_MOST_POINTS_NUM_TO_PEAK) {
            maxDataIndex = maxIndex;
            return true;
        }
        return false;
    }
}
