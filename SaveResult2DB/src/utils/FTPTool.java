package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP工具类
 *
 * @author Wing
 * @date 2018.07.19
 */
public class FtpTool {

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url,
                                     int port,
                                     String username,
                                     String password,
                                     String path,
                                     String filename,
                                     InputStream input
    ) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            // 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.connect(url, port);
            // 登录
            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(path);
            ftp.changeWorkingDirectory(path);
            ftp.storeFile(filename, input);
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * Description: 将本地文件上传到FTP服务器上
     *
     * @param url           FTP服务器hostname
     * @param port          FTP服务器端口
     * @param username      FTP登录账号
     * @param password      FTP登录密码
     * @param path          FTP服务器保存目录
     * @param filename      上传到FTP服务器上的文件名
     * @param originFilename 输入流文件名
     */
    public static void upLoadFromProduction(String url,
                                            int port,
                                            String username,
                                            String password,
                                            String path,
                                            String filename,
                                            String originFilename
    ) {
        try {
            FileInputStream in = new FileInputStream(new File(originFilename));
            boolean flag = uploadFile(url, port, username, password, path, filename, in);
            System.out.println(flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试
     */
    public static void main(String[] args) {
        upLoadFromProduction("192.168.13.32", 21, "name", "han", "韩士波测试", "name.doc", "E:/temp/H2数据库使用.doc");
    }
}