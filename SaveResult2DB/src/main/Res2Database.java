package main;

import jxl.Workbook;
import jxl.write.*;
import utils.Config;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static utils.FileUtil.*;
import static utils.MyLogger.LOGGER;

/**
 * 将盘点结果保存至数据库中，并生成报表
 *
 * @author Wing
 * @version 1.0, 18/05/10
 */
public class Res2Database {
    /**
     * 报表存储路径
     **/
    private String reportPath;
    /**
     * 结果文件路径
     **/
    private String resPath;
    /**
     * 丢失报表路径
     */
    private String lossReportPath;
    /**
     * 文件参数
     **/
    private static String DB_TXT_PATH = "data\\DB_m_transform_tag_2018-06-29.txt";

    /**
     * 是否第一次盘点
     */
    private static boolean IS_FIRST = true;

    /**
     * 丢失书籍集合
     */
    private HashSet<String> lossSet;
    /**
     * 0 为文件读取,1为数据库
     */
    private static int FLAG = 1;
    /**
     * 数据库ip
     **/
    private static String HOST = "202.114.65.49";
    /**
     * 数据库端口
     */
    private static int PORT_NO = 1521;
    /**
     * 数据库名
     */
    private static String DB_NAME = "RFID";
    /**
     * 表名
     */
    private static String TABLE_NAME = "m_transform_tag";
    /**
     * 用户名
     */
    private static String USERNAME = "autopd";
    /**
     * 密码
     */
    private static String PASSWORD = "123456";

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    /**
     * 结果文件解析<br>
     * EPC 区域号 楼层号 列号 排号 架号 层号 顺序号 放错等级 当层数量
     **/
    private String[][] resInfo;
    private static final int FIELD_NUM = 10;


    /**
     * 书籍信息维度
     */
    private static final int BOOK_FIELD_NUM = 12;

    /**
     * 表格列宽
     */
    private static int[] EXCEL_LENGTH = {20, 25, 80, 6, 6, 6, 6, 6, 15};

    /**
     * 书的集合
     */
    private Map<String, String[]> bookMap;
    /**
     * 书的有序列表
     */
    private List<Map.Entry<String, String[]>> bookList;
    /**
     * 对应图书馆数据库字段
     **/
    private static final String[] DB_FIELD_NAME;

    static {
        DB_FIELD_NAME = new String[]{"TAG_ID", "AREANO", "FLOORNO", "COLUMNNO", "ROWNO", "SHELFNO",
                "LAYERNO", "ORDERNO", "ERRORFLAG", "NUM"};
    }

    private static final int SHEET_NUM = 10;
    /**
     * 错误等级说明
     */
    private static final String[] ERROR_LEVEL = {"暂无实际位置", "位置正确", "区域错误", "楼层错误", "列错误", "排错误", "架错误", "层错误", "顺序错误"};

    /**
     * 书籍信息<br>
     * 条形码 索书号 书名 区域号 楼层号 列号 排号 层号 架号 顺序号 放错等级 数量
     */
    private enum BookFieldName {
        /**
         * 条形码
         */
        BOOK_ID("BOOK_ID", 0),
        /**
         * 索书号
         */
        BOOK_INDEX("BOOK_INDEX", 1),
        /**
         * 书名
         */
        BOOK_NAME("BOOK_NAME", 2),
        AREANO("AREANO", 3),
        FLOORNO("FLOORNO", 4),
        COLUMNNO("COLUMNNO", 5),
        ROWNO("ROWNO", 6),
        SHELFNO("SHELFNO", 7),
        LAYERNO("LAYERNO", 8),
        ORDERNO("ORDERNO", 9),
        ERRORFLAG("ERRORFLAG", 10),
        NUM("NUM", 11);
        private String name;
        private int index;

