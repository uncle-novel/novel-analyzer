package com.unclezs.novel.analyzer.proxy;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.proxy.DefaultProxyProvider;
import com.unclezs.novel.analyzer.util.io.IoUtils;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author blog.unclezs.com
 * @date 2021/01/11 16:03
 */
public class ProxyTest {
  public static void main(String[] args) throws Exception {
    ProxyUtils.setHttpProxyHost("127.0.0.1");
    ProxyUtils.setHttpProxyPort("1081");
    ProxyUtils.setEnabled(true);
//    OkHttpClient okHttpClient = new OkHttpClient.Builder()
//      .readTimeout(10, TimeUnit.SECONDS)
//      .proxy(Proxy.NO_PROXY)
//      .connectTimeout(10, TimeUnit.SECONDS).build();
//    Request request = new Request.Builder().url("http://www.cip.cc/").build();
//    Response response = okHttpClient.newCall(request).execute();
    System.out.println(Jsoup.parse(Http.get("http://www.cip.cc/")).select(".kq-well pre").text());
//    OkHttpClient okHttpClient1 = new OkHttpClient.Builder()
//      .readTimeout(10, TimeUnit.SECONDS)
//      .proxy(Proxy.NO_PROXY)
//      .connectTimeout(10, TimeUnit.SECONDS).build();
//    Request request2 = new Request.Builder().url("http://www.cip.cc/").build();
//    Response response1 = okHttpClient1.newCall(request2).execute();
    ProxyUtils.setEnabled(false);
    System.out.println(Jsoup.parse(Http.get("http://www.cip.cc/")).select(".kq-well pre").text());
//    test();
  }

  @Test
  public void test() throws Exception {
    System.setProperty("java.net.useSystemProxies", "true");
    URLConnection connection = new URL("http://www.cip.cc/").openConnection();
    InputStream stream = connection.getInputStream();
    String s = new String(IoUtils.readBytes(stream));
    System.out.println(Jsoup.parse(s).select(".kq-well pre").text());
    System.clearProperty("java.net.useSystemProxies");
    System.clearProperty("socksProxyHost");
    System.clearProperty("socksProxyPort");
    System.clearProperty("http.proxyHost");
    System.clearProperty("http.proxyPort");
    URLConnection connection1 = new URL("http://www.cip.cc/").openConnection();
    InputStream stream1 = connection1.getInputStream();
    System.out.println(Jsoup.parse(new java.lang.String(IoUtils.readBytes(stream1))).select(".kq-well pre").text());
  }

  @Test
  public void testProxy() throws IOException {
    DefaultProxyProvider provider = new DefaultProxyProvider();
    provider.setVerify(true);
    System.out.println(provider.getProxy());
    RequestParams requestParams = RequestParams.builder().url("http://icanhazip.com/").autoProxy(false).enableProxy(true).proxy(provider.getProxy()).build();
//        RequestData requestData = RequestData.defaultBuilder("http://ip.3322.net/").autoProxy(false).enableProxy(true).proxy(new HttpProxy("117.185.17.144", 80)).build();
    System.out.println(Http.content(requestParams));
  }
}
