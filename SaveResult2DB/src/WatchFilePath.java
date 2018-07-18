/**
 * created by wing on 2018.05.10
 * 文件夹监听服务
 */

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

public class WatchFilePath {

    private WatchService watcher;
    private String path;
    private static final String END_FILE = "end";
    private boolean isRes = false;

    public WatchFilePath(Path path) throws IOException {
        this.path = path.toString();
        watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
    }

    public void handleEvents() throws InterruptedException {
        String resFile = "";

        System.out.println("正在监听...");
        while (true) {
            WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {//事件可能lost or discarded
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;

                String fileName = e.context().toString();
//                System.out.printf("Event %s has happened,which fileName is %s%n"
//                        , kind.name(), fileName);
                if (kind.name().equals("ENTRY_CREATE")) {
                    if (fileName.matches(".*\\.res$")) {
                        //监听到结果文件创建
                        resFile = path + File.separator + fileName;
                        isRes = true;
                        System.out.println("检测到res文件，等待传输完成...");
                    }
                    if (isRes && fileName.equals(END_FILE)) {
                        //监听到结束标志文件创建

                        System.out.println("检测到end文件，等待解析...");
                        Thread.sleep(100);
                        new File(path + File.separator + fileName).delete();//删除结束标志文件
                        Res2DB res2DB = new Res2DB(resFile);
                        //写回数据库
                        res2DB.write2DB();
                        res2DB.generateReportTemp();
//                        res2DB.generateReport();
                        resFile = "";
                        isRes = false;
                    }
                }
//                System.out.println("监听继续...");

            }
            if (!key.reset()) {
                break;
            }
        }
    }

    public static void main(String args[]) throws IOException, InterruptedException {
//        String FILEPATH = args[0];
//        String FILEPATH = "C:\\Users\\Wing\\Desktop\\Test\\";
        String FILEPATH = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\Tooker\\result";
        File file = new File(FILEPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        new WatchFilePath(Paths.get(FILEPATH)).handleEvents();
    }
}
