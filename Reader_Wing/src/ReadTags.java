import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;


public class ReadTags implements TagReportListenerImplementation.OnGetStartTimeCallback {
    public static PrintWriter writer;
    public String path = ".\\Data\\";
    private String dataStr = "0523\\";
    public String dataFile =  "test.txt";
    private ReaderSettings setting;

    private long startTime = 0;
    private long endTime = 0;
    private String timeFile =   "time.txt";

    public ReadTags() {
        File file = new File(path+dataStr);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        setting = new ReaderSettings();
    }

    public void reading(){
        try {
            String hostname = setting.hostname;
            ImpinjReader reader = new ImpinjReader();
            reader.connect(hostname);
            reader.applySettings(Settings.load(setting.settingsFilePath));

            writer = new PrintWriter(path+dataStr+dataFile, "UTF-8");

            reader.setTagReportListener(new TagReportListenerImplementation(this));
            reader.start();

            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            reader.stop();
            reader.disconnect();
            writer.close();
//            FileWriter fileWriter = new FileWriter(path+"\\"+timeFile,true);
//            PrintWriter writer1 = new PrintWriter(fileWriter);
            endTime = System.currentTimeMillis();
//            writer1.append(dataFile+" "+startTime+" "+endTime+" "+(endTime-startTime)+"\n");
//            writer1.close();
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        ReadTags readTags = new ReadTags();
        readTags.reading();
    }

    @Override
    public void onGetStartTime(long startTime) {
       this.startTime = startTime;
    }
}
