package com.unclezs.novel.core.request;

import com.unclezs.novel.core.request.okhttp.OkHttpClient;
import com.unclezs.novel.core.request.spi.HttpProvider;
import com.unclezs.novel.core.request.phantomjs.PhantomJsClient;
import com.unclezs.novel.core.utils.StringUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;

/**
 * okHttp请求工具
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
    private HttpProvider staticHttpClient;

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

    }

    /**
     * 获取http请求内容
     *
     * @param requestData /
     * @return /
     */
    public String content(RequestData requestData) throws IOException {
        if (requestData.isDynamic()) {
            return dynamicHttpClient.content(requestData);
        } else {
            return staticHttpClient.content(requestData);
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
        return StringUtil.EMPTY;
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
        if (requestData.isDynamic()) {
            return dynamicHttpClient.stream(requestData);
        } else {
            return staticHttpClient.stream(requestData);
        }
    }
}
