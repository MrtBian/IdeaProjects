import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class LocationServlet extends javax.servlet.http.HttpServlet {

    String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=Test";
    String serverIP = "114.212.80.13";
    String serverID = "Administrator";
    String serverPwd = "NetLab624Admin";
    String dbID = "sa";
    String dbPwd = "NetLab624";
    String dbName = "MiniLibrary";
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        // 设置响应内容类型
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        String title = "图书位置";
        // 处理中文
        String barcode =new String(request.getParameter("barcode"));
        String bookName="",bookShelfID="",bookLayer="";

        //连接数据库
        String JDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";//SQL数据库引擎
        String connectDB="jdbc:sqlserver://"+serverIP+":1433;DatabaseName="+dbName;
        Connection dbConn = null;
        try
        {
            Class.forName(JDriver);//加载数据库引擎，返回给定字符串名的类
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("加载数据库引擎失败");
            System.exit(0);
        }
        System.out.println("数据库驱动成功");

        Connection con= null;//连接数据库对象
        try {
            con = DriverManager.getConnection(connectDB,dbID,dbPwd);
        } catch (SQLException e) {

            System.out.println("连接数据库失败！");
            e.printStackTrace();
        }
        System.out.println("连接数据库成功");
        Statement stmt= null;//创建SQL命令对象
        try {
            stmt = con.createStatement();
            //读取数据
            System.out.println("开始读取数据");
            ResultSet rs=stmt.executeQuery("SELECT * FROM Book,BookCopy where Book.ID = BookCopy.BookID AND Barcode = \'"+barcode+"\'");//返回SQL语句查询结果集(集合)
            //循环输出每一条记录
            while(rs.next())
            {
                //输出每个字段
                bookName = rs.getString("Title");
                bookShelfID=rs.getString("ShelfID");
                bookLayer=rs.getString("Layer");
            }
            System.out.println("读取完毕");

            //关闭连接
            stmt.close();//关闭命令对象连接
            con.close();//关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String docType = "<!DOCTYPE html> \n";
        out.println(docType +
                "<html>\n" +
                "<head><title>" + title + "</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + title + "</h1>\n" +
                "<ul>\n" +
                "  <li><b>Barcode:</b>："
                + barcode + "\n" +
                "  <li><b>BooKName:</b>："
                + bookName + "\n" +
                "  <li><b>bookShelfID:</b>："
                + bookShelfID + "\n" +
                "  <li><b>bookLayer:</b>："
                + bookLayer + "\n" +
                "</ul>\n" +
                "</body></html>");
    }
}
