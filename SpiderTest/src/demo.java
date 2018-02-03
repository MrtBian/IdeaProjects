import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;


/**
 * @author zkw
 */
public class demo {

    private String baseUrl = "http://weibo.cn/u/";
    private String starUid = "1537790411";
    private String starName;
    public String starUrl = baseUrl + starUid;
    String[] userAgents = {"Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8.1.11) Gecko/20071127 " +
            "Firefox/2.0.0.11", "Opera/9.25 (Windows NT 5.1; U; en)", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT " +
            "5.1; SV1;.NET CLR " +
            "1.1.4322;" + " " + "" + "" + "" + "" + ".NET" + " " + "" + "CLR " + "" + "2.0.50727)" +
            "" + "", "Mozilla/5.0" + " " + "" + "" + "" + "" + "" + "" + "(compatible; " + "" + "Konqueror/3.5; " +
            "Linux)" + " " + "KHTML/3.5.5 " + "" + "" + "" + "(like " + "" + "Gecko)" + "" + "" + "" + " " + "" + ""
            + "" + "(Kubuntu)", "Mozilla/5.0" + "" + " " + "(X11; " + "" + "U; " + "Linux " + "" + "" + "" + "i686; "
            + "en-US; " + "rv:1.8.0.12) " + "Gecko/20070731 " + "Ubuntu/dapper-security" + "" + " " + "" +
            "Firefox/1.5.0.12", "Lynx/2.8.5rel.1 " + "libwww-FM/2.14 " + "SSL-MM/1.4.1 GNUTLS/1.2.9", "Mozilla/5.0 "
            + "(X11; Linux" + " " + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "i686)" + "" +
            "" + "" + " " + "" + "" + "AppleWebKit/535.7 " + "" + "(KHTML, " + "" + "" + "" + "like " + "Gecko) " +
            "" + "Ubuntu/11.04" + " " + "Chromium/16.0.912.77 " + "Chrome/16.0.912.77" + "" + " " + "Safari/535.7",
            "Mozilla/5.0 " + "" + "(X11; " + "" + "Ubuntu; " + "Linux" + " " + "i686; " + "rv:10.0) " + "" +
                    "Gecko/20100101 " + "Firefox/10.0" + " ",};
    //18207394354
    String cookie1 = "SCF=Am0YUbvwB0GlA1I4OqSXkdUe3Eacyj3VKMwq1Fokt7xn--VLzW_XT_5UnzmQ7Hte7rILx9Zzk_bAlYdlfWSUy8Y.; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9Wh6b5rOnOC1xZ1vVOnnAaEa5JpX5KMhUgL.Fo-0SoBXeh54S0M2dJLoI0YLxKMLBK-L1--LxKBLBonL1h5LxKqLBK5LBoMLxKML1-2L1hBLxKBLB.2L1K2LxKBLBo.L12zLxKnL1h5L1h5t; _T_WM=8a54890bc1ab6f5050f2bb793c622dfd; SUB=_2A253cPrWDeRhGeBK7VcR9ijKyT6IHXVUmoaerDV6PUJbkdANLVjwkW1NR4omjJZcZlrBXyI7koH_fox-i0ibY91C; SUHB=0JvgDcAFsd9O6A; SSOLoginState=1517587077";
    //bianxy96@gmail.com
    String cookie0 = "SCF=Am0YUbvwB0GlA1I4OqSXkdUe3Eacyj3VKMwq1Fokt7xn--VLzW_XT_5UnzmQ7Hte7rILx9Zzk_bAlYdlfWSUy8Y.; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9Wh6b5rOnOC1xZ1vVOnnAaEa5JpX5KMhUgL.Fo-0SoBXeh54S0M2dJLoI0YLxKMLBK-L1--LxKBLBonL1h5LxKqLBK5LBoMLxKML1-2L1hBLxKBLB.2L1K2LxKBLBo.L12zLxKnL1h5L1h5t; _T_WM=8a54890bc1ab6f5050f2bb793c622dfd; SUB=_2A253cPxDDeThGeBM61sS8yfIyjyIHXVUmoQLrDV6PUJbkdAKLVTjkW1NRRFx_27nTDK6YueG54IxB1HNU8xcU2kh; SUHB=0btshTBE2Gksba; SSOLoginState=1517587475";
    //13574854583
    String cookie2 = "SCF=Am0YUbvwB0GlA1I4OqSXkdUe3Eacyj3VKMwq1Fokt7xn--VLzW_XT_5UnzmQ7Hte7rILx9Zzk_bAlYdlfWSUy8Y.; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9Wh6b5rOnOC1xZ1vVOnnAaEa5JpX5KMhUgL.Fo-0SoBXeh54S0M2dJLoI0YLxKMLBK-L1--LxKBLBonL1h5LxKqLBK5LBoMLxKML1-2L1hBLxKBLB.2L1K2LxKBLBo.L12zLxKnL1h5L1h5t; _T_WM=8a54890bc1ab6f5050f2bb793c622dfd; SUB=_2A253cPq2DeRhGeBK7VcS9i3IyDyIHXVUmob-rDV6PUJbkdANLRHMkW1NR4omip_eYWh_PS3hRISUJTAqMKPHACRR; SUHB=00xZ-rGia0KlRg; SSOLoginState=1517587174";
    //13467453825
    String cookie3 = "SCF=Am0YUbvwB0GlA1I4OqSXkdUe3Eacyj3VKMwq1Fokt7xn--VLzW_XT_5UnzmQ7Hte7rILx9Zzk_bAlYdlfWSUy8Y.; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9Wh6b5rOnOC1xZ1vVOnnAaEa5JpX5KMhUgL.Fo-0SoBXeh54S0M2dJLoI0YLxKMLBK-L1--LxKBLBonL1h5LxKqLBK5LBoMLxKML1-2L1hBLxKBLB.2L1K2LxKBLBo.L12zLxKnL1h5L1h5t; _T_WM=8a54890bc1ab6f5050f2bb793c622dfd; SUB=_2A253cPtjDeRhGeBK7VcR9ijKzzmIHXVUmoUrrDV6PUJbkdANLVrgkW1NR4omiQw5wzUh0wI1NZKiAPUtib1mtNSn; SUHB=0C1CoMvX55S0Gw; SSOLoginState=1517587251";
    String[] cookies = {cookie1, cookie2, cookie3, cookie0};

