/**
 * created by wing on 2018.05.10
 * 文件夹监听服务
 */

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
    private static final String ENDFILE = "END";

    public WatchFilePath(Path path) throws IOException {
        this.path = path.toString();
        watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
    }

    public void handleEvents() throws InterruptedException {
        String resFile = "";
        while (true) {
            WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {//事件可能lost or discarded
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;

                String fileName = e.context().toString();
                if (kind.name().equals("ENTRY_CREATE")) {
                    if (fileName.matches(".*\\.res$")) {
                        //监听到结果文件创建
                        resFile = path + fileName;
                    }
                    if (fileName.equals(ENDFILE)) {
                        //监听到结束标志文件创建
                        new File(fileName).delete();//删除结束标志文件
                        Res2DB res2DB = new Res2DB(resFile);
                        res2DB.write2DB();
                        res2DB.generateReport();
                        resFile = "";
                    }
                }
                System.out.printf("Event %s has happened,which fileName is %s%n"
                        , kind.name(), fileName);

            }
            if (!key.reset()) {
                break;
            }
        }
    }

    public static void main(String args[]) throws IOException, InterruptedException {
//        String FILEPATH = "C:\\Users\\Wing\\Desktop\\";
//        new WatchFilePath(Paths.get(FILEPATH).handleEvents();
        new WatchFilePath(Paths.get(args[0])).handleEvents();
    }
}
