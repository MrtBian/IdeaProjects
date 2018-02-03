import java.io.IOException;
import java.net.URI;

import com.baidu.aip.nlp.AipNlp;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class StarMapper extends Mapper<Object, Text, Text, Text> {
    public static final String APP_ID = "10731238";
    public static final String API_KEY = "3v1mVKQnKLsSAREEx7XjL7x6";
    public static final String SECRET_KEY = "qCQCdHbaIekQD9nagIdTLTSkQWNbnN75";
    public static final double THRESHOLD_CONFIDENCE = 0.35;
    private AipNlp client;
    private String baseUrl = "https://weibo.cn/u/";
    private String starUid = "1537790411";
    public String starName;
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
    String cookie1 = "_T_WM=850fc9d3069c874c19fbd9e1fe092411; SUB=_2A253cRCrDeRhGeBK7VcR9ijKyT6IHXVUnbDjrDV6PUJbkdBeLWHWkW1NR4omjCViwakeBb52TYCg0nsYjAU3kZf4; SUHB=0V5lyZpTrP-0cM; SCF=AtI0SJPpmwi_n4RzToidS_z-RwVsuXVBolNs7OTmkkXybHiown20ApKKFqrabsBvKXXw2zwakAhhuNhKsrHIk20.; SSOLoginState=1517641979";
    //bianxy96@gmail.com
    String cookie0 = "_T_WM=918d43a7f5805f40e60d654b3ccc3301; ALF=1519991699; SCF=AgDWR313JuXm8k8mQnzvC2QugmwEc_65aDyMTbV2LFZ4HphBufkwEl7KgLbZEXYjCOnMfETkJYMBgXzoVWABtCo.; SUB=_2A253dd8dDeThGeBM61sS8yfIyjyIHXVUmeFVrDV6PUJbktBeLRjBkW1NRRFx_yTanlvWccBs3jr9yi_YhuPv44k2; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5OGKSDVF6Bu7d9DXNCWFXm5JpX5K-hUgL.FoqEeh.0e0.XeK52dJLoIE9SqPWC9gxfqc-LxKBLB.eL1-2LxK-LBo.LBonLxKMLBKqL12qR15tt; SUHB=0btshTBE2Gksba; SSOLoginState=1517399885";
    //13574854583
    String cookie2 = "_T_WM=9e6435847f436692b2fd240531245805; SUB=_2A253cRUmDeRhGeBK7VcS9i3IyDyIHXVUnbturDV6PUJbkdBeLU_3kW1NR4omipfMhIuNhCoi53g1sXRhRC6z1YWT; SUHB=0qjEtY8FzhT3cq; SCF=AqXYTJ7xht10RIs2EFbCY1rcYW1HloQZ4ofbp-89DBN0m9hVIxFD--hRcUQsjzspqJii8VF3cSbvsonJzbjaayo.; SSOLoginState=1517643126";
    //13467453825
    String cookie3 = "SCF=Am0YUbvwB0GlA1I4OqSXkdUe3Eacyj3VKMwq1Fokt7xn--VLzW_XT_5UnzmQ7Hte7rILx9Zzk_bAlYdlfWSUy8Y.; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9Wh6b5rOnOC1xZ1vVOnnAaEa5JpX5KMhUgL.Fo-0SoBXeh54S0M2dJLoI0YLxKMLBK-L1--LxKBLBonL1h5LxKqLBK5LBoMLxKML1-2L1hBLxKBLB.2L1K2LxKBLBo.L12zLxKnL1h5L1h5t; _T_WM=8a54890bc1ab6f5050f2bb793c622dfd; SUB=_2A253cPtjDeRhGeBK7VcR9ijKzzmIHXVUmoUrrDV6PUJbkdANLVrgkW1NR4omiQw5wzUh0wI1NZKiAPUtib1mtNSn; SUHB=0C1CoMvX55S0Gw; SSOLoginState=1517587251";
    String[] cookies = {cookie0, cookie2};
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        System.out.println("Setup!");
        client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
    }

    @Override
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        System.out.println("Map!");
        String uid = value.toString();
        String url = baseUrl + uid;
        String html = Login(url);
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementsByAttributeValue("name", "mp").first();
        int pageNum = Integer.valueOf(ele.attr("value"));
//        int count = 0;
        for (int i = 0; i <= pageNum; i++) {
            System.out.println("Get " + (i + 1) + "th page!");
            String eachUrl = url + "?page=" + (i + 1);
            html = Login(eachUrl);
            doc = Jsoup.parse(html);
            Elements eles = doc.getElementsByClass("c");
            for (Element each : eles) {
                if (each.attr("id").matches("M_[0-9a-zA-Z]*")) {
//                    count++;

                    String text = each.text();
                    OneWeiBo oneWeiBo = LangTool.run(text);
                    double sentimentValue = getSentimentValue(oneWeiBo.getText());
                    Text valueInfo = new Text();
                    valueInfo.set(oneWeiBo.getTime() + "&" + sentimentValue);
                    context.write(new Text(uid), valueInfo);
                }
            }
        }
    }
    public String Login(String url) {
        int ranInt = (int) (Math.random() * 5);
        try {
            Thread.sleep(1000 * ranInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int agentIndex = (int) (Math.random() * userAgents.length);
        int cookieIndex = (int)(Math.random()*cookies.length);
//        cookieIndex = 3;
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
            System.out.println("URL："+url);
            System.out.println("Cookie："+cookieIndex);
            System.out.println("UserAgent:"+agentIndex);
            CloseableHttpResponse resp = client.execute(get);
            System.out.println(resp.getStatusLine());
            HttpEntity entity = resp.getEntity();
            cont = EntityUtils.toString(entity);
            resp.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return cont;
    }
    private double getSentimentValue(String text) {
        JSONObject res = client.sentimentClassify(text, null);
        System.out.println(res.toString(2));
        JSONObject item = (JSONObject) res.getJSONArray("items").get(0);
        double confidence = item.getDouble("confidence");
        double sentimentValue = 50;
        if(confidence >= THRESHOLD_CONFIDENCE) {
            sentimentValue = item.getDouble("positive_prob") * 100;
        }
        return sentimentValue;
    }
}
