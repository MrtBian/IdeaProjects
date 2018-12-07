<%@ page import="java.sql.*" %><%--
  Created by IntelliJ IDEA.
  User: Wing
  Date: 2018/1/23
  Time: 16:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" import="java.lang.*"
         errorPage="error.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Required meta tags always come first -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>图书位置信息</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/ionicons.min.css">
    <link rel="stylesheet" href="css/owl.carousel.css">
    <link rel="stylesheet" href="css/owl.theme.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/iq.css">
</head>

<body>
<%

    String barcode = "", bookName = "", bookPlace = "", updateTime = "", shelfNo = "", columnNo = "", rowNo = "";
    double offset = 0.0;
    int orderNo = 0, layer = 0, shelfNum = 0;
    barcode = request.getParameter("barcode");
    try {
        bookName = (String) session.getAttribute("bookName");
        bookPlace = (String) session.getAttribute("bookPlace");
//        System.out.println("书名 " + bookName + "\n位置 " + bookPlace);
        offset = (Double) session.getAttribute("offset");
        layer = (int) session.getAttribute("layer");
        orderNo = (int) session.getAttribute("orderNo");
        updateTime = (String) session.getAttribute("updateTime");
        shelfNum = (int) session.getAttribute("shelfNum");
    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println(layer + offset + bookPlace + orderNo + updateTime + shelfNum);
    if (bookPlace.contains(" ")) {
        String[] bookP = bookPlace.split(" ");
        if (bookP.length != 6) {
            bookPlace = "暂无此书位置";
        } else {
            shelfNo = bookP[4];
            columnNo = bookP[2];
            rowNo = bookP[3];
            bookPlace = " 楼层:" + bookP[1] + " " + bookP[2] + "列" + bookP[3] + "排" + bookP[5] + "层";
//            bookPlace = "区域:" + bookP[0] + " 楼层:" + bookP[1] + " " + bookP[2] + "列" + bookP[3] + "排" + bookP[4] + "架" + bookP[5] + "层第" + orderNo + "本";
        }
    } else {
        layer = 0;
    }
%>
<div id="header"></div>
<section class="padding-30">
    <div class="container">
        <div class="row">
            <div class="heading-wraper text-center margin-bottom-30">
                <h4>RFID定位</h4>
                <hr class="heading-devider gradient-orange">
            </div>
        </div>
        <div class="row">
            <div class="row padding-left-30 padding-right-30">
                <table>
                    <tbody>
                    <tr>
                        <td>
                            <div class="text-col">书名</div>
                        </td>
                        <td>
                            <div id="book-name" class="text-black padding-left-30 text-bold"><%= bookName%>
                            </div>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <div class="text-col">楼层</div>
                        </td>
                        <td>
                            <div id="book-floor" class="text-black padding-left-30 text-bold">4（外文阅览室）
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="text-col">书架</div>
                        </td>
                        <td>
                            <div id="book-shelf" class="text-black padding-left-30 text-bold">第<%=columnNo%>列，第<%= rowNo%>排
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="text-col">层号</div>
                        </td>
                        <td>
                            <div id="book-layer" class="text-black padding-left-30 text-bold"><%= layer%>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="text-col">更新时间</div>
                        </td>
                        <td>
                            <div id="update-time" class="text-black padding-left-30"><%= updateTime%>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <hr>
                <div>部分图书由于借阅频繁，有可能信息更新不及时，请同学们理解。</div>
            </div>

            <div id="shelfs_parent" class="row">
                <h5 class="text-center">图书在书架内部示意图</h5>
                <%--<div class="text-center">Hint:展示位置为大概率所在位置，左右偏差都是有可能的！</div>--%>
                <div id="shelfs" style="align-items: center;width: auto;font-size:0;display: none">
                    <div id="shelf1" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf2" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf3" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf4" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf5" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf6" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf7" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                    <div id="shelf8" class="col-md-2_3">
                        <div class="div-image-top">
                            <img class="img-shelf" src="image/shelf_top.png">
                        </div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book1 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book2 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book3 img-book display-none" src="image/book2.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book4 img-book display-none" src="image/book0.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book5 img-book display-none" src="image/book1.png"></div>
                        <div class="div-image">
                            <img class="img-shelf" src="image/shelf.png">
                            <img class="book6 img-book display-none" src="image/book2.png"></div>
                    </div>
                </div>
                <div id="shelf" class="display-none">
                    <div class="text-center">展示书架为一整排书架，不是一个小架。</div>
                    <div class="div-image-top">
                        <img class="img-shelf" src="image/shelf_top.png">
                    </div>
                    <div class="div-image">
                        <img class="img-shelf" src="image/shelf.png">
                        <img class="book1 img-book display-none" src="image/book0.png"></div>
                    <div class="div-image">
                        <img class="img-shelf" src="image/shelf.png">
                        <img class="book2 img-book display-none" src="image/book1.png"></div>
                    <div class="div-image">
                        <img class="img-shelf" src="image/shelf.png">
                        <img class="book3 img-book display-none" src="image/book2.png"></div>
                    <div class="div-image">
                        <img class="img-shelf" src="image/shelf.png">
                        <img class="book4 img-book display-none" src="image/book0.png"></div>
                    <div class="div-image">
                        <img class="img-shelf" src="image/shelf.png">
                        <img class="book5 img-book display-none" src="image/book1.png"></div>
                    <div class="div-image">
                        <img class="img-shelf" src="image/shelf.png">
                        <img class="book6 img-book display-none" src="image/book2.png"></div>
                </div>
            </div>
        </div>
    </div>
</section>


<div id="footer"></div>
<!-- jQuery first, then Tether, then Bootstrap JS. -->
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.js"></script>
<script src="js/owl.carousel.min.js"></script>
<script src="js/script.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $('#header').load('header.html');
        $('#footer').load('footer.html');
        var layer = <%=layer%> -1 + 1;
        var offset = <%=offset%>;
        var shelfNo = <%=shelfNo%>;
        var showShelfNo = shelfNo;
        var shelfNum = <%=shelfNum%>;
//         alert(showShelfNo+" "+shelfNum);
        if (shelfNum == 3) {
            $("#shelf1").css('visibility', 'hidden');
            $("#shelf2").css('visibility', 'hidden');
            $("#shelf6").css('visibility', 'hidden');
            $("#shelf7").css('visibility', 'hidden');
            $("#shelf8").css('visibility', 'hidden');
            showShelfNo += 2;
        } else if (shelfNum == 5) {
            $("#shelf1").css('visibility', 'hidden');
            $("#shelf7").css('visibility', 'hidden');
            $("#shelf8").css('visibility', 'hidden');
            showShelfNo += 1;
        } else if (shelfNum == 2) {
            $("#shelf1").css('visibility', 'hidden');
            $("#shelf2").css('visibility', 'hidden');
            $("#shelf3").css('visibility', 'hidden');
            $("#shelf6").css('visibility', 'hidden');
            $("#shelf7").css('visibility', 'hidden');
            $("#shelf8").css('visibility', 'hidden');
            showShelfNo += 3;
        } else if (shelfNum == 7) {
            $("#shelf8").css('visibility', 'hidden');
        }
        var widthWin = $("#shelfs_parent").width();
//        alert(widthWin);
        var width = widthWin * 0.125;
//        alert(showShelfNo);
        var height = width * 0.35;
//        $("#shelfs .col-md-2_3").css("width",width+"px");
        $("#shelfs .div-image").css("height", height + "px");
        $("#shelfs .div-image-top").css("height", height * 22 / 53 + "px");
        $("#shelfs .img-shelf").css("marginBottom", -0.68 * height + "px");
        $("#shelfs .img-book").css("height", height * 0.6 + "px");
        $("#shelfs .book" + layer).css("marginLeft", (offset * width * 0.90 + width * 0.025) + "px");
        $("#shelf" + showShelfNo + " .book" + layer).show();

        var offsetPx = (offset + shelfNo - 1) * widthWin * 0.925 / shelfNum + widthWin * 0.025;
//        alert(offsetPx);
        $("#shelf .book" + layer).css("marginLeft", offsetPx + "px");
        $("#shelf .book" + layer).show();
        if (widthWin < 792) {
            $("#shelfs").hide();
            $("#shelf").show();
        } else {
            $("#shelfs").show();
            $("#shelf").hide();
        }
    });

</script>
</body>
</html>