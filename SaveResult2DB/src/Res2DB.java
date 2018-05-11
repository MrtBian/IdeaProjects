/**
 * created by wing on 2018.05.10
 * 将盘点结果保存至数据库中，并生成报表
 */

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import static util.FileUtil.readFileByLine;


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
     * 数据库参数
     **/
    private static final String HOST = "202.114.65.49";
    private static final int PORTNO = 1521;
    private static final String DBNAME = "RFID";
    private static final String USERNAME = "autopd";
    private static final String PASSWORD = "123456";

    Connection connect = null;
    Statement statement = null;
    ResultSet resultSet = null;
    /**
     * 结果文件解析
     **/
    /*EPC 区域号 楼层号 列号 排号 架号 层号 顺序号 放错等级*/
    private String[][] resInfo;
    private static final int FIELDNUM = 9;
    /**
     * 对应图书馆数据库字段
     **/
    String DBFIELDNAME[] = {"TAG_ID", "AREANO", "FLOORNO", "COLUMNNO", "ROWNO", "SHELFNO", "LAYERNO", "ORDERNO", "ERRORFLAG"};
    //
    private static final int SHEETNUM = 10;
    /**
     * 错误等级说明
     */
    String ERRORLEVEL[] = {"暂无实际位置", "位置正确", "区域错误", "楼层错误", "列错误", "排错误", "架错误", "层错误", "顺序错误"};

    /**
     * 初始化
     *
     * @param filePath 结果文件路径
     */

    Res2DB(String filePath) {
        this.resPath = filePath;
        reportPath = resPath.replace(".res", ".xlsx").replace("result", "report");
//        System.out.println(reportPath);
    }

    /**
     * 将位置信息更改写回数据库
     */
    public void write2DB() {
        List<String> result = readFileByLine(resPath);
        resInfo = new String[result.size()][FIELDNUM];
        int i = 0;
        getDBConnection();
        try {
            statement = connect.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String data : result) {
            resInfo[i] = data.split(" ");
            String sql = "update " + DBNAME + " set " + DBFIELDNAME[1] + "='" + resInfo[1] + "', "
                    + DBFIELDNAME[2] + "='" + resInfo[2] + "', "
                    + DBFIELDNAME[3] + "='" + resInfo[3] + "', "
                    + DBFIELDNAME[4] + "='" + resInfo[4] + "', "
                    + DBFIELDNAME[5] + "='" + resInfo[5] + "', "
                    + DBFIELDNAME[6] + "='" + resInfo[6] + "', "
                    + DBFIELDNAME[7] + "='" + resInfo[7] + "'"
                    + " where " + DBFIELDNAME[0] + "='" + resInfo[0] + "'";
            System.out.println(sql);
            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            i++;
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
            String sql = "SELECT BOOK_ID BOOK_INDEX BOOK_NAME FROM " + DBNAME + " WHERE TAG_ID = '" + tagID + "'";
            System.out.println(sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                bookID = resultSet.getString("BOOK_ID");
                bookIndex = resultSet.getString("BOOK_INDEX");
                bookName = resultSet.getString("BOOK_NAME");
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookID + " " + bookIndex + " " + bookName;
    }


    public void generateReport() {
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(new File(reportPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WritableSheet[] sheets = new WritableSheet[SHEETNUM];
        Label labBookID = new Label(0, 0, "BOOK_ID");
        Label labBookIndex = new Label(1, 0, "BOOK_INDEX");
        Label labBookName = new Label(2, 0, "BOOK_NAME");
        Label labError = new Label(3, 0, "ERROR");
        for (int i = 0; i < SHEETNUM; i++) {
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
            int errorNo = Integer.valueOf(data[FIELDNUM - 1]);
            //写入总表之中
            int rowNo_ = sheets[errorNo + 2].getRows();
            Label id_ = new Label(0, rowNo_, bookID);
            Label index_ = new Label(1, rowNo_, bookIndex);
            Label name_ = new Label(2, rowNo_, bookName);
            Label error_ = new Label(3, rowNo_, ERRORLEVEL[errorNo + 1]);
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
            try {
                book.write();
                book.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDBConnection(String host, int port, String dbName, String user, String password) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());//实例化驱动程序类
            String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;//驱动程序名：@主机名/IP：端口号：数据库实例名
            connect = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getDBConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());//实例化驱动程序类
            String url = "jdbc:oracle:thin:@" + HOST + ":" + PORTNO + ":" + DBNAME;//驱动程序名：@主机名/IP：端口号：数据库实例名
            connect = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Res2DB res2DB = new Res2DB("C:\\Users\\Wing\\Desktop\\result\\xxxx.res");
    }
}