        BookFieldName(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 初始化
     *
     * @param filePath 结果文件路径
     */

    Res2Database(String filePath) {
        this.resPath = filePath;
        String fileName = resPath.replaceAll("^.+\\\\", "").replace(".res", ".xls");
        String lossFileName = fileName.replace(".xls", "_loss.xls");
        String fileDir = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\报表";
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        reportPath = fileDir + File.separator + fileName;
        lossReportPath = fileDir + File.separator + lossFileName;
        LOGGER.info("报表位置：" + reportPath + "\n丢失报表位置：" + lossReportPath);
        initFromXml();
        LOGGER.info("从XML中初始化设置成功！");

        if (FLAG == 0) {
            getResInfoFromTXT();
        } else {
            getResInfo();
        }
    }

    /**
     * method_name: initFromXml
     * package: main
     *
     * @description: 从XML文件初始化设置
     * @author: Wing
     * date: 2018/9/5
     * time: 20:30
     */
    private void initFromXml() {
        DB_TXT_PATH = Config.DB_TXT_PATH;
        FLAG = Config.FLAG;
        HOST = Config.HOST;
        PORT_NO = Config.PORT_NO;
        DB_NAME = Config.DB_NAME;
        TABLE_NAME = Config.TABLE_NAME;
        USERNAME = Config.USERNAME;
        PASSWORD = Config.PASSWORD;
        IS_FIRST = Config.IS_FIRST;
    }

    private void getResInfoFromTXT() {
        //读取TXT中的图书信息
        LOGGER.info("读取TXT中的图书信息...");
        List<String> txtInfos = readFileByLine(DB_TXT_PATH);
        Map<String, String[]> bookInfosTxt = new TreeMap<>();
        for (String data : txtInfos) {
            String[] infos = data.split(";");
            String tagID = infos[0];
            String[] bookInfos = Arrays.copyOfRange(infos, 1, 4);
            bookInfosTxt.put(tagID, bookInfos);
//            if (IS_FIRST) {
//                //初始化丢失列表
//                char[] arr = tagID.toCharArray();
//                if (arr[0] != '8') {
//                    lossSet.add(tagID);
//                }
//            }
        }
//        LOGGER.info("丢失列表初始化成功");
        LOGGER.info("读取成功");
        LOGGER.info("读取res文件...");
        bookMap = new TreeMap<>();
        List<String> result = readFileByLine(resPath);
        LOGGER.info("共有" + result.size() + "条待处理");
        resInfo = new String[result.size()][];
        LOGGER.info("连接数据库...");
        getDBConnection();
        LOGGER.info("连接成功...");
        try {
            statement = connect.createStatement();
            int i = 0;
            int countNotInDB = 0;
            int countRC = 0;
            int countSuccess = 0;
            for (String data : result) {
                String[] bookInfos = new String[BOOK_FIELD_NUM];
                resInfo[i] = data.split(" ");
                String tagID = resInfo[i][0];
                if (tagID.length() > 0) {
                    //处理借出图书，将首位8改为0
                    char[] arr = tagID.toCharArray();
                    arr[0] = arr[0] == '8' ? '0' : arr[0];
                    tagID = new String(arr);
                    if (tagID.matches("^CD[\\d\\w]+$")) {
                        countRC++;
                        LOGGER.info("层架标：" + tagID);
                        continue;
                    }
                }
                //更新丢失列表
                String sql = "UPDATE " + DB_NAME + "." + TABLE_NAME + " SET IS_LOSS='0' WHERE TAG_ID = '" + tagID + "'";
                statement.executeQuery(sql);

                System.arraycopy(resInfo[i], 1, bookInfos, BookFieldName.AREANO.getIndex(), FIELD_NUM - 1);
                //            bookInfos[BookFieldName.LAYERNO.getIndex()] = 6 - Integer.valueOf(bookInfos[BookFieldName
                // .LAYERNO
                //                    .getIndex()]) + "";
                //忽略架号
                //bookInfos[BookFieldName.SHELFNO.getIndex()]="";
                String[] tmp = bookInfosTxt.get(tagID);
                if (tmp == null) {
                    //数据库无此书
                    countNotInDB++;
                    LOGGER.warn("数据库未找到TagID=" + tagID + "的书");
                    continue;
                }
                bookInfos[0] = tmp[0];
                bookInfos[1] = tmp[1];
                bookInfos[2] = tmp[2];
                bookMap.put(tagID, bookInfos);
                countSuccess++;
                i++;
                if (i % 1000 == 0) {
                    LOGGER.info("已处理" + i);
                }
            }
            LOGGER.info("有" + countRC + "个层架标;" + "有" + countNotInDB + "个EPC不在数据库中;" + countSuccess + "本书处理成功！");
            statement.close();
            connect.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }


        LOGGER.info("结果排序中...");
        //排序
        bookList = new ArrayList<>(bookMap.entrySet());
        //升序排序
        bookList.sort((o1, o2) -> {
            String[] b1 = o1.getValue().clone();
            String[] b2 = o2.getValue().clone();
            long i1 = 0, i2 = 0;
            //排序，从楼层号到书架层号
            for (int i3 = BookFieldName.FLOORNO.getIndex(); i3 <= BookFieldName.ORDERNO.getIndex(); i3++) {
                if (i3 == BookFieldName.ORDERNO.getIndex()) {
                    i1 *= 2;
                    i2 *= 2;
                }
                i1 = i1 * 100 + Integer.valueOf(b1[i3]);
                i2 = i2 * 100 + Integer.valueOf(b2[i3]);
            }
            int tmp = (int) (i1 - i2);
            if (tmp == 0) {
                tmp = b1[BookFieldName.BOOK_INDEX.getIndex()].compareTo(b2[BookFieldName.BOOK_INDEX.getIndex()]);
            }
            return tmp;
        });
        LOGGER.info("排序完成");
    }

    /**
     * 从数据中获取图书信息
     */
    private void getResInfo() {
        LOGGER.info("连接数据库...");
        getDBConnection();
        LOGGER.info("连接成功...");
        try {
            statement = connect.createStatement();
//            if (IS_FIRST) {
//                String sql = "SELECT TAG_ID FROM " + DB_NAME + "." + TABLE_NAME;
//                resultSet = statement.executeQuery(sql);
//                while (resultSet.next()) {
//                    String tagID = resultSet.getString("TAG_ID");
//                    char[] arr = tagID.toCharArray();
//                    if (arr[0] != '8') {
//                        lossSet.add(tagID);
//                    }
//                }
//                LOGGER.info("丢失列表初始化成功");
//            }
            bookMap = new TreeMap<>();
            LOGGER.info("读取res文件...");
            List<String> result = readFileByLine(resPath);
            LOGGER.info("共有" + result.size() + "条待处理");
            resInfo = new String[result.size()][];
            int i = 0;
            int countNotInDB = 0;
            int countRC = 0;
            int countSuccess = 0;
            for (String data : result) {
                String[] bookInfos = new String[BOOK_FIELD_NUM];
                resInfo[i] = data.split(" ");
                String tagID = resInfo[i][0];
                if (tagID.length() > 0) {
                    //处理借出图书，将首位8改为0
                    char[] arr = tagID.toCharArray();
                    arr[0] = arr[0] == '8' ? '0' : arr[0];
                    tagID = new String(arr);
                    if (tagID.matches("^CD[\\d\\w]+$")) {
                        //排除层架标
                        countRC++;
                        LOGGER.info("层架标：" + tagID);
                        continue;
                    }
                }
                //更新丢失列表
                String sql = "UPDATE " + DB_NAME + "." + TABLE_NAME + " SET IS_LOSS='0' WHERE TAG_ID = '" + tagID + "'";
                statement.executeQuery(sql);
//                if(lossSet.contains(tagID)){
//                    lossSet.remove(tagID);
//                }
                System.arraycopy(resInfo[i], 1, bookInfos, BookFieldName.AREANO.getIndex(), FIELD_NUM - 1);
                //bookInfos[BookFieldName.LAYERNO.getIndex()] = 6 - Integer.valueOf(bookInfos[BookFieldName.LAYERNO
                // .getIndex()]) + "";
                //忽略架号
                //bookInfos[BookFieldName.SHELFNO.getIndex()]="";
                sql = "SELECT BOOK_ID, BOOK_INDEX, BOOK_NAME FROM " + DB_NAME + "." + TABLE_NAME + " WHERE TAG_ID = '" + tagID + "'";
                resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    bookInfos[0] = resultSet.getString("BOOK_ID");
                    bookInfos[1] = resultSet.getString("BOOK_INDEX");
                    bookInfos[2] = resultSet.getString("BOOK_NAME");
                    bookMap.put(tagID, bookInfos);
                } else {
                    //数据库无此书
                    countNotInDB++;
                    LOGGER.warn("数据库未找到TagID=" + tagID + "的书");
                    continue;
                }
                bookMap.put(tagID, bookInfos);
                i++;
                countSuccess++;
                if (i % 1000 == 0) {
                    LOGGER.info("已处理" + i);
                }
                LOGGER.info("有" + countRC + "个层架标;" + "有" + countNotInDB + "个EPC不在数据库中;" + countSuccess + "本书处理成功！");
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("结果排序中...");
        //排序
        bookList = new ArrayList<>(bookMap.entrySet());
        //升序排序
        bookList.sort((o1, o2) -> {
            String[] b1 = o1.getValue().clone();
            String[] b2 = o2.getValue().clone();
            long i1 = 0, i2 = 0;
            //排序，从楼层号到书架层号
            for (int i = BookFieldName.FLOORNO.getIndex(); i <= BookFieldName.ORDERNO.getIndex(); i++) {
                i1 = i1 * 1000 + Integer.valueOf(b1[i]);
                i2 = i2 * 1000 + Integer.valueOf(b2[i]);
            }
            int tmp = (int) (i1 - i2);
            if (tmp == 0) {
                tmp = b1[BookFieldName.BOOK_INDEX.getIndex()].compareTo(b2[BookFieldName.BOOK_INDEX.getIndex()]);
            }
            return tmp;
        });
        LOGGER.info("排序完成");
    }

    /**
     * 将位置信息更改写回数据库
     */
    public void write2DB() {
        /*
         * 写入数据库暂时不启用
         */
        LOGGER.info("将更改写回数据库");
        LOGGER.info("连接数据库...");
        getDBConnection();
        LOGGER.info("连接成功...");
        try {
            statement = connect.createStatement();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        for (Map.Entry<String, String[]> entry : bookList) {
            String tagID = entry.getKey();
            String[] bookInfos = entry.getValue();
            String bookPlace = locationEncode(bookInfos);
            String sql = "UPDATE " + DB_NAME + "." + TABLE_NAME + " SET " + "BOOK_PLACE" + "='" + bookPlace + "' " +
                    "WHERE " + DB_FIELD_NAME[0] + "='" + tagID + "'";
            LOGGER.info(sql);

            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }

        }
        try {
            statement.close();
            connect.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 通过EPC来获取图书信息
     *
     * @param tagID EPC号
     *
     * @return 书ID 书索引号 书名
     */
    private String getBookInfo(String tagID) {
        String bookID = "", bookIndex = "", bookName = "";
        getDBConnection();
        try {
            statement = connect.createStatement();
            String sql = "SELECT BOOK_ID, BOOK_INDEX, BOOK_NAME FROM " + DB_NAME + "." + TABLE_NAME + " WHERE TAG_ID " +
                    "" + "" + "= '" + tagID + "'";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                bookID = resultSet.getString(BookFieldName.BOOK_ID.getName());
                bookIndex = resultSet.getString(BookFieldName.BOOK_INDEX.getName());
                bookName = resultSet.getString(BookFieldName.BOOK_NAME.getName());
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return bookID + " " + bookIndex + " " + bookName;
    }

    /**
     * 按行和列生成报表
     */
    public void generateReportTemp() {
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(new File(reportPath));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        //        int i = 0;
        int sheetNum = 0;
        //错误列表
        assert book != null;
        WritableSheet sheetErr = book.createSheet("错架列表", sheetNum);
        sheetNum++;
        WritableCellFormat format1 = new WritableCellFormat();
        try {
            format1.setAlignment(Alignment.CENTRE);
        } catch (WriteException e) {
            LOGGER.error(e.getMessage());
        }
        // Label labTitle_ = new Label(0, 0, "架号"+data[3]+" 层号"+data[4]);
        Label labBookIDText = new Label(0, 0, "条形码", format1);
        Label labBookIndexText = new Label(1, 0, "索书号", format1);
        Label labBookNameText = new Label(2, 0, "书名", format1);
        Label labColumnNoText = new Label(3, 0, "列号", format1);
        Label labRowNoText = new Label(4, 0, "排号", format1);
        Label labShelfNoText = new Label(5, 0, "架号", format1);
        Label labLayerNoText = new Label(6, 0, "层号", format1);
        Label labOrderNoText = new Label(7, 0, "顺序号", format1);
        Label labNumText = new Label(8, 0, "书格图书总数", format1);
        Label[] labelsText = {labBookIDText, labBookIndexText, labBookNameText, labColumnNoText, labRowNoText, labShelfNoText,
                labLayerNoText, labOrderNoText, labNumText};
        try {

            for (Label aLabelsText : labelsText) {
                sheetErr.addCell(aLabelsText);
            }


        } catch (WriteException e) {
            LOGGER.error(e.getMessage());
        }
        //表格格式设置，固定列宽和自动换行
        for (int i = 0; i < EXCEL_LENGTH.length; i++) {
            sheetErr.setColumnView(i, EXCEL_LENGTH[i]);
        }
        for (Map.Entry<String, String[]> entry : bookList) {
            String tagID = entry.getKey();
            String[] bookInfos = entry.getValue();
            String tmp = bookInfos[BookFieldName.COLUMNNO.getIndex()] + "列 " + bookInfos[BookFieldName.ROWNO.getIndex
                    ()] + "排";
            WritableSheet sheet;
            if ((sheet = book.getSheet(tmp)) == null) {
                //不存在此sheet
                sheet = book.createSheet(tmp, sheetNum);
                sheetNum++;

                // Label labTitle_ = new Label(0, 0, "架号"+data[3]+" 层号"+data[4]);
                labBookIDText = new Label(0, 0, "条形码", format1);
                labBookIndexText = new Label(1, 0, "索书号", format1);
                labBookNameText = new Label(2, 0, "书名", format1);
                labColumnNoText = new Label(3, 0, "列号", format1);
                labRowNoText = new Label(4, 0, "排号", format1);
                labShelfNoText = new Label(5, 0, "架号", format1);
                labLayerNoText = new Label(6, 0, "层号", format1);
                labOrderNoText = new Label(7, 0, "顺序号", format1);
                Label[] newLabelsText = {labBookIDText, labBookIndexText, labBookNameText, labColumnNoText, labRowNoText, labShelfNoText,
                        labLayerNoText, labOrderNoText};
                try {
                    for (Label aNewLabelsText : newLabelsText) {
                        sheet.addCell(aNewLabelsText);
                    }

                } catch (WriteException e) {
                    LOGGER.error(e.getMessage());
                }
                //表格格式设置，固定列宽和自动换行
                for (int i = 0; i < EXCEL_LENGTH.length; i++) {
                    sheet.setColumnView(i, EXCEL_LENGTH[i]);
                }
            }
            //定义样式
            WritableCellFormat format = new WritableCellFormat();
            // true自动换行，false不自动换行
            try {
                format.setWrap(true);
            } catch (WriteException e) {
                LOGGER.error(e.getMessage());
            }

            //添加书本信息
            int rowNo = sheet.getRows();
            if (bookInfos[BookFieldName.BOOK_ID.getIndex()] == null) {
                //数据库图书未找到
                continue;
            }
            Label labBookID = new Label(0, rowNo, bookInfos[BookFieldName.BOOK_ID.getIndex()], format);
            Label labBookIndex = new Label(1, rowNo, bookInfos[BookFieldName.BOOK_INDEX.getIndex()], format);
            Label labBookName = new Label(2, rowNo, bookInfos[BookFieldName.BOOK_NAME.getIndex()], format);
            Label labColumnNo = new Label(3, rowNo, bookInfos[BookFieldName.COLUMNNO.getIndex()], format);
            Label labRowNo = new Label(4, rowNo, bookInfos[BookFieldName.ROWNO.getIndex()], format);
            Label labShelfNo = new Label(5, rowNo, bookInfos[BookFieldName.SHELFNO.getIndex()], format);
            Label labLayerNo = new Label(6, rowNo, bookInfos[BookFieldName.LAYERNO.getIndex()], format);
            Label labOrderNo = new Label(7, rowNo, bookInfos[BookFieldName.ORDERNO.getIndex()], format);
            Label[] labels = {labBookID, labBookIndex, labBookName, labColumnNo, labRowNo, labShelfNo, labLayerNo,
                    labOrderNo};
            try {
                for (Label label : labels) {
                    sheet.addCell(label);
                }
            } catch (WriteException e) {
                LOGGER.error(e.getMessage());
            }
            String str1 = "1";
            if (str1.equals(bookInfos[BookFieldName.ERRORFLAG.getIndex()])) {
                //将错架图书加入错架列表
                rowNo = sheetErr.getRows();
                labBookID = new Label(0, rowNo, bookInfos[BookFieldName.BOOK_ID.getIndex()], format);
                labBookIndex = new Label(1, rowNo, bookInfos[BookFieldName.BOOK_INDEX.getIndex()], format);
                labBookName = new Label(2, rowNo, bookInfos[BookFieldName.BOOK_NAME.getIndex()], format);
                labColumnNo = new Label(3, rowNo, bookInfos[BookFieldName.COLUMNNO.getIndex()], format);
                labRowNo = new Label(4, rowNo, bookInfos[BookFieldName.ROWNO.getIndex()], format);
                labShelfNo = new Label(5, rowNo, bookInfos[BookFieldName.SHELFNO.getIndex()], format);
                labLayerNo = new Label(6, rowNo, bookInfos[BookFieldName.LAYERNO.getIndex()], format);
                labOrderNo = new Label(7, rowNo, bookInfos[BookFieldName.ORDERNO.getIndex()], format);
                Label labNum = new Label(8, rowNo, bookInfos[BookFieldName.NUM.getIndex()], format);
                //                Label labEPC = new Label(9,rowNo,tagID,format);
                Label[] newLabels = {labBookID, labBookIndex, labBookName, labColumnNo, labRowNo, labShelfNo,
                        labLayerNo, labOrderNo, labNum
                        /*,labEPC*/};
                try {

                    for (Label newLabel : newLabels) {
                        sheetErr.addCell(newLabel);
                    }
                } catch (WriteException e) {
                    LOGGER.error(e.getMessage());
                }
            }
            //            i++;
        }
        try {
            book.write();
            book.close();
        } catch (IOException | WriteException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("报表生成！");
        LOGGER.info("正在监听...");
    }

    /**
     * 生成丢失报表
     */
    public void generateLossReport() {
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(new File(lossReportPath));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        int sheetNum = 0;
        //丢失列表
        assert book != null;
        WritableSheet sheetLoss = book.createSheet("丢失列表", sheetNum);
        sheetNum++;
        //定义样式
        WritableCellFormat format = new WritableCellFormat();
        // true自动换行，false不自动换行
        try {
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error(e.getMessage());
        }
        WritableCellFormat format1 = new WritableCellFormat();
        try {
            format1.setAlignment(Alignment.CENTRE);
        } catch (WriteException e) {
            LOGGER.error(e.getMessage());
        }
        // Label labTitle_ = new Label(0, 0, "架号"+data[3]+" 层号"+data[4]);
        Label labBookIDText = new Label(0, 0, "条形码", format1);
        Label labBookIndexText = new Label(1, 0, "索书号", format1);
        Label labBookNameText = new Label(2, 0, "书名", format1);
//        Label labColumnNoText = new Label(3, 0, "列号", format1);
//        Label labRowNoText = new Label(4, 0, "排号", format1);
//        Label labShelfNoText = new Label(5, 0, "架号", format1);
//        Label labLayerNoText = new Label(6, 0, "层号", format1);
//        Label labOrderNoText = new Label(7, 0, "顺序号", format1);
//        Label labNumText = new Label(8, 0, "书格图书总数", format1);
        Label[] labelsText = {labBookIDText, labBookIndexText, labBookNameText};
        try {

            for (Label aLabelsText : labelsText) {
                sheetLoss.addCell(aLabelsText);
            }


        } catch (WriteException e) {
            LOGGER.error(e.getMessage());
        }
        //表格格式设置，固定列宽和自动换行
        for (int i = 0; i < EXCEL_LENGTH.length; i++) {
            sheetLoss.setColumnView(i, EXCEL_LENGTH[i]);
        }
        LOGGER.info("连接数据库...");
        getDBConnection();
        LOGGER.info("连接成功...");
        try {
            statement = connect.createStatement();
            //限制A馆
            String sql = "SELECT BOOK_ID, BOOK_INDEX, BOOK_NAME FROM " + DB_NAME + "." + TABLE_NAME + " WHERE IS_LOSS <> '0' or amount is null ";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String bookID = resultSet.getString(BookFieldName.BOOK_ID.getName());
                String bookIndex = resultSet.getString(BookFieldName.BOOK_INDEX.getName());
                String bookName = resultSet.getString(BookFieldName.BOOK_NAME.getName());
                int rowNo = sheetLoss.getRows();
                Label labBookID = new Label(0, rowNo, bookID, format);
                Label labBookIndex = new Label(1, rowNo, bookIndex, format);
                Label labBookName = new Label(2, rowNo, bookName, format);
//                Label labColumnNo = new Label(3, rowNo, bookInfos[BookFieldName.COLUMNNO.getIndex()], format);
//                Label labRowNo = new Label(4, rowNo, bookInfos[BookFieldName.ROWNO.getIndex()], format);
//                Label labShelfNo = new Label(5, rowNo, bookInfos[BookFieldName.SHELFNO.getIndex()], format);
//                Label labLayerNo = new Label(6, rowNo, bookInfos[BookFieldName.LAYERNO.getIndex()], format);
//                Label labOrderNo = new Label(7, rowNo, bookInfos[BookFieldName.ORDERNO.getIndex()], format);
                Label[] labels = {labBookID, labBookIndex, labBookName};
                try {
                    for (Label label : labels) {
                        sheetLoss.addCell(label);
                    }
                } catch (WriteException e) {
                    LOGGER.error(e.getMessage());
                }
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        try {
            book.write();
            book.close();
        } catch (IOException | WriteException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("丢失报表生成！");
    }

    /**
     * 将位置信息解码
     *
     * @param code 位置信息的编码
     *
     * @return 图书信息数组
     */
    public String[] locationDecode(String code) {
        String[] bookPlace = new String[6];
        bookPlace[0] = code.substring(2, 3);
        bookPlace[1] = code.substring(3, 4);
        bookPlace[2] = code.substring(5, 6);
        for (int i = 6; i < 12; i++) {
            bookPlace[i / 2] = Integer.parseInt(code.substring(i, i + 2)) + "";
            i++;
        }
        return bookPlace;
    }

    /**
     * 将位置信息进行编码
     *
     * @param bookInfos 图书信息数组
     *
     * @return 编码字符串
     */
    private String locationEncode(String[] bookInfos) {
        StringBuilder code = new StringBuilder("WL");
        for (int i = BookFieldName.AREANO.getIndex(); i <= BookFieldName.LAYERNO.getIndex(); i++) {
            if (i == BookFieldName.FLOORNO.getIndex()) {
                code.append(bookInfos[i]).append("F");
            } else if (i == BookFieldName.AREANO.getIndex() || i == BookFieldName.COLUMNNO.getIndex()) {
                code.append(bookInfos[i]);
            } else {
                String tmp = bookInfos[i];
                if (tmp.length() == 1) {
                    tmp = "0" + tmp;
                }
                code.append(tmp);
            }
        }
        return code.toString();
    }


    /**
     * 判断EPC是否正确
     *
     * @param epc epc
     *
     * @return EPC是否符合规则
     */
    private boolean isEpc(String epc) {
        /*之后可采用正则表达式判断*/
        return epc.length() >= 20;
    }

    private void getDBConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        try {
            //实例化驱动程序类
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //驱动程序名：@主机名/IP：端口号：数据库实例名
            String url = "jdbc:oracle:thin:@" + HOST + ":" + PORT_NO + ":" + DB_NAME;
            connect = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Res2Database res2Database = new Res2Database("C:\\Users\\Wing\\Desktop\\result\\name.res");
    }
}
