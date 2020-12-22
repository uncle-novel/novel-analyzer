package com.unclezs.novel.analyzer.request;

import lombok.Data;

import java.net.Proxy;

/**
 * @author blog.unclezs.com
 * @date 2020/12/21 12:47 上午
 */
@Data
public class HttpConfig {
    /**
     * 读取超时时间
     */
    private long readTimeout = 10L;
    /**
     * 连接超时时间
     */
    private long connectionTimeout = 10L;
    /**
     * 自动重定向
     */
    private boolean followRedirect = true;
    /**
     * 连接失败时重试
     */
    private boolean retryOnFailed = true;
    /**
     * 连接池最大空闲连接
     */
    private int maxIdleConnections = 5;
    /**
     * 连接池空闲连存活时间 秒
     */
    private long keepAliveDuration = 5L;
    /**
     * 代理
     */
    private Proxy proxy = Proxy.NO_PROXY;

    /**
     * 默认配置
     *
     * @return /
     */
    public static HttpConfig defaultConfig() {
        return new HttpConfig();
    }
}
