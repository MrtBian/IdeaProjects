package cn.edu.nju.iq.reader;

import cn.edu.nju.iq.constants.Constants;
import cn.edu.nju.iq.fitter.RealTimeFitter;
import cn.edu.nju.iq.listener.TagReportListenerImpl;
import cn.edu.nju.iq.utils.FileUtil;
import cn.edu.nju.iq.utils.ReaderSettings;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.Calendar;

public class Reader {
    public static ImpinjReader reader;
    static {
        reader = new ImpinjReader();
    }

    private RealTimeFitter realTimeFitter;

    private long startTime;
    private long endTime;

    public Reader() {
        Calendar calendar = Calendar.getInstance();
        Constants.DATA_FILE_PATH += File.separator
                + (calendar.get(Calendar.MONTH) + 1)
                + (calendar.get(Calendar.DAY_OF_MONTH));
        File file = new File(Constants.DATA_FILE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        FileUtil.clearFile(Constants.DATA_FILE_PATH + File.separator + Constants.DATA_FILE_NAME);
        realTimeFitter = new RealTimeFitter();
        startTime = 0;
        endTime = 0;
    }

    public void reading() {
        try {
            ReaderSettings.init();
            reader.setTagReportListener(new TagReportListenerImpl(realTimeFitter, new TagReportListenerImpl.OnGetFirstReadTimeCallback() {
                @Override
                public void onGetFirstReadTime(long firstReadTime) {
                    startTime = firstReadTime;
                }
            }));
            reader.start();

            int midTime = realTimeFitter.fit(Constants.FIT_WAY);
            System.out.println("midTime: " + midTime);
            endTime = startTime + midTime + Constants.THRESHOLD_RUN_INTERVAL_WHEN_PEAK_APPEAR;
            System.out.println("stop...");
            Thread.sleep(endTime - System.currentTimeMillis());
            System.out.println("stop");

//            stopRun();
            reader.stop();
            reader.disconnect();
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRun() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