    public demo() {

    }

    public String Login(String url) {
        int ranInt = (int) (Math.random() * 5);
        try {
            Thread.sleep(1000 * ranInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int agentIndex = (int) (Math.random() * 8);
        int cookieIndex = (int)(Math.random()*4);
        cookieIndex = 3;
        CloseableHttpClient client;
        HttpGet get = new HttpGet();

        client = HttpClients.custom().build();
        try {
            get.setURI(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        get.addHeader("Host", "weibo.cn");
        get.addHeader("User-Agent", userAgents[agentIndex]);
        get.addHeader("Accept", "*/*");
        get.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get.addHeader("Accept-Encoding", "gzip, deflate");
        get.addHeader("Referer", "http://weibo.cn/");
        get.addHeader(new BasicHeader("Cookie", cookies[cookieIndex]));

        String cont = "";
        try {
            CloseableHttpResponse resp = client.execute(get);
            System.out.println("URL："+url);
            System.out.println(resp.getStatusLine());
            System.out.println("Cookie："+cookieIndex);
            System.out.println("UserAgent:"+agentIndex);
            HttpEntity entity = resp.getEntity();
            cont = EntityUtils.toString(entity);
            resp.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return cont;
    }

    public void mainLoop(String uid, String fileName) {
        String url = baseUrl + uid;
        String html = Login(url);
        Document doc = Jsoup.parse(html);
        //        System.out.println("获取的微博内容:" + cont);
        Elements links = doc.getElementsByClass("cc");
        HashSet<String> allFans = new HashSet<>();
        for (Element link : links) {
            //            System.out.println("每条微博：" + link.toString());
            String tmptext = link.text();
            tmptext = tmptext.substring(0, tmptext.indexOf("["));
            //区别原文评论还是转发评论
            if (tmptext.length() < 5) {
                String comlink = link.attr("href");
                HashSet<String> uList = getFans(comlink);
                allFans.addAll(uList);
                //                System.out.println("评论链接:" + comlink);
            }
            for (Iterator<String> it = allFans.iterator(); it.hasNext();) {
                String fan = it.next();
                if(!isFan(starName,fan)){
                    it.remove();
                }
            }
            FileUtil.writeFile(fileName, true, allFans);
        }
        return;
    }

    public boolean isFan(String starName,String uid){
        HashSet<String> fList = getFollowers(uid);
        boolean tmp = fList.contains(starName);
        return tmp;
    }

    public HashSet<String> getFollowers(String uid) {
        HashSet<String> uList = new HashSet<>();
        String url = baseUrl + uid;
        String html = Login(url);
        Document doc = Jsoup.parse(html);
        Element elef = doc.getElementsByClass("tip2").first().getElementsByTag("a").first();
        String fUrl = elef.attr("href");
        html = Login(fUrl);
        doc = Jsoup.parse(html);
        Elements eles = doc.getElementsByAttributeValue("value", "跳页");
        if (eles.size() == 0) {
            return uList;
        }
        Element div = eles.first().parent();
        String str = div.text();
        int pageNum = Integer.valueOf(str.substring(str.indexOf("/") + 1, str.length() - 1));
        for (int i = 0; i <= pageNum; i++) {
            String eachUrl = fUrl + "&page=" + (i + 1);
            html = Login(eachUrl);doc = Jsoup.parse(html);
            Elements links = doc.getElementsByTag("table");
            for (Element link : links) {
                Element tmp = link.getElementsByTag("a").first();
                String uName = tmp.text();
                uList.add(uName);
            }
        }
        return uList;
    }

    /**
     * 从评论链接中获取粉丝uid
     *
     * @param comlink 评论链接
     * @return 粉丝uid列表
     */
    public HashSet<String> getFans(String comlink) {
        HashSet<String> uList = new HashSet<>();
        String html = Login(comlink);
        Document doc = Jsoup.parse(html);
        Elements eles = doc.getElementsByAttributeValue("value", "跳页");
        if (eles.size() == 0) {
            return uList;
        }
        Element div = eles.first().parent();
        String str = div.text();
        int pageNum = Integer.valueOf(str.substring(str.indexOf("/") + 1, str.length() - 1));
        for (int i = 0; i <= pageNum; i++) {
            System.out.println("Get " + (i + 1) + "th page!");
            String eachUrl = comlink.substring(0, comlink.indexOf("#")) + "&page=" + (i + 1);
            html = Login(eachUrl);

            System.out.println(html.length());
            doc = Jsoup.parse(html);
            Elements links = doc.getElementsByAttributeValueMatching("id", "C_[0-9]*");
            for (Element link : links) {

                Element tmp = link.getElementsByTag("a").first();
                String uidtmp = tmp.attr("href").toString();
                String uid = "";
                String uName = tmp.text();
                if (uidtmp.matches("/u/[0-9]*")) {
                    uid = uidtmp.substring(3, uidtmp.length());
                } else {
                    uid = uidtmp.substring(1, uidtmp.length());
                }
                uList.add(uid);
                //                System.out.println("ID:" + uid);
                //                System.out.println("Name:" + uName);
            }
        }
        return uList;
    }

    private int getTotalPages(String uid) {
        String url = baseUrl + uid;
        String html = Login(url);
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementsByAttributeValue("name", "mp").first();
        int pageNum = Integer.valueOf(ele.attr("value"));
        return pageNum;
    }

    /**
     * @param uid
     * @param file
     */
    public void getAllWeibo(String uid, String file) {
        String url = baseUrl + uid;
        String html = Login(url);
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementsByAttributeValue("name", "mp").first();
        int pageNum = Integer.valueOf(ele.attr("value"));
        int count = 0;
        for (int i = 0; i <= pageNum; i++) {
            System.out.println("Get " + (i + 1) + "th page!");
            String eachUrl = url + "?page=" + (i + 1);
            html = Login(eachUrl);
            doc = Jsoup.parse(html);
            Elements eles = doc.getElementsByClass("c");
            for (Element each : eles) {
                if (each.attr("id").matches("M_[0-9a-zA-Z]*")) {
                    count++;
                    // System.out.println("Get "+count+" weibos!");

                    String text = each.text();
                    FileUtil.writeFile(file, true, text);
                }
            }
        }
    }

    public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException {
        demo demo1 = new demo();
        //        String fileName = "LuhanWeibo.txt";
        //        FileUtil.clearFile(fileName);
        //        demo1.getAllWeibo(demo1.starUid, fileName);
        String fileNameFans = "LuHanFans.txt";
        FileUtil.clearFile(fileNameFans);
        demo1.mainLoop(demo1.starUid, fileNameFans);
    }
}
