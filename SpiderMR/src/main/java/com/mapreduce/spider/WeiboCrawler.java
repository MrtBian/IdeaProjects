package com.mapreduce.spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 利用WebCollector和获取的cookie爬取新浪微博并抽取数据
 * @author hu
 */
public class WeiboCrawler {

    String cookie;

    public WeiboCrawler(String crawlPath, boolean autoParse) throws Exception {

        /*获取新浪微博的cookie，账号密码以明文形式传输，请使用小号*/
        cookie = "_T_WM=918d43a7f5805f40e60d654b3ccc3301; ALF=1519991699; "
                +"SCF=AgDWR313JuXm8k8mQnzvC2QugmwEc_65aDyMTbV2LFZ4HphBufkwEl7KgLbZEXYjCOnMfETkJYMBgXzoVWABtCo.; "
                +"SUB=_2A253dd8dDeThGeBM61sS8yfIyjyIHXVUmeFVrDV6PUJbktBeLRjBkW1NRRFx_yTanlvWccBs3jr9yi_YhuPv44k2; "
                +"SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5OGKSDVF6Bu7d9DXNCWFXm5JpX5K-hUgL.FoqEeh.0e0."
                +"XeK52dJLoIE9SqPWC9gxfqc-LxKBLB.eL1-2LxK-LBo.LBonLxKMLBKqL12qR15tt; SUHB=0btshTBE2Gksba; SSOLoginState=1517399885";
//        cookie = WeiboCN.getSinaCookie("bianxy96@gmail.com", "19960420bxy");
    }



    public static void main(String[] args) throws Exception {

        // 定义即将访问的链接
        String baseUrl = "https://weibo.cn/u/";
        String starUid = "1537790411";
        String url = baseUrl+starUid;
        // 定义一个字符串用来存储网页内容
        String result = "";
        // 定义一个缓冲字符输入流
        BufferedReader in = null;
        try
        {
            // 将string转成url对象
            URL realUrl = new URL(url);
            // 初始化一个链接到那个url的连接
            URLConnection connection = realUrl.openConnection();
            // 开始实际的连接
            connection.connect();
            // 初始化 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // 用来临时存储抓取到的每一行的数据
            String line;
            while ((line = in.readLine()) != null)
            {
                // 遍历抓取到的每一行并将其存储到result里面
                result += line + "\n";
            }
        } catch (Exception e)
        {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        } // 使用finally来关闭输入流
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
        System.out.println(result);
    }
    }

}