package main;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.filechooser.FileSystemView;

import static java.nio.file.StandardWatchEventKinds.*;

import utils.Config;

import static utils.MyLogger.LOGGER;

/**
 * 文件夹监听服务
 *
 * @author Wing
 * @version 1.0, 18/05/10
 */
public class WatchFilePath {

    /** 传输结束标志 */
    private static final String END_FILE = "end";

    /** 监听路径 */
    private static String PATH;

    /** 是否为结果文件 */
    private boolean isRes = false;

    /** 监听服务 */
    private WatchService watcher;

    /**
     * Constructs
     *
     * @param path 监听路径
     *
     * @throws IOException IO异常
     */
    public WatchFilePath(Path path) throws IOException {
        PATH = path.toString();
        watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
    }

    /**
     * Method description
     *
     * @throws InterruptedException
     */
    public void handleEvents() throws InterruptedException {
        String resFile = "";

        LOGGER.info("正在监听文件夹：" + PATH);

        while (true) {
            WatchKey key = watcher.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // 事件可能lost or discarded
                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;
                String fileName = e.context().toString();
                String entryCreate = "ENTRY_CREATE";

                if (entryCreate.equals(kind.name())) {
                    if (fileName.matches(".*\\.res$")) {

                        // 监听到结果文件创建
                        resFile = PATH + File.separator + fileName;
                        isRes = true;
                        LOGGER.info("检测到res文件，等待传输完成...");
                    }

                    if (isRes && fileName.equals(END_FILE)) {

                        // 监听到结束标志文件创建
                        LOGGER.info("检测到end文件，等待解析...");
                        Thread.sleep(100);

                        // 删除结束标志文件
                        new File(PATH + File.separator + fileName).delete();

                        Res2Database res2Database = new Res2Database(resFile);
                        if (Config.UPDATE_DATABASE) {
                            // 写回数据库
                            res2Database.write2DB();
                        }
                        res2Database.generateLossReport();
                        res2Database.generateReportTemp();
                        resFile = "";
                        isRes = false;
                    }
                }
            }

            if (!key.reset()) {
                break;
            }
        }
    }

    /**
     * Method description
     *
     * @param args
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        // String FILEPATH = args[0];
        // String FILEPATH = "C:\\Users\\Wing\\Desktop\\Test\\";
        Config config = new Config();
        String filepath = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\Tooker\\result";
        File file = new File(filepath);

        if (!file.exists()) {
            file.mkdirs();
        }

        new WatchFilePath(Paths.get(filepath)).handleEvents();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
