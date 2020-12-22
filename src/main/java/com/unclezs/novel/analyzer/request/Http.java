package com.unclezs.novel.analyzer.request;

import com.unclezs.novel.analyzer.request.ssl.SslTrustAllCerts;
import com.unclezs.novel.analyzer.utils.CollectionUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
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
@UtilityClass
public class Http {
    /**
     * OkHttp客户端
     */
    private OkHttpClient client;

    static {
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
        client = new OkHttpClient.Builder()
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
            .hostnameVerifier((s, sslSession) -> true)
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
    private static SSLSocketFactory createSslSocketFactory(X509TrustManager manager) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{manager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }
        return ssfFactory;
    }

    /**
     * 发起HTTP请求
     *
     * @param requestData 请求数据
     * @return /
     * @throws IOException 请求失败
     */
    public Call init(RequestData requestData) throws IOException {
        Request.Builder request = new Request.Builder().url(requestData.getUrl());
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
        return client.newCall(request.build());
    }

    /**
     * 获取http请求内容
     *
     * @param requestData /
     * @return
     */
    public String content(RequestData requestData) throws IOException {
        Call request = init(requestData);
        try (Response response = request.execute()) {
            handleFailed(response);
            ResponseBody body = response.body();
            if (body == null) {
                return "";
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
        return "";
    }

    /**
     * 获取流
     *
     * @param requestData 请求数据
     * @return /
     * @throws IOException 请求失败
     */
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
