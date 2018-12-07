package main;

import common.BookInfo;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import static utils.FileUtil.readFileByLine;
import static utils.MyLogger.LOGGER;

/**
 * 将盘点结果保存至数据库中，并生成报表
 *
 * @author Wing
 * @version 1.0, 18/05/10
 */
public class Res2Database {
    /**
     * 结果文件路径
     **/
    private String resPath;

    /**
     * 丢失书籍集合
     */
    private HashSet<String> lossSet;
    private ArrayList<BookInfo> lossList;
    /**
     * 数据库ip
     **/
    private String HOST = "114.212.80.13";
    /**
     * 端口
     */
    private String PORT_NO = "1433";

    /**
     * 数据库名
     */
    private String DB_MAIN_NAME = "newMiniLibrary";
    private String serverID = "Administrator";
    private String serverPwd = "NetLab624Admin";
    /**
     * 用户名
     */
    private static String USERNAME = "sa";
    /**
     * 密码
     */
    private static String PASSWORD = "NetLab624";

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
     * 需要的图书集合，存储barcode
     */
    private Set<String> bookSetNeeded;
    /**
     * 书的集合
     */
    private Map<String, String[]> bookMap;
    /**
     * 书的有序列表
     */
    private List<Map.Entry<String, String[]>> bookList;
    private LinkedHashMap<String, String> firstBookMap;
    /**
     * 对应图书馆数据库字段
     **/
    private static final String[] DB_FIELD_NAME;

    static {
        DB_FIELD_NAME = new String[]{"TAG_ID", "AREANO", "FLOORNO", "COLUMNNO", "ROWNO", "SHELFNO",
                "LAYERNO", "ORDERNO", "ERRORFLAG", "NUM"};
    }

