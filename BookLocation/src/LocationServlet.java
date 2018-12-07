import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Wing
 * @date 18/11/13
 */
public class LocationServlet extends javax.servlet.http.HttpServlet {

    private String serverIP = "114.212.80.13";
    private String serverPort = "1433";
    private String serverID = "Administrator";
    private String serverPwd = "NetLab624Admin";
    private String dbID = "sa";
    private String dbPwd = "NetLab624";
    private String dbName = "newMiniLibrary";

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

    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        //网址格式：localhost:8080/BookLocation/location?barcode=xxxxxxx
        //设置响应内容类型
        response.setContentType("text/html;charset=UTF-8");
        String title = "图书位置";
        // 处理中文
        String barcode = request.getParameter("barcode");
        String bookName = "", bookPlace = "", updateTime = "";
        int orderNo = 0, totalNum = 100;

        //连接数据库
        String JDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String connectDB = "jdbc:sqlserver://" + serverIP + ":" + serverPort + ";DatabaseName=" + dbName;
        try {
            //加载数据库引擎，返回给定字符串名的类
            Class.forName(JDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(404, "加载数据库引擎失败");
            System.exit(0);
        }
//        System.out.println("数据库驱动成功");
        //连接数据库对象
        Connection con = null;
        try {
            con = DriverManager.getConnection(connectDB, dbID, dbPwd);
        } catch (SQLException e) {
            response.sendError(404, "连接数据库失败");
            e.printStackTrace();
            //System.exit(-1);
        }
        if (con == null) {
            response.sendError(404, "连接数据库失败");
        }
//        System.out.println("连接数据库成功");
        //创建SQL命令对象
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            //读取数据
//            System.out.println("开始读取数据");
            String sql = "SELECT Title FROM Book,BookCopy where Book.ID = BookCopy.BookID AND Barcode = '" + barcode + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                bookName = rs.getString("Title");
            } else {
                bookName = "Barcode不在数据库";
            }
            sql = "SELECT * FROM BookCopy where Barcode = '" + barcode + "'";
            rs = stmt.executeQuery(sql);
            //循环输出每一条记录
            if (rs.next()) {
                //输出每个字段
                bookPlace = rs.getString("check_level");
                updateTime = rs.getString("updateTime");
                orderNo = rs.getInt("BOOK_ORDER_NO");
                totalNum = rs.getInt("ShelfBlock_Num");
            }
//            System.out.println("读取完毕");
            //关闭连接
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("书名 " + bookName + "\n位置 " + bookPlace);
        HttpSession session = request.getSession();
        if (bookPlace == null) {
            System.out.println("bookPlace == null");
            bookPlace = "暂无此书位置";
            session.setAttribute("bookPlace", bookPlace);
            session.setAttribute("bookName", bookName);
            response.sendRedirect("location.jsp?barcode=" + barcode);
        } else if (bookPlace.equals("")) {
            System.out.println("bookPlace == ''");
            bookPlace = "Barcode不在数据库";
            session.setAttribute("bookPlace", bookPlace);
            session.setAttribute("bookName", bookName);
            response.sendRedirect("location.jsp?barcode=" + barcode);
        } else {
            String[] bookP = bookPlace.split(" ");
            int shelfNum = getShelfNum(bookP[2], bookP[3]);
            int layer = Integer.parseInt(bookP[5]);
            double offset = (double) (orderNo - 1) / totalNum;
            session.setAttribute("bookPlace", bookPlace);
            session.setAttribute("bookName", bookName);
            session.setAttribute("orderNo", orderNo);
            session.setAttribute("offset", offset);
            session.setAttribute("layer", layer);
            session.setAttribute("updateTime", updateTime);
            session.setAttribute("shelfNum", shelfNum);

            System.out.println("shelfNum:"+shelfNum);
            String postParams = "?barcode=" + barcode +
                    "\n&bookName=" + bookName +
                    "\n&bookPlace=" + bookPlace +
                    "\n&orderNo=" + orderNo +
                    "\n&offset=" + offset +
                    "\n&totalNum=" + totalNum +
                    "\n&shelfNum=" + shelfNum +
                    "\n&updateTime=" + updateTime;
            System.out.println(postParams);
            response.sendRedirect("location.jsp?barcode=" + barcode);
        }
    }
}
