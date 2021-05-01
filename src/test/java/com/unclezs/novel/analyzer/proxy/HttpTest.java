package com.unclezs.novel.analyzer.proxy;

import com.unclezs.novel.analyzer.util.io.IoUtils;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author blog.unclezs.com
 * @date 2021/4/29 10:06
 */
public class HttpTest {
  public static void main(String[] args) throws Exception {
    com.unclezs.novel.analyzer.proxy.ProxyUtils.setHttpProxyHost("127.0.0.1");
    com.unclezs.novel.analyzer.proxy.ProxyUtils.setHttpProxyPort("1081");
    ProxyUtils.setEnabled(true);
    URLConnection connection = new URL("http://www.cip.cc/").openConnection();
    InputStream stream = connection.getInputStream();
    String s = new String(IoUtils.readBytes(stream));
    System.out.println(Jsoup.parse(s).select(".kq-well pre").text());
    ProxyUtils.setEnabled(false);
    URLConnection connection1 = new URL("http://www.cip.cc/").openConnection();
    InputStream stream1 = connection1.getInputStream();
    System.out.println(Jsoup.parse(new java.lang.String(IoUtils.readBytes(stream1))).select(".kq-well pre").text());
  }
}
