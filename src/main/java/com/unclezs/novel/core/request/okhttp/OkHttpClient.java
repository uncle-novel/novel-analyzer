package com.unclezs.novel.core.request.okhttp;

import com.unclezs.novel.core.request.HttpConfig;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.request.spi.HttpProvider;
import com.unclezs.novel.core.request.ssl.SslTrustAllCerts;
import com.unclezs.novel.core.utils.CollectionUtil;
import com.unclezs.novel.core.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okHttp请求工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 12:31 上午
 */
@Slf4j
public class OkHttpClient implements HttpProvider {
    /**
     * OkHttp客户端 静态网页
     */
    private okhttp3.OkHttpClient staticHttpClient;

    public OkHttpClient() {
        configuration(HttpConfig.defaultConfig());
    }

    /**
     * 配置OkHttpClient
     *
     * @param config 配置信息
     */
    public void configuration(HttpConfig config) {
        // SSL配置
        SslTrustAllCerts sslTrustAllCerts = new SslTrustAllCerts();
        SSLSocketFactory sslSocketFactory = createSslSocketFactory(sslTrustAllCerts);
        staticHttpClient = new okhttp3.OkHttpClient.Builder()
            // 连接池
            .connectionPool(
                new ConnectionPool(config.getMaxIdleConnections(), config.getKeepAliveDuration(), TimeUnit.SECONDS))
            // 超时
            .connectTimeout(config.getConnectionTimeout(), TimeUnit.SECONDS)
            .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
            // 代理
            .proxy(config.getProxy())
            // 信任所有SSL
            .sslSocketFactory(sslSocketFactory, sslTrustAllCerts)
            .hostnameVerifier((requestedHost, remoteServerSession) -> requestedHost.equalsIgnoreCase(
                remoteServerSession.getPeerHost()))
            // 自动跟随重定向
            .followRedirects(config.isFollowRedirect())
            // 连接失败自动重试
            .retryOnConnectionFailure(config.isRetryOnFailed())
            .build();
    }

    /**
     * 创建SSL factory工厂
     *
     * @param manager X509TrustManager
     * @return /
     */
    private SSLSocketFactory createSslSocketFactory(X509TrustManager manager) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] {manager}, new SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception ignored) {
            log.warn("SSL Factory 创建失败，使用默认的SSL Factory");
        }
        return (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    /**
     * 发起HTTP请求
     *
     * @param requestData 请求数据
     * @return /
     */
    public Call init(RequestData requestData) {
        Request.Builder request = new Request.Builder().url(requestData.getUrl());
        // 请求头
        if (CollectionUtil.isNotEmpty(requestData.getHeaders())) {
            for (Map.Entry<String, String> header : requestData.getHeaders().entrySet()) {
                request.addHeader(header.getKey(), header.getValue());
            }
        }
        // 请求方法
        if (requestData.isPost()) {
            request.post(RequestBody.create(requestData.getBody(), MediaType.get(requestData.getMediaType())));
        } else {
            request.get();
        }
        // 请求头
        if (CollectionUtil.isNotEmpty(requestData.getHeaders())) {
            for (Map.Entry<String, String> entry : requestData.getHeaders().entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        // 如果要使用代理
        if (requestData.isEnableProxy()) {
            // 创建代理
            InetSocketAddress inetSocketAddress =
                new InetSocketAddress(requestData.getProxy().getHost(), requestData.getProxy().getPort());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
            // 复用client线程池与连接池及配置 使用代理
            okhttp3.OkHttpClient client = staticHttpClient.newBuilder().proxy(proxy).build();
            return client.newCall(request.build());
        }
        return staticHttpClient.newCall(request.build());
    }

    /**
     * 获取http请求内容
     *
     * @param requestData /
     * @return
     */
    @Override
    public String content(RequestData requestData) throws IOException {
        Call request = init(requestData);
        try (Response response = request.execute()) {
            handleFailed(response);
            ResponseBody body = response.body();
            if (body == null) {
                return StringUtil.EMPTY;
            } else {
                return body.string();
            }
        }
    }

    /**
     * 获取 get http请求内容
     *
     * @param url /
     * @return null if error.
     */
    public String get(String url) {
        RequestData requestData = RequestData.builder().url(url).build();
        try {
            return content(requestData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtil.EMPTY;
    }

    /**
     * 获取流
     *
     * @param requestData 请求数据
     * @return /
     * @throws IOException 请求失败
     */
    @Override
    public InputStream stream(RequestData requestData) throws IOException {
        Call request = init(requestData);
        try (Response response = request.execute()) {
            handleFailed(response);
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            } else {
                return body.byteStream();
            }
        }
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    /**
     * 处理失败
     *
     * @param response 响应
     * @throws IOException 失败
     */
    private void handleFailed(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("错误的状态码，非200-299 ：" + response);
        }
    }
}
