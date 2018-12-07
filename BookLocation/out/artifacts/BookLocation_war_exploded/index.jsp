<%--
  Created by IntelliJ IDEA.
  User: Wing
  Date: 2018/11/21
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <!-- Required meta tags always come first -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>首页</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/ionicons.min.css">
    <link rel="stylesheet" href="css/owl.carousel.css">
    <link rel="stylesheet" href="css/owl.theme.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/iq.css">
</head>
<body>
<div id="header"></div>
<section class="padding-30">
    <div class="container">
        <div class="row">
            <div class="heading-wraper text-center margin-bottom-30">
                <h4>欢迎来到RFID定位查询系统</h4>
                <hr class="heading-devider gradient-orange">
            </div>
        </div>

        <div class="row padding-30">
            <div class="text-center">
                <p>Hint:请输入你想要查询的barcode</p>
            </div>
            <form action="location" method="get">
                <table style="margin: auto">
                    <tbody>
                    <tr>
                        <td>
                            <div style="margin: auto" class="text-center">
                                <h6>BARCODE:</h6>
                            </div>
                        </td>
                        <td>
                            <input style="margin: auto" name="barcode" type="text" value="20024081">
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="text-center ">
                    <input type="submit" value="提交" class="btn btn-violat border-none btn-rounded-corner">
                </div>
            </form>
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
    });
</script>
</body>
</html>
