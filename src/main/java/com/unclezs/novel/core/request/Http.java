package com.unclezs.novel.core.request;

import com.unclezs.novel.core.AnalyzerManager;
import com.unclezs.novel.core.request.okhttp.OkHttpClient;
import com.unclezs.novel.core.request.phantomjs.PhantomJsClient;
import com.unclezs.novel.core.request.proxy.DefaultProxyProvider;
import com.unclezs.novel.core.request.spi.HttpProvider;
import com.unclezs.novel.core.request.spi.ProxyProvider;
import com.unclezs.novel.core.util.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Http请求工具，支持动态与静态网页
 * 兼容SPI机制加载自定义HTTP客户端与代理提供商
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 12:31 上午
 */
@Slf4j
@UtilityClass
public class Http {
    /**
     * 动态网页Http客户端
     */
    private static HttpProvider dynamicHttpClient;
    /**
     * 静态网页Http客户端
     */
    private static HttpProvider staticHttpClient;
    private static final ProxyProvider PROXY_PROVIDER;

    static {
        // 加载自定义的 动态/静态网页Http客户端
        ServiceLoader<HttpProvider> httpProviders = ServiceLoader.load(HttpProvider.class);
        for (HttpProvider provider : httpProviders) {
            if (provider.isDynamic()) {
                dynamicHttpClient = provider;
            } else {
                staticHttpClient = provider;
            }
        }
        // 没有提供则使用默认
        if (dynamicHttpClient == null) {
            dynamicHttpClient = new PhantomJsClient();
        }
        if (staticHttpClient == null) {
            staticHttpClient = new OkHttpClient();
        }

        // 初始化ProxyProvider 没有就使用默认的
        ServiceLoader<ProxyProvider> proxyProviders = ServiceLoader.load(ProxyProvider.class);
        Iterator<ProxyProvider> proxyProviderIterator = proxyProviders.iterator();
        if (proxyProviderIterator.hasNext()) {
            PROXY_PROVIDER = proxyProviderIterator.next();
        } else {
            PROXY_PROVIDER = new DefaultProxyProvider();
        }
    }


    /**
     * 获取http请求内容
     *
     * @param requestData /
     * @return /
     */
    public String content(RequestData requestData) throws IOException {
        autoProxy(requestData);
        try {
            if (requestData.isDynamic()) {
                return dynamicHttpClient.content(requestData);
            } else {
                return staticHttpClient.content(requestData);
            }
        } catch (IOException e) {
            proxyFailed(requestData);
            throw new IOException(e);
        }
    }

    /**
     * 获取 get http请求内容
     *
     * @param url /
     * @return null if error.
     */
    public String get(String url, boolean dynamic) {
        RequestData requestData = RequestData.builder().dynamic(dynamic).url(url).build();
        try {
            return content(requestData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取 get http请求内容 静态网页
     *
     * @param url /
     * @return null if error.
     */
    public String get(String url) {
        return get(url, false);
    }

    /**
     * 获取流
     *
     * @param requestData 请求数据
     * @return /
     * @throws IOException 请求失败
     */
    public InputStream stream(RequestData requestData) throws IOException {
        autoProxy(requestData);
        try {
            if (requestData.isDynamic()) {
                return dynamicHttpClient.stream(requestData);
            } else {
                return staticHttpClient.stream(requestData);
            }
        } catch (IOException e) {
            proxyFailed(requestData);
            throw new IOException(e);
        }
    }

    /**
     * 自动代理 如果配置之后自动从代理池中获取代理，每个请求随机代理
     *
     * @param requestData /
     */
    private void autoProxy(RequestData requestData) {
        if (AnalyzerManager.me().isAutoProxy()) {
            requestData.setAutoProxy(true);
            // 运行代理
            requestData.setEnableProxy(true);
            requestData.setProxy(PROXY_PROVIDER.getProxy());
        }
    }

    /**
     * 请求失败移除代理
     *
     * @param requestData /
     */
    private void proxyFailed(RequestData requestData) {
        if (requestData.isAutoProxy()) {
            PROXY_PROVIDER.removeProxy(requestData.getProxy());
        }
    }
}
