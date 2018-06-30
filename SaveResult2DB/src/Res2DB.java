/**
 * created by wing on 2018.05.10
 * 将盘点结果保存至数据库中，并生成报表
 */

import jxl.Workbook;
import jxl.write.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static utils.FileUtil.*;


class Res2DB {
    /**
     * 报表存储路径
     **/
    private String reportPath;
    /**
     * 结果文件路径
     **/
    private String resPath;

    /**
     * 文件参数
     **/
    private final static String DB_TXT_PATH = "data\\DB_m_transform_tag_2018-06-11.txt";
    private int flag = 0;//0 为文件读取
    /**
     * 数据库参数
     **/
    private static final String HOST = "202.114.65.49";
    private static final int PORT_NO = 1521;
    private static final String DB_NAME = "RFID";
    private static final String TABLE_NAME = "m_transform_tag";
    private static final String USERNAME = "autopd";
    private static final String PASSWORD = "123456";

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    /**
     * 结果文件解析
     **/
    /*EPC 区域号 楼层号 列号 排号 架号 层号 顺序号 放错等级*/
    private String[][] resInfo;
    private static final int FIELD_NUM = 9;

    /**
     * 书籍信息
     */
    /*条形码 索书号 书名 区域号 楼层号 列号 排号 层号 架号 顺序号 放错等级*/
    private enum BookFieldName {
        BOOK_ID("BOOK_ID", 0), BOOK_INDEX("BOOK_INDEX", 1), BOOK_NAME("BOOK_NAME", 2), AREANO("AREANO", 3), FLOORNO
                ("FLOORNO", 4), COLUMNNO("COLUMNNO", 5), ROWNO("ROWNO", 6), SHELFNO("SHELFNO", 7), LAYERNO("LAYERNO",
                8), ORDERNO("ORDERNO", 9), ERRORFLAG("ERRORFLAG", 10);
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

    //表格列宽
    private static int[] EXCEL_LENGTH = {20, 25, 80, 10, 10, 10};

    private static final int BOOK_FIELD_NUM = 11;
    private Map<String, String[]> bookMap;
    private List<Map.Entry<String, String[]>> bookList;
    /**
     * 对应图书馆数据库字段
     **/
    private static final String DB_FIELD_NAME[] = {"TAG_ID", "AREANO", "FLOORNO", "COLUMNNO", "ROWNO", "SHELFNO",
            "LAYERNO", "ORDERNO", "ERRORFLAG"};
    //
    private static final int SHEET_NUM = 10;
    /**
     * 错误等级说明
     */
    private static final String ERROR_LEVEL[] = {"暂无实际位置", "位置正确", "区域错误", "楼层错误", "列错误", "排错误", "架错误", "层错误", "顺序错误"};

    /**
     * 初始化
     *
     * @param filePath 结果文件路径
     */

    Res2DB(String filePath) {
        this.resPath = filePath;
        reportPath = resPath.replace(".res", ".xls").replace("result", "report");
        System.out.println(reportPath);
        if(flag == 0){
            getResInfoFromTXT();
        }
        else {
            getResInfo();
        }
    }

