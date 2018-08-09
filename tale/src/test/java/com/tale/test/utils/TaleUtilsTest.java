package com.tale.test.utils;

import com.sun.syndication.io.FeedException;
import com.tale.model.entity.Contents;
import com.tale.test.ALLTests;
import com.tale.utils.TaleUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TaleUtilsTest extends ALLTests {

    @Test
    public void testIsEmail() {
        String email1 = "SX1716006@qq.com";
        String email2 = "ljw1995@163.com";
        String notEmail = "SX1716006#132.com";
        Assert.assertTrue(TaleUtils.isEmail(email1));
        Assert.assertTrue(TaleUtils.isEmail(email2));
        Assert.assertFalse(TaleUtils.isEmail(notEmail));
    }

    @Test
    public void testIsPath() {
        String path = "install";
        String notPath = "SX1716006/LJW";
        Assert.assertTrue(TaleUtils.isPath(path));
        Assert.assertFalse(TaleUtils.isPath(notPath));
    }

    @Test
    public void testGetSitemapXml() {
        Contents contents = new Contents();
        contents.setCid(1);
        contents.setHits(3);
        contents.setTitle("李俊薇的文章");
        contents.setSlug("SX1716006");
        contents.setContent("这是一次测试！！！");
        contents.setCreated(200);
        contents.setModified(201);
        contents.setAuthorId(123);
        ArrayList<Contents> cs = new ArrayList<>();
        cs.add(contents);
        String sitemapXml = TaleUtils.getSitemapXml(cs);
        Assert.assertEquals(sitemapXml, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<urlset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 " +
                "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
                "<url><loc>http://127.0.0.1:9000/article/1</loc><lastmod>1970-01-01T08:03:21.000+08:00</lastmod></url>\n" +
                "<url><loc>http://127.0.0.1:9000/archives</loc></url></urlset>");
    }

    @Test
    public void testRssXml() {
        Contents contents = new Contents();
        contents.setCid(1);
        contents.setHits(3);
        contents.setTitle("李俊薇第二篇文章");
        contents.setSlug("SX1716006");
        contents.setContent("这是第二次次测试！！！");
        contents.setCreated(12311421);
        contents.setModified(12312342);
        contents.setAuthorId(123);
        ArrayList<Contents> cs = new ArrayList<>();
        cs.add(contents);
        String rssXml = null;
        try {
            rssXml = TaleUtils.getRssXml(cs);
        } catch (FeedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(rssXml, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<rss xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" version=\"2.0\">\r\n" +
                "  <channel>\r\n" +
                "    <title>李俊薇的技术博客</title>\r\n" +
                "    <link>http://127.0.0.1:9000</link>\r\n" +
                "    <description>博客系统,Blade框架,Tale</description>\r\n" +
                "    <language>zh-CN</language>\r\n" +
                "    <item>\r\n" +
                "      <title>李俊薇第二篇文章</title>\r\n" +
                "      <link>http://127.0.0.1:9000/article/SX1716006</link>\r\n" +
                "      <content:encoded>&lt;p&gt;这是第二次次测试！！！&lt;/p&gt;</content:encoded>\r\n" +
                "      <pubDate>Sat, 23 May 1970 11:50:21 GMT</pubDate>\r\n" +
                "    </item>\r\n" +
                "  </channel>\r\n" +
                "</rss>\r\n\r\n");
    }
}
