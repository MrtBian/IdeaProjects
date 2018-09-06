package utils;

import java.io.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 用户设置
 *
 * @author Wing
 * @version 1.0, 18/09/01
 */
public class Config {

    /**
     * 0 为文件读取,1为数据库
     */
    public static int FLAG = 1;

    /**
     * xml路径
     */
    private static final String XML_PATH = "config.xml";

    /**
     * 是否第一次盘点
     */
    public static boolean IS_FIRST = true;

    /**
     * 文件参数
     */
    public static String DB_TXT_PATH;

    /**
     * 数据库ip
     */
    public static String HOST;

    /**
     * 数据库端口
     */
    public static int PORT_NO;

    /**
     * 数据库名
     */
    public static String DB_NAME;

    /**
     * 表名
     */
    public static String TABLE_NAME;

    /**
     * 用户名
     */
    public static String USERNAME;

    /**
     * 密码
     */
    public static String PASSWORD;

    public static boolean UPDATE_DATABASE;

    /**
     * xml的Dom树
     */
    private static Document configDom;

    /**
     * Constructs ...
     */
    public Config() {
        try {
            configDom = new SAXReader().read(new File(XML_PATH));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Element config    = configDom.getRootElement();
        Element configEle = config.element("bookInfo");

        FLAG = configEle.element("enableTxt").getText().equals("true")
               ? 0
               : 1;

        Element configDB = configEle.element("databaseConfig");

        HOST        = configDB.element("host").getText();
        PORT_NO     = Integer.parseInt(configDB.element("portNo").getText());
        DB_NAME     = configDB.element("databaseName").getText();
        TABLE_NAME  = configDB.element("tableName").getText();
        USERNAME    = configDB.element("username").getText();
        PASSWORD    = configDB.element("password").getText();
        DB_TXT_PATH = configEle.element("databaseTxtConfig").element("txtPath").getText();
        IS_FIRST = config.element("isFirst").getText().equals("true");
        UPDATE_DATABASE = config.element("updateDatabase").getText().equals("true");
        if(IS_FIRST){
            config.element("isFirst").setText("false");
        }
    }

    public static void saveXml() {
        //指定文件输出的位置
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(XML_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 指定文本的写出的格式：
        OutputFormat format=OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        //1.创建写出对象
        XMLWriter writer= null;
        try {
            writer = new XMLWriter(out,format);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            //2.写出Document对象
            writer.write(configDom);
            //3.关闭流
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method description
     *
     * @param args
     */
    public static void main(String[] args) {
        Config config = new Config();

        System.out.println(Config.IS_FIRST);
        Config.saveXml();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
