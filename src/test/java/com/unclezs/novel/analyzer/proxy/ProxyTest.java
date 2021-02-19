package com.unclezs.novel.analyzer.proxy;

import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.proxy.DefaultProxyProvider;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @since 2021/01/11 16:03
 */
public class ProxyTest {
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
