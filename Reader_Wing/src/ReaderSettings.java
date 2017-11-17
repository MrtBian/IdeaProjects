/**
 * Created by XiaoCong on 2017/1/13.
 * 当阅读器设置发生改变时运行，重新保存设置
 */

import com.impinj.octane.*;

import javax.xml.soap.SAAJResult;
import java.io.IOException;
import java.util.ArrayList;


public class ReaderSettings {

    public static String hostname = "speedwayr-11-1c-1f.local";
    private double powerinDbm = 24;
    private ImpinjReader reader;
    private int tagPopulationEstimate = 4;
    private int session = 0;
    //掩码位数
    private int bitCount = 32;
    //掩码起始位置 是EPC第 bitCount-32 位
    private int bitPointer = 32;
    //掩码的十六进制表示
    private String tagMask = "11070005";
    //设置文件地址
    public static String settingsFilePath = "src/RFID_Reader_Settings.xml";
    public ReaderSettings(){

        System.out.println("Setting Start!");
        reader = new ImpinjReader();
        try {
            reader.connect(hostname);
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        }

        Settings settings = reader.queryDefaultSettings();
        settings.setReaderMode(ReaderMode.MaxThroughput);
        settings.setSearchMode(SearchMode.DualTarget);
        settings.setSession(session);
        settings.setTagPopulationEstimate(tagPopulationEstimate);

        //设置功率
        ArrayList<AntennaConfig> arrayList = settings.getAntennas().getAntennaConfigs();
        for (AntennaConfig ac : arrayList) {
            ac.setIsMaxTxPower(false);
            ac.setTxPowerinDbm(powerinDbm);
            ac.setIsMaxRxSensitivity(true);
        }

        ReportConfig r = settings.getReport();
        r.setIncludeAntennaPortNumber(true);
        r.setIncludeFirstSeenTime(true);
        r.setIncludeLastSeenTime(true);
        r.setIncludeFastId(true);
        r.setIncludePeakRssi(true);
        r.setIncludePcBits(true);
        r.setIncludeChannel(true);
        r.setIncludeDopplerFrequency(true);
        r.setIncludePhaseAngle(true);
        r.setMode(ReportMode.Individual);
        settings.setReport(r);

        TagFilter t1 = settings.getFilters().getTagFilter1();
        t1.setBitCount(bitCount);
        t1.setBitPointer(bitPointer);
        //匹配EPC部分
        t1.setMemoryBank(MemoryBank.Epc);
        t1.setFilterOp(TagFilterOp.Match);
        t1.setTagMask(tagMask);
        settings.getFilters().setMode(TagFilterMode.OnlyFilter1);

        try {
            settings.save(settingsFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.disconnect();
        System.out.println("Setting Done!");
    }

}
