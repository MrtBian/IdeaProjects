package com.mapreduce.spider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


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


/**
 *
 *
 * @author zkw
 *
 */
public class cookieLogin {
    private HttpClient client;
    private HttpPost post;
    private HttpGet get;
    private BasicCookieStore cookieStore;

    public cookieLogin() {
        //cookie策略，不设置会拒绝cookie rejected，设置策略保存cookie信息
        cookieStore = new BasicCookieStore();
        CookieSpecProvider myCookie = new CookieSpecProvider() {

            public CookieSpec create(HttpContext context) {
                return new DefaultCookieSpec();
            }
        };
        Registry<CookieSpecProvider> rg = RegistryBuilder.<CookieSpecProvider> create().register("myCookie", myCookie)
                .build();

        client = HttpClients.custom().setDefaultCookieStore(cookieStore).setDefaultCookieSpecRegistry(rg).build();
        get = new HttpGet();
        post = new HttpPost();
    }

    public void Login() throws ClientProtocolException, IOException, URISyntaxException {

        String LoginUrl = "你的微博主页网址";

        get.setURI(new URI(LoginUrl));
        get.addHeader("Host", "weibo.com");
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        get.addHeader("Accept", "*/*");
        get.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get.addHeader("Accept-Encoding", "gzip, deflate");
        get.addHeader("Referer", "http://weibo.com/");
        get.addHeader(new BasicHeader("Cookie", "上述获取的cookie值"));

        HttpResponse resp = client.execute(get);
        HttpEntity entity = resp.getEntity();
        String cont = EntityUtils.toString(entity);
        System.out.println("获取的微博内容:" + cont);

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
        new cookieLogin().Login();
    }
}
