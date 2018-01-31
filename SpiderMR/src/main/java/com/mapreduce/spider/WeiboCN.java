package com.mapreduce.spider;

import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Set;

/**
 * 利用Selenium获取登陆新浪微博weibo.cn的cookie
 *
 * @author hu
 */
public class WeiboCN {

    /**
     * 获取新浪微博的cookie，这个方法针对weibo.cn有效，对weibo.com无效 weibo.cn以明文形式传输数据，请使用小号
     *
     * @param username 新浪微博用户名
     * @param password 新浪微博密码
     * @return
     * @throws Exception
     */
    public static String getSinaCookie(String username, String password) throws Exception {
        String mycookie = "_T_WM=918d43a7f5805f40e60d654b3ccc3301; ALF=1519991699; "
                +"SCF=AgDWR313JuXm8k8mQnzvC2QugmwEc_65aDyMTbV2LFZ4HphBufkwEl7KgLbZEXYjCOnMfETkJYMBgXzoVWABtCo.; "
                +"SUB=_2A253dd8dDeThGeBM61sS8yfIyjyIHXVUmeFVrDV6PUJbktBeLRjBkW1NRRFx_yTanlvWccBs3jr9yi_YhuPv44k2; "
                +"SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5OGKSDVF6Bu7d9DXNCWFXm5JpX5K-hUgL.FoqEeh.0e0."
                +"XeK52dJLoIE9SqPWC9gxfqc-LxKBLB.eL1-2LxK-LBo.LBonLxKMLBKqL12qR15tt; SUHB=0btshTBE2Gksba; SSOLoginState=1517399885";
        StringBuilder sb = new StringBuilder();
        HtmlUnitDriver driver = new HtmlUnitDriver();
        driver.setJavascriptEnabled(true);
        driver.get("http://passport.weibo.cn/signin/login");
        WebElement ele = driver.findElementByCssSelector("img");
        String src = ele.getAttribute("src");
        String cookie = mycookie;
        HttpRequest request = new HttpRequest(src);
        request.setCookie(cookie);
        HttpResponse response = request.response();
        ByteArrayInputStream is = new ByteArrayInputStream(response.getContent());
        BufferedImage img = ImageIO.read(is);
        is.close();
        ImageIO.write(img, "png", new File("result.png"));
        String userInput = new CaptchaFrame(img).getUserInput();
        WebElement mobile = driver.findElementByCssSelector("input[name=mobile]");
        mobile.sendKeys(username);
        WebElement pass = driver.findElementByCssSelector("input[name^=password]");
        pass.sendKeys(password);
        WebElement code = driver.findElementByCssSelector("input[name=code]");
        code.sendKeys(userInput);
        WebElement rem = driver.findElementByCssSelector("input[name=remember]");
        rem.click();
        WebElement submit = driver.findElementByCssSelector("input[name=submit]");
        submit.click();
        String result = concatCookie(driver);
        driver.close();
        if (result.contains("gsid_CTandWM")) {
            return result;
        } else {
            throw new Exception("weibo login failed");
        }
    }

    public static String concatCookie(HtmlUnitDriver driver) {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        String result = sb.toString();
        return result;
    }

    public static class CaptchaFrame {

        JFrame frame;
        JPanel panel;
        JTextField input;
        int inputWidth = 100;
        BufferedImage img;
        String userInput = null;

        public CaptchaFrame(BufferedImage img) {
            this.img = img;
        }

        public String getUserInput() {
            frame = new JFrame("输入验证码");
            final int imgWidth = img.getWidth();
            final int imgHeight = img.getHeight();
            int width = imgWidth * 2 + inputWidth * 2;
            int height = imgHeight * 2+50;
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int startx = (dim.width - width) / 2;
            int starty = (dim.height - height) / 2;
            frame.setBounds(startx, starty, width, height);
            Container container = frame.getContentPane();
            container.setLayout(new BorderLayout());
            panel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img, 0, 0, imgWidth * 2, imgHeight * 2, null);
                }
            };
            panel.setLayout(null);
            container.add(panel);
            input = new JTextField(6);
            input.setBounds(imgWidth * 2, 0, inputWidth, imgHeight * 2);
            panel.add(input);
            JButton btn = new JButton("登录");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userInput = input.getText().trim();
                    synchronized (CaptchaFrame.this) {
                        CaptchaFrame.this.notify();
                    }
                }
            });
            btn.setBounds(imgWidth * 2 + inputWidth, 0, inputWidth, imgHeight * 2);
            panel.add(btn);
            frame.setVisible(true);
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            frame.dispose();
            return userInput;
        }
    }

}