    private int countNotInDB = 0, countSuccess = 0, countLoss = 0,
            countErrLib = 0, countErr = 0, countLoan = 0, countHold = 0, countStatusAbn = 0;

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
        getBookNeeded();
        getResInfo();
        write2DB();
    }

    /**
     * 获取南大外文图书的书格数目
     *
     * @param column 列号
     * @param row    排号
     *
     * @return 书格数目
     */
    private int getShelfNum(String column, String row) {
        int col_int = Integer.parseInt(column);
        int row_int = Integer.parseInt(row);
        ArrayList<Integer> tmp = new ArrayList<>(Arrays.asList(7, 8, 19, 20));
        int shelfNum = 0;
        switch (col_int) {
            case 1:
                if (tmp.contains(row_int)) {
                    shelfNum = 2;
                } else {
                    shelfNum = 3;
                }
                break;
            case 2:
                shelfNum = 5;
                break;
            case 3:
                if (tmp.contains(row_int)) {
                    shelfNum = 7;
                } else {
                    shelfNum = 8;
                }
                break;
            default:
                break;
        }
        return shelfNum;
    }

    private void getBookSetByShelfID(String shelfID) {
        LOGGER.info("获取shelfID为" + shelfID + "的图书信息");
        getDBConnection();
        createStatement();
        int count = 0;
        String sql = "SELECT Barcode FROM BookCopy where shelfID = '" + shelfID + "'";
        try {
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String barcode = resultSet.getString("Barcode");
                bookSetNeeded.add(barcode);
                count++;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        closeStatement();
        closeDBConnection();
        LOGGER.info("shelfID为" + shelfID + "的图书数目为" + count);
    }

    private void getBookNeeded() {
        LOGGER.info("获取需要图书");
        bookSetNeeded = new HashSet<>();
        getBookSetByShelfID("10500049");
        getBookSetByShelfID("10500050");
        getBookSetByShelfID("10500051");
        getBookSetByShelfID("10500057");
        getBookSetByShelfID("10500058");
        getBookSetByShelfID("10500059");
        LOGGER.info("图书数量：" + bookSetNeeded.size());
    }

    /**
     * 从数据库中获取图书信息
     */
    private void getResInfo() {
        LOGGER.info("获取图书信息...");
        LOGGER.info("连接数据库...");
//        getDBConnection();
        LOGGER.info("连接成功...");

        bookMap = new TreeMap<>();
        LOGGER.info("读取res文件...");
        List<String> result = readFileByLine(resPath);
        LOGGER.info("共有" + result.size() + "条待处理");
        resInfo = new String[result.size()][];
        int i = 0;
        countNotInDB = 0;
        int countRC = 0;
        countSuccess = 0;
        for (String data : result) {
            data = data.replaceAll("[^\\w|\\d| ]", "");
            String[] bookInfos = new String[BOOK_FIELD_NUM];
            resInfo[i] = data.split(" ");
            String tagID = resInfo[i][0];
            if (resInfo[i].length < 10) {
                LOGGER.warn("EPC:" + tagID + " 数据异常！数据长度：" + resInfo[i].length);
                continue;
            }

            if (tagID.length() < 11) {
                LOGGER.warn("EPC:" + tagID + " EPC过短！");
                continue;
            }
            if (tagID.matches("E.*")) {
                //标签转换失败
                countNotInDB++;
                LOGGER.warn("标签转换失败，TagID=" + tagID + "的书");
            }
            String barcode = getBarcode(tagID);
            String sql;
            System.arraycopy(resInfo[i], 1, bookInfos, BookFieldName.AREANO.getIndex(), FIELD_NUM - 1);

            //TODO: 应急处理，后续需要更改
            bookInfos[BookFieldName.FLOORNO.getIndex()] = "4";
            bookInfos[BookFieldName.AREANO.getIndex()] = "W";

            //查询图书信息，暂时不需要
//                sql = "SELECT TITLE FROM Book,BookCopy where Book.ID = BookCopy.BookID AND Barcode = '" + barcode + "'";
//                statement = connect.createStatement();
//                resultSet = statement.executeQuery(sql);
//                if (resultSet.next()) {
//                    bookInfos[0] = barcode;
//                    bookInfos[1] = "";
//                    bookInfos[2] = resultSet.getString("TITLE");
//                    if (bookInfos[1] == null || bookInfos[0] == null) {
//                        //数据库无此书
//                        countNotInDB++;
//                        LOGGER.warn("数据库未找到TagID=" + tagID + "的书");
//                        continue;
//                    }
//            if (bookSetNeeded.contains(barcode)) {
                bookMap.put(tagID, bookInfos);
//            }
//
//                } else {
//                    //数据库无此书
//                    countNotInDB++;
//                    LOGGER.warn("数据库未找到TagID=" + tagID + "的书");
//                    continue;
//                }
            i++;
            countSuccess++;
            if (i % 1000 == 0) {
                LOGGER.info("已处理" + i);
            }
        }
        closeDBConnection();
        LOGGER.info("有" + countNotInDB + "个EPC不在数据库中;" + countSuccess + "本书处理成功！");
//            statement.close();
//            connect.close();
//        } catch (SQLException e) {
//            LOGGER.error(e.getMessage());
//        }
//        sortBooks();
    }

    /**
     * 排序
     */
    private void sortBooks() {
        LOGGER.info("结果排序中...");
        //排序
        bookList = new ArrayList<>(bookMap.entrySet());
        //升序排序
        bookList.sort((o1, o2) ->
        {
            String[] b1 = o1.getValue().clone();
            String[] b2 = o2.getValue().clone();
            long i1 = 0, i2 = 0;
            //排序，从楼层号到书架层号
            for (int i3 = BookFieldName.FLOORNO.getIndex(); i3 <= BookFieldName.ORDERNO.getIndex(); i3++) {
                if (i3 == BookFieldName.ORDERNO.getIndex()) {
                    i1 *= 2;
                    i2 *= 2;
                }
                if (b1[i3].equals("") || b2[i3].equals("")) {
                    System.out.println(b1.toString());
                    System.out.println(b2.toString());
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
     * 将位置信息更改写回数据库
     */
    private void write2DB() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String dateString = simpleDateFormat.format(date);
        LOGGER.info("将更改写回数据库");
        LOGGER.info("连接数据库...");
        getDBConnection();
        LOGGER.info("连接成功...");
        createStatement();
        int i = 0;
        for (Map.Entry<String, String[]> entry : bookMap.entrySet()) {
            String tagID = entry.getKey();
            String barcode = getBarcode(tagID);
            String[] bookInfos = entry.getValue();
            String bookPlace = locationEncode(bookInfos);
            String sql = "UPDATE BOOKCOPY SET " +
                    "updatetime='" + dateString + "', " +
                    "check_level='" + bookPlace + "', " +
                    "ShelfBlock_Num='" + bookInfos[BookFieldName.NUM.getIndex()] + "', " +
                    "BOOK_ORDER_NO=" + bookInfos[BookFieldName.ORDERNO.getIndex()] + " " +
                    "WHERE BARCODE='" + barcode + "'";
//            LOGGER.info(sql);
            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
            if (++i % 1000 == 0) {
                LOGGER.info("已更新" + i);
            }

        }
        closeStatement();
        closeDBConnection();
        LOGGER.info("数据库更新完成");
    }

    /**
     * 将位置信息解码
     *
     * @param code 位置信息的编码
     *
     * @return 图书信息数组
     */
    public String[] locationDecode(String code) {
        String[] bookPlace = code.split(" ");
        if (bookPlace.length != 6) {
            LOGGER.error("编码无效！");
        }
        return bookPlace;
    }

    /**
     * 将位置信息进行编码
     * 编码格式：以空格分隔
     *
     * @param bookInfos 图书信息数组
     *
     * @return 编码字符串
     */
    private String locationEncode(String[] bookInfos) {
        StringBuilder code = new StringBuilder("");
        for (int i = BookFieldName.AREANO.getIndex(); i <= BookFieldName.LAYERNO.getIndex(); i++) {
            code.append(bookInfos[i]);
            if (i != BookFieldName.LAYERNO.getIndex()) {
                code.append(" ");
            }
        }
        return code.toString();
    }

    private String getBarcode(String epc) {
        int len = Integer.parseInt(epc.substring(2, 3), 16);
        return epc.substring(3, 3 + len);
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

    /**
     * 连接数据库
     */
    private void getDBConnection() {

        String JDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://" + HOST + ":" + PORT_NO + ";DatabaseName=" + DB_MAIN_NAME;
        try {
            Class.forName(JDriver);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            System.exit(0);
        }
        try {
            connect = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        try {
            //驱动程序名：@主机名/IP：端口号：数据库实例名
            connect = DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void closeDBConnection() {
        try {
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createStatement() {
        try {
            statement = connect.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeStatement() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Res2Database res2Database = new Res2Database("E:\\IdeaProjects\\SaveResult2DB_NJU\\data\\A2_2018-11-17_13-22-59_full.res");

    }
}
