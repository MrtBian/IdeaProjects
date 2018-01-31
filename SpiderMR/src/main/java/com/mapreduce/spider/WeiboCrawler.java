package com.mapreduce.spider;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 利用WebCollector和获取的cookie爬取新浪微博并抽取数据
 * @author hu
 */
public class WeiboCrawler extends BreadthCrawler {

    String cookie;

    public WeiboCrawler(String crawlPath, boolean autoParse) throws Exception {
        super(crawlPath, autoParse);
        /*获取新浪微博的cookie，账号密码以明文形式传输，请使用小号*/
        cookie = "_T_WM=918d43a7f5805f40e60d654b3ccc3301; ALF=1519991699; "
                +"SCF=AgDWR313JuXm8k8mQnzvC2QugmwEc_65aDyMTbV2LFZ4HphBufkwEl7KgLbZEXYjCOnMfETkJYMBgXzoVWABtCo.; "
                +"SUB=_2A253dd8dDeThGeBM61sS8yfIyjyIHXVUmeFVrDV6PUJbktBeLRjBkW1NRRFx_yTanlvWccBs3jr9yi_YhuPv44k2; "
                +"SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5OGKSDVF6Bu7d9DXNCWFXm5JpX5K-hUgL.FoqEeh.0e0."
                +"XeK52dJLoIE9SqPWC9gxfqc-LxKBLB.eL1-2LxK-LBo.LBonLxKMLBKqL12qR15tt; SUHB=0btshTBE2Gksba; SSOLoginState=1517399885";
//        cookie = WeiboCN.getSinaCookie("bianxy96@gmail.com", "19960420bxy");
    }

    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setCookie(cookie);
        return request.responsePage();
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        int pageNum = Integer.valueOf(page.meta("pageNum"));
        /*抽取微博*/
        Elements weibos = page.select("div.c");
        for (Element weibo : weibos) {
            System.out.println("第" + pageNum + "页\t" + weibo.text());
        }
    }

    public static void main(String[] args) throws Exception {
        WeiboCrawler crawler = new WeiboCrawler("weibo_crawler", false);
        crawler.setThreads(3);
        /*对某人微博前5页进行爬取*/
        for (int i = 1; i <= 50; i++) {
            crawler.addSeed(new CrawlDatum("http://weibo.cn/u/3865692567?vt=4&page=" + i)
                    .meta("pageNum", i + ""));
        }
        crawler.start(1);
    }

}