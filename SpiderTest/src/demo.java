import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashSet;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileUtil;


/**
 * @author zkw
 */
public class demo {

    private CloseableHttpClient client;
    private HttpPost post;
    private HttpGet get;
    private BasicCookieStore cookieStore;

    String baseUrl = "http://weibo.cn/u/";
    String starUid = "1537790411";
    String starUrl = baseUrl + starUid;
    String cookie = "SCF=Am0YUbvwB0GlA1I4OqSXkdUe3Eacyj3VKMwq1Fokt7xn--VLzW_XT_5UnzmQ7Hte7rILx9Zzk_bAlYdlfWSUy8Y.; " +
            "SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9Wh6b5rOnOC1xZ1vVOnnAaEa5JpX5KMhUgL" +
            ".Fo-0SoBXeh54S0M2dJLoI0YLxKMLBK-L1--LxKBLBonL1h5LxKqLBK5LBoMLxKML1-2L1hBLxKBLB.2L1K2LxKBLBo" +
            ".L12zLxKnL1h5L1h5t; _T_WM=8a54890bc1ab6f5050f2bb793c622dfd; " +
            "SUB=_2A253donSDeThGeBM61sS8yfIyjyIHXVUmBearDV6PUJbkdAKLXXDkW1NRRFx_0W-RxvP2ClJt92iW4pSD-KNSTWI; " +
            "SUHB=0WGMQX7DKSWBFO; SSOLoginState=1517484422";

    public demo() {
        CookieSpecProvider easySpecProvider = new CookieSpecProvider() {

            public CookieSpec create(HttpContext context) {

                return new BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin)
                            throws MalformedCookieException {
                        // Oh, I am easy
                    }
                };
            }

        };
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH,
                        new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory())
                .register("easy", easySpecProvider)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .build();

        client = HttpClients.custom()
                .setDefaultCookieSpecRegistry(r)
                .setDefaultRequestConfig(requestConfig)
                .build();

        get = new HttpGet();
        post = new HttpPost();
    }

    public String Login(String url) {
        get.reset();
        client = HttpClients.custom()
                .build();
        try {
            get.setURI(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        get.addHeader("Host", "weibo.cn");
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        get.addHeader("Accept", "*/*");
        get.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get.addHeader("Accept-Encoding", "gzip, deflate");
        get.addHeader("Referer", "http://weibo.cn/");
        get.addHeader(new BasicHeader("Cookie", cookie));

        String cont = "";
        try {
            CloseableHttpResponse resp = client.execute(get);
//            System.out.println(resp.toString());
            HttpEntity entity = resp.getEntity();
            cont = EntityUtils.toString(entity);
            resp.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return cont;
    }

    public void mainLoop(String html, String fileName) {
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
                HashSet<String> uList = getFollower(comlink);
                allFans.addAll(uList);
                //                System.out.println("评论链接:" + comlink);
            }
        }
        FileUtil.writeFile(fileName, true, allFans);
    }

    /**
     * 从评论链接中获取粉丝uid
     *
     * @param comlink 评论链接
     * @return 粉丝uid列表
     */
    public HashSet<String> getFollower(String comlink) {
        HashSet<String> uList = new HashSet<>();
        String html = Login(comlink);
        Document doc = Jsoup.parse(html);
        Elements eles = doc.getElementsByAttributeValue("value", "跳页");
        if(eles.size()==0){
            return uList;
        }
        Element div = eles.first().parent();
        String str = div.text();
        int pageNum = Integer.valueOf(str.substring(str.indexOf("/") + 1, str.length() - 1));
        for (int i = 0; i <= pageNum; i++) {
            System.out.println("Get " + (i + 1) + "th page!");
            String eachUrl = comlink.substring(0, comlink.indexOf("#")) + "&page=" + (i + 1);
            html = Login(eachUrl);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(html);
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
                    //                    System.out.println("Get "+count+" weibos!");

                    String text = each.text();
                    FileUtil.writeFile(file, true, text);
                    if (i % 5 == 4) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public HttpClient getClient() {
        return client;
    }

    public void setClient(CloseableHttpClient client) {
        this.client = client;
    }

    public HttpPost getPost() {
        return post;
    }

    public void setPost(HttpPost post) {
        this.post = post;
    }

    public HttpGet getGet() {
        return get;
    }

    public void setGet(HttpGet get) {
        this.get = get;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException {
        demo demo1 = new demo();
        //        String fileName = "Wing.txt";
        //        FileUtil.clearFile(fileName);
        //        demo1.getAllWeibo("5364400977", fileName);
        String fileNameFans = "LuHanFans.txt";
        FileUtil.clearFile(fileNameFans);
        demo1.mainLoop(demo1.Login(demo1.starUrl), fileNameFans);
    }
}
