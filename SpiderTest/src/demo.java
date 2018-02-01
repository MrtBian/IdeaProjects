import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @author zkw
 */
public class demo {
    private HttpClient client;
    private HttpPost post;
    private HttpGet get;
    private BasicCookieStore cookieStore;

    String baseUrl = "https://weibo.cn/u/";
    String starUid = "1537790411";
    String starUrl = baseUrl + starUid;
    String cookie = "_T_WM=918d43a7f5805f40e60d654b3ccc3301; ALF=1519991699; "
            + "SCF=AgDWR313JuXm8k8mQnzvC2QugmwEc_65aDyMTbV2LFZ4HphBufkwEl7KgLbZEXYjCOnMfETkJYMBgXzoVWABtCo.; "
            + "SUB=_2A253dd8dDeThGeBM61sS8yfIyjyIHXVUmeFVrDV6PUJbktBeLRjBkW1NRRFx_yTanlvWccBs3jr9yi_YhuPv44k2; "
            + "SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5OGKSDVF6Bu7d9DXNCWFXm5JpX5K-hUgL.FoqEeh.0e0."
            + "XeK52dJLoIE9SqPWC9gxfqc-LxKBLB.eL1-2LxK-LBo.LBonLxKMLBKqL12qR15tt; SUHB=0btshTBE2Gksba; SSOLoginState=1517399885";

    public demo() {

        //cookie策略，不设置会拒绝cookie rejected，设置策略保存cookie信息
        cookieStore = new BasicCookieStore();
        CookieSpecProvider myCookie = new CookieSpecProvider() {

            public CookieSpec create(HttpContext context) {
                return new DefaultCookieSpec();
            }
        };
        Registry<CookieSpecProvider> rg = RegistryBuilder.<CookieSpecProvider>create().register("myCookie", myCookie)
                .build();

        client = HttpClients.custom().setDefaultCookieStore(cookieStore).setDefaultCookieSpecRegistry(rg).build();
        get = new HttpGet();
        post = new HttpPost();
    }

    public String Login(String url) {

        try {
            get.setURI(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        get.addHeader("Host", "weibo.cn");
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        get.addHeader("Accept", "*/*");
        get.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get.addHeader("Accept-Encoding", "gzip, deflate");
        get.addHeader("Referer", "http://weibo.com/");
        get.addHeader(new BasicHeader("Cookie", cookie));
        String cont = "";
        try {
            HttpResponse resp = client.execute(get);
            HttpEntity entity = resp.getEntity();
            cont = EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cont;
    }

    public void mainLoop(String html){
        Document doc = Jsoup.parse(html);
//        System.out.println("获取的微博内容:" + cont);
        Elements links = doc.getElementsByClass("cc");
        for (Element link : links) {
//            System.out.println("每条微博：" + link.toString());
            String tmptext = link.text();
            tmptext = tmptext.substring(0, tmptext.indexOf("["));
            //区别原文评论还是转发评论
            if (tmptext.length() < 5) {
                String comlink = link.attr("href");
                ArrayList<String> uList = getFollower(comlink);
//                System.out.println("评论链接:" + comlink);
            }
        }
    }

    /**
     * 从评论链接中获取粉丝uid
     *
     * @param comlink 评论链接
     * @return 粉丝uid列表
     */
    public ArrayList<String> getFollower(String comlink) {
        ArrayList<String> uList = new ArrayList<>();
        String html = Login(comlink);
        Document doc = Jsoup.parse(html);
        Elements links = doc.getElementsByAttributeValueMatching("id", "C_[0-9]*");
        for (Element link : links) {
//            String tmpid = link.attr("id");
//            if(tmpid.matches("C_[0-9]*")){
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
            System.out.println("ID:" + uid);
            System.out.println("Name:" + uName);
//            }

        }
        return uList;
    }

    private int getTotalPages(String uid) {
        String url = baseUrl + uid;
        String html = Login(url);
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementsByAttributeValue("name","mp").first();
        int pageNum = Integer.valueOf(ele.attr("value"));
        return pageNum;
    }

    public HttpClient getClient() {
        return client;
    }

    public void setClient(HttpClient client) {
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
        demo1.mainLoop(demo1.Login(demo1.starUrl));
    }
}