    public void getResInfoFromTXT() {
        //读取TXT中的图书信息
        System.out.println("读取TXT中的图书信息...");
        List<String> txtInfos = readFileByLine(DB_TXT_PATH);
        Map<String, String[]> bookInfosTxt = new HashMap<String, String[]>();
        for (String data : txtInfos) {
            String[] infos = data.split(";");
            String tagID = infos[0];
            String[] bookInfos = Arrays.copyOfRange(infos, 1, 4);
            bookInfosTxt.put(tagID, bookInfos);
        }
        System.out.println("读取成功");
        bookMap = new HashMap<>();
        List<String> result = readFileByLine(resPath);
        resInfo = new String[result.size()][];
        int i = 0;
        for (String data : result) {
            String[] bookInfos = new String[BOOK_FIELD_NUM];
            resInfo[i] = data.split(" ");
            //tagIDs[i] = "\"" + resInfo[i][0] + "\"";
            String tagID = resInfo[i][0];
            //处理借出图书，将首位8改为0
            char[] arr = tagID.toCharArray();
            arr[0] = arr[0]=='8'?'0':arr[0];
            tagID = new String (arr);

            System.arraycopy(resInfo[i], 1, bookInfos, BookFieldName.AREANO.getIndex(), FIELD_NUM - 2);
//            bookInfos[BookFieldName.LAYERNO.getIndex()] = 6 - Integer.valueOf(bookInfos[BookFieldName.LAYERNO
//                    .getIndex()]) + "";
            //忽略架号
            //bookInfos[BookFieldName.SHELFNO.getIndex()]="";
            String[] tmp = bookInfosTxt.get(tagID);
            if(tmp == null){
                //数据库无此书
                continue;
            }
            bookInfos[0] = tmp[0];
            bookInfos[1] = tmp[1];
            bookInfos[2] = tmp[2];
            bookMap.put(tagID, bookInfos);
            i++;
            if (i % 1000 == 0) System.out.println(i);
        }


        //排序
        bookList = new ArrayList<Map.Entry<String, String[]>>(bookMap.entrySet());
        Collections.sort(bookList, new Comparator<Map.Entry<String, String[]>>() {
            //升序排序
            public int compare(Map.Entry<String, String[]> o1, Map.Entry<String, String[]> o2) {
                String[] b1 = o1.getValue().clone();
                String[] b2 = o2.getValue().clone();
                long i1=0,i2=0;
                //排序，从楼层号到书架层号
                for(int i=BookFieldName.FLOORNO.getIndex();i<=BookFieldName.ORDERNO.getIndex();i++){
                    i1 = i1*100 + Integer.valueOf(b1[i]);
                    i2 = i2*100 + Integer.valueOf(b2[i]);
                }
                int tmp = (int)(i1-i2);
                if(tmp==0){
                    tmp = b1[BookFieldName.BOOK_INDEX.getIndex()].compareTo(b2[BookFieldName.BOOK_INDEX.getIndex()]);
                }
                return tmp;
            }

        });
    }

