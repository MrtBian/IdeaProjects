package cn.edu.nju.iq.listener;

import cn.edu.nju.iq.constants.Constants;
import cn.edu.nju.iq.fitter.RealTimeFitter;
import cn.edu.nju.iq.utils.FileUtil;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TagReportListenerImpl implements TagReportListener {
    private StringBuffer record;
    private long firstReadTime;
    private long timeStamp;
    private long lastTime;
    private DecimalFormat rssiDf;
    private DecimalFormat phaseDf;
    private boolean isFirstRead;

    private boolean isAlreadyFit;

    private RealTimeFitter realTimeFitter;
    private OnGetFirstReadTimeCallback onGetFirstReadTimeCallback;

    public TagReportListenerImpl(RealTimeFitter realTimeFitter, OnGetFirstReadTimeCallback onGetFirstReadTimeCallback) {
        record = new StringBuffer();
        firstReadTime = 0;
        timeStamp = 0;
        lastTime = 0;
        rssiDf = new DecimalFormat(Constants.RSSI_PATTERN);
        phaseDf = new DecimalFormat(Constants.PHASE_PATTERN);
        isFirstRead = true;
        this.realTimeFitter = realTimeFitter;
        this.onGetFirstReadTimeCallback = onGetFirstReadTimeCallback;
        isAlreadyFit = false;
    }

    @Override
    public void onTagReported(ImpinjReader impinjReader, TagReport tagReport) {
        if(isFirstRead) {
            onGetFirstReadTimeCallback.onGetFirstReadTime(System.currentTimeMillis());
        }
        List<Tag> tagList = tagReport.getTags();
        List<String> recordList = new ArrayList<String>();
        for(Tag tag : tagList) {
            if(tag.isFastIdPresent()) {
                synchronized (this) {
                    if(isFirstRead) {
                        firstReadTime = tag.getFirstSeenTime().getLocalDateTime().getTime();
                        isFirstRead = false;
                    }
                }
                timeStamp = tag.getFirstSeenTime().getLocalDateTime().getTime() - firstReadTime;
                record.setLength(0);
                record.append(tag.getEpc()).append(" ").append(rssiDf.format(tag.getPeakRssiInDbm()))
                        .append(" ").append(phaseDf.format(tag.getPhaseAngleInRadians())).append(" ")
                        .append(timeStamp).append(" ").append(tag.getChannelInMhz());
                recordList.add(record.toString());

                synchronized (realTimeFitter) {
                    if(timeStamp - lastTime > Constants.THRESHOLD_MAX_NO_READ_TIME) {
                        realTimeFitter.clear();
                        FileUtil.clearFile(Constants.DATA_FILE_PATH + File.separator + Constants.DATA_FILE_NAME);
                    }
                    lastTime = timeStamp;
                    realTimeFitter.addData(record.toString());
                    if(!isAlreadyFit && realTimeFitter.isPolyFit(Constants.FIT_WAY)) {
                        realTimeFitter.notify();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isAlreadyFit = true;
                    }
                    FileUtil.writeFile(Constants.DATA_FILE_PATH + File.separator + Constants.DATA_FILE_NAME,
                            true, recordList);
                }

//                if(!isAlreadyFit) {
//                    judgeFit();
//                    FileUtil.writeFile(Constants.DATA_FILE_PATH + File.separator + Constants.DATA_FILE_NAME,
//                            true, recordList);
//                }
            }
        }
    }

//    private void judgeFit() {
//        synchronized (realTimeFitter) {
//            if(timeStamp - lastTime > Constants.THRESHOLD_MAX_NO_READ_TIME) {
//                realTimeFitter.clear();
//                FileUtil.clearFile(Constants.DATA_FILE_PATH + File.separator + Constants.DATA_FILE_NAME);
//            }
//            lastTime = timeStamp;
//            realTimeFitter.addData(record.toString());
//            if(realTimeFitter.isPolyFit(Constants.FIT_WAY)) {
//
//                realTimeFitter.notify();
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                isAlreadyFit = true;
//            }
//        }
//    }

    public interface OnGetFirstReadTimeCallback {
        void onGetFirstReadTime(long firstReadTime);
    }
}
