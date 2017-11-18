package cn.edu.nju.iq.utils;

import cn.edu.nju.iq.constants.Constants;
import cn.edu.nju.iq.reader.Reader;
import com.impinj.octane.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ReaderSettings {
    public static void init() {
        System.out.println("Setting Start!");

        try {
            Reader.reader.connect(Constants.HOST_NAME);

            Settings settings = Reader.reader.queryDefaultSettings();
            settings.setReaderMode(ReaderMode.MaxThroughput);
            settings.setSearchMode(SearchMode.DualTarget);
            settings.setSession(Constants.SESSION);
            settings.setTagPopulationEstimate(Constants.TAG_POPULATION_ESTIMATE);
            List<Double> fixedFreqList = new ArrayList<Double>();
            fixedFreqList.add(new Double(Constants.FIXED_FREQUENCY));
            settings.setTxFrequenciesInMhz((ArrayList<Double>) fixedFreqList);

            //设置功率
            ArrayList<AntennaConfig> antennaConfigList = settings.getAntennas().getAntennaConfigs();
            for (AntennaConfig ac : antennaConfigList) {
                ac.setIsMaxTxPower(false);
                ac.setTxPowerinDbm(Constants.POWER_IN_DBM);
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
            t1.setBitCount(Constants.BIT_COUNT);
            t1.setBitPointer(Constants.BIT_POINTER);
            //匹配EPC部分
            t1.setMemoryBank(MemoryBank.Epc);
            t1.setFilterOp(TagFilterOp.Match);
            t1.setTagMask(Constants.TAG_MASK);
            settings.getFilters().setMode(TagFilterMode.OnlyFilter1);

            settings.save(Constants.SETTINGS_FILE_NAME);

            Reader.reader.applySettings(Settings.load(Constants.SETTINGS_FILE_NAME));
        } catch (OctaneSdkException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Setting Done!");
    }
}