    /**
     *从数据中获取图书信息
     */
    public void getResInfo() {
        getDBConnection();
        try {
            statement = connect.createStatement();
            bookMap = new HashMap<>();
            List<String> result = readFileByLine(resPath);
            resInfo = new String[result.size()][];
            int i = 0;
            for (String data : result) {
                String[] bookInfos = new String[BOOK_FIELD_NUM];
                resInfo[i] = data.split(" ");
                //            tagIDs[i] = "\"" + resInfo[i][0] + "\"";
                String tagID = resInfo[i][0];
                //处理借出图书，将首位8改为0
                char[] arr = tagID.toCharArray();
                arr[0] = arr[0]=='8'?'0':arr[0];
                tagID = new String (arr);

                System.arraycopy(resInfo[i], 1, bookInfos, BookFieldName.AREANO.getIndex(), FIELD_NUM - 2);
//                bookInfos[BookFieldName.LAYERNO.getIndex()] = 6 - Integer.valueOf(bookInfos[BookFieldName.LAYERNO
//                        .getIndex()]) + "";
                //忽略架号
                //                bookInfos[BookFieldName.SHELFNO.getIndex()]="";
                String sql = "SELECT BOOK_ID, BOOK_INDEX, BOOK_NAME FROM " + DB_NAME + "." + TABLE_NAME + " WHERE " +
                        "" + "TAG_ID = '" + tagID + "'";
                //                System.out.println(sql);
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    bookInfos[0] = resultSet.getString("BOOK_ID");
                    bookInfos[1] = resultSet.getString("BOOK_INDEX");
                    bookInfos[2] = resultSet.getString("BOOK_NAME");
                    bookMap.put(tagID, bookInfos);
                }
                bookMap.put(tagID, bookInfos);
                i++;
                if (i % 100 == 0) System.out.println(i);
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //排序
        bookList = new ArrayList<Map.Entry<String, String[]>>(bookMap.entrySet());
        Collections.sort(bookList, new Comparator<Map.Entry<String, String[]>>() {
            //升序排序
            public int compare(Map.Entry<String, String[]> o1, Map.Entry<String, String[]> o2) {
                String[] b1 = o1.getValue().clone();
                String[] b2 = o2.getValue().clone();
                long i1=0,i2=0;
                //排序，从楼层号到书架层号
                for(int i=BookFieldName.FLOORNO.getIndex();i<=BookFieldName.ORDERNO.getIndex();i++){
                    i1 = i1*100 + Integer.valueOf(b1[i]);
                    i2 = i2*100 + Integer.valueOf(b2[i]);
                }
                int tmp = (int)(i1-i2);
                if(tmp==0){
                    tmp = b1[BookFieldName.BOOK_INDEX.getIndex()].compareTo(b2[BookFieldName.BOOK_INDEX.getIndex()]);
                }
                return tmp;
            }

        });
    }

    /**
     * 将位置信息更改写回数据库
     */
    public void write2DB() {
        /**
         * 写入数据库暂时不启用
         */
        getDBConnection();
        try {
            statement = connect.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String[] data : resInfo) {
            if (isEpc(data[0])) {
                String sql = "UPDATE " + DB_NAME + "." + TABLE_NAME + " SET " + DB_FIELD_NAME[1] + "='" + data[1] +
                        "', " + DB_FIELD_NAME[2] + "='" + data[2] + "', " + DB_FIELD_NAME[3] + "='" + data[3] + "', "
                        + DB_FIELD_NAME[4] + "='" + data[4] + "', " + DB_FIELD_NAME[5] + "='" + data[5] + "', " +
                        DB_FIELD_NAME[6] + "='" + data[6] + "', " + DB_FIELD_NAME[7] + "='" + data[7] + "' " +
                        "WHERE" + " " + DB_FIELD_NAME[0] + "='" + data[0] + "'";
                //System.out.println(sql);

                try {
                    statement.executeUpdate(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过EPC来获取图书信息
     *
     * @param tagID EPC号
     * @return 书ID 书索引号 书名
     */
    private String getBookInfo(String tagID) {
        String bookID = "", bookIndex = "", bookName = "";
        getDBConnection();
        try {
            statement = connect.createStatement();
            String sql = "SELECT BOOK_ID, BOOK_INDEX, BOOK_NAME FROM " + DB_NAME + "." + TABLE_NAME + " WHERE TAG_ID " +
                    "" + "= '" + tagID + "'";
            //            System.out.println(sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                bookID = resultSet.getString(BookFieldName.BOOK_ID.getName());
                bookIndex = resultSet.getString(BookFieldName.BOOK_INDEX.getName());
                bookName = resultSet.getString(BookFieldName.BOOK_NAME.getName());
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookID + " " + bookIndex + " " + bookName;
    }

    public void generateReportTemp() {
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(new File(reportPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        int i = 0;
        int sheetNum = 0;
        for (Map.Entry<String, String[]> entry : bookList) {
            String tagID = entry.getKey();
            String[] bookInfos = entry.getValue();
            String tmp = bookInfos[BookFieldName.COLUMNNO.getIndex()] + "列 " + bookInfos[BookFieldName.ROWNO.getIndex
                    ()] + "排";
            WritableSheet sheet = null;
            if ((sheet = book.getSheet(tmp)) == null) {
                //不存在此sheet
                sheet = book.createSheet(tmp, sheetNum);
                sheetNum++;
                //添加此sheet信息
                //定义样式
                WritableCellFormat format1 = new WritableCellFormat();
                try {
                    format1.setAlignment(Alignment.CENTRE);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                // Label labTitle_ = new Label(0, 0, "架号"+data[3]+" 层号"+data[4]);
                Label labBookID_ = new Label(0, 0, "条形码",format1);
                Label labBookIndex_ = new Label(1, 0, "索书号",format1);
                Label labBookName_ = new Label(2, 0, "书名",format1);
                Label labShelfNo_ = new Label(3, 0, "架号",format1);
                Label labLayerNo_ = new Label(4, 0, "层号",format1);
                Label labOrderNo_ = new Label(5, 0, "顺序号",format1);
                try {
                    //sheet.addCell(labTitle_);
                    sheet.addCell(labBookID_);
                    sheet.addCell(labBookIndex_);
                    sheet.addCell(labBookName_);
                    sheet.addCell(labShelfNo_);
                    sheet.addCell(labLayerNo_);
                    sheet.addCell(labOrderNo_);


                } catch (WriteException e) {
                    e.printStackTrace();
                }
                //表格格式设置，固定列宽和自动换行
                for (int i = 0; i < EXCEL_LENGTH.length; i++) {
                    sheet.setColumnView(i, EXCEL_LENGTH[i]);
                }
            }
            //定义样式
            WritableCellFormat format = new WritableCellFormat();
            // true自动换号，false不自动换行
            try {
                format.setWrap(true);
            } catch (WriteException e) {
                e.printStackTrace();
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
            Label labLayerNo = new Label(4, rowNo, bookInfos[BookFieldName.LAYERNO.getIndex()], format);
            Label labShelfNo = new Label(3, rowNo, bookInfos[BookFieldName.SHELFNO.getIndex()],format);
            Label labOrderNo = new Label(5, rowNo, bookInfos[BookFieldName.ORDERNO.getIndex()],format);
            try {
                sheet.addCell(labBookID);
                sheet.addCell(labBookIndex);
                sheet.addCell(labBookName);
                sheet.addCell(labShelfNo);
                sheet.addCell(labLayerNo);
                sheet.addCell(labOrderNo);
            } catch (WriteException e) {
                e.printStackTrace();
            }
            //            i++;
        }
        try {
            book.write();
            book.close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
        System.out.println("报表生成！");
        System.out.println("正在监听...");
    }

    public void generateReport() {
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(new File(reportPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WritableSheet[] sheets = new WritableSheet[SHEET_NUM];
        for (int i = 0; i < SHEET_NUM; i++) {
            Label labBookID = new Label(0, 0, "BOOK_ID");
            Label labBookIndex = new Label(1, 0, "BOOK_INDEX");
            Label labBookName = new Label(2, 0, "BOOK_NAME");
            Label labError = new Label(3, 0, "ERROR");

            assert book != null;
            sheets[i] = book.createSheet("sheet" + (i + 1), 0);
            try {
                sheets[i].addCell(labBookID);
                sheets[i].addCell(labBookIndex);
                sheets[i].addCell(labBookName);
                if (i == 0) {
                    sheets[0].addCell(labError);
                }
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }

        for (String[] data : resInfo) {
            String[] tmp = getBookInfo(data[0]).split(" ");
            if (tmp.length == 0) {
                //数据库中无此书
                continue;
            }
            String bookID = tmp[0], bookIndex = tmp[1], bookName = tmp[2];
            int errorNo = Integer.valueOf(data[data.length - 1]);
            //写入总表之中
            int rowNo_ = sheets[errorNo + 2].getRows();
            Label id_ = new Label(0, rowNo_, bookID);
            Label index_ = new Label(1, rowNo_, bookIndex);
            Label name_ = new Label(2, rowNo_, bookName);
            Label error_ = new Label(3, rowNo_, ERROR_LEVEL[errorNo + 1]);
            try {
                sheets[0].addCell(id_);
                sheets[0].addCell(index_);
                sheets[0].addCell(name_);
                sheets[0].addCell(error_);
            } catch (WriteException e) {
                e.printStackTrace();
            }
            //写入对应sheet之中
            int rowNo = sheets[errorNo + 2].getRows();
            Label id = new Label(0, rowNo, bookID);
            Label index = new Label(1, rowNo, bookIndex);
            Label name = new Label(2, rowNo, bookName);
            try {
                sheets[errorNo + 2].addCell(id);
                sheets[errorNo + 2].addCell(index);
                sheets[errorNo + 2].addCell(name);
            } catch (WriteException e) {
                e.printStackTrace();
            }

        }
        try {
            book.write();
            book.close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
        System.out.println("");
    }


    /**
     * 判断EPC是否正确
     *
     * @param epc
     * @return
     */
    private boolean isEpc(String epc) {
        /*之后可采用正则表达式判断*/
        return epc.length() >= 20;
    }

    //    private void getDBConnection(String host, int port, String dbName, String user, String password) {
    //        try {
    //            Class.forName("oracle.jdbc.driver.OracleDriver");
    //            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());//实例化驱动程序类
    //            String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;//驱动程序名：@主机名/IP：端口号：数据库实例名
    //            connect = DriverManager.getConnection(url, user, password);
    //        } catch (SQLException e) {
    //            e.printStackTrace();
    //        } catch (ClassNotFoundException e) {
    //            e.printStackTrace();
    //        }
    //    }

    private void getDBConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());//实例化驱动程序类
            String url = "jdbc:oracle:thin:@" + HOST + ":" + PORT_NO + ":" + DB_NAME;//驱动程序名：@主机名/IP：端口号：数据库实例名
            connect = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Res2DB res2DB = new Res2DB("C:\\Users\\Wing\\Desktop\\result\\xxxx.res");
    }
}
