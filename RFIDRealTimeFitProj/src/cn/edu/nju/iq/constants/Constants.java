package cn.edu.nju.iq.constants;

public final class Constants {
//    public static final String HOST_NAME = "speedwayr-11-1c-1f.local";
    public static final String HOST_NAME = "speedwayr-11-1e-9c.local";
    public static final double POWER_IN_DBM = 28;
    public static final int TAG_POPULATION_ESTIMATE = 4;
    public static final int SESSION = 0;
    public static final double FIXED_FREQUENCY = 920.625;

    //掩码位数
    public static final int BIT_COUNT = 32;

    //掩码起始位置 是EPC第 bitCount-32 位
    public static final int BIT_POINTER = 32;

    //掩码的十六进制表示
    public static final String TAG_MASK = "11070002";
//    public static final String TAG_MASK = "11210001";

    //设置文件地址
    public static final String SETTINGS_FILE_NAME = "RFID_Reader_Settings.xml";

    public static final String RSSI_PATTERN = "#0.0";
    public static final String PHASE_PATTERN = "#0.00000";

    public static String DATA_FILE_PATH = "Data";
    public static final String DATA_FILE_NAME = "23.txt";
//    public static final String DATA_FILE_NAME = "test.txt";

    public static final int RSSI_FIT = 0;
    public static final int PHASE_FIT = 1;
    public static final int FIT_WAY = PHASE_FIT;
//    public static final int FIT_WAY = RSSI_FIT;

    public static final double MIN_RSSI = -70.0;

    public static final double V = 4.4179;

    //一些阈值
    /** 最大读不到数据时间，若读不到数据的时间超过此值，就将以前的数据全部清空 **/
    public static final int THRESHOLD_MAX_NO_READ_TIME = 500;
    /**  拟合选取的点的rssi值与峰值的最大差值，拟合时只选取与峰值相差不超过此值的点 **/
    public static final double THRESHOLD_RSSI_MAX_DIFF_TO_PEAK = 10.0;
    /** 拟合需要的最少的点的数量 **/
    public static final int THRESHOLD_MIN_FIT_POINTS_NUM = 20;
    /**  峰值出现后，读到的点的数量超过此值就开始拟合 **/
    public static final int THRESHOLD_RSSI_MOST_POINTS_NUM_TO_PEAK = 20;
    public static final int THRESHOLD_TIME_READ_DATA_AFTER_PEAK_APPEAR = (int) (1000 * 20 / V);
    /**  最小波动值 **/
    public static final double THRESHOLD_MIN_FLUCTUATION = 5.0;
    /**  拟合结束返回峰值后，从峰值的时刻开始再跑此值的时间 **/
    public static final int THRESHOLD_RUN_READ_DATA_AFTER_PEAK_APPEAR = 8 * 1000;
}
