package com.unclezs.novel.analyzer.request;

import com.unclezs.novel.analyzer.model.Verifiable;
import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求数据
 *
 * @author blog.unclezs.com
 * @since 2020/12/20 5:51 下午
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestParams implements Cloneable, Verifiable {
    public static final String REFERER = "Referer";
    public static final String COOKIE = "Cookie";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENT_DEFAULT_VALUE = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";
    /**
     * 请求链接
     */
    private String url;
    /**
     * 请求方法
     */
    @Builder.Default
    private String method = "GET";
    /**
     * 网页编码
     */
    private String charset;
    /**
     * 请求头
     */
    private Map<String, String> headers;
    /**
     * 请求方式
     */
    @Builder.Default
    private String mediaType = MediaType.FORM.getMediaType();
    /**
     * 请求体
     */
    @Builder.Default
    private String body = StringUtils.EMPTY;
    /**
     * 是否为动态网页
     */
    @Builder.Default
    private boolean dynamic = false;
    /**
     * HTTP代理信息
     */
    private HttpProxy proxy;
    /**
     * 启用HTTP代理 标记允许代理 如果开发了全局代理配置这个字段将会自动设置为true
     */
    @Builder.Default
    private boolean enableProxy = false;
    /**
     * 自动代理
     * 请在全局代理中开启自动代理配置 并且设置此字段为true 才会真正启用全局代理
     * 这么做是为了方便全局控制代理的热拔插
     * 优先级 先判断此此字段为true 再判断全局AnalyzerManager.autoProxy是否开启
     */
    @Builder.Default
    private boolean autoProxy = true;

    public String getMethod() {
        return StringUtils.isNotEmpty(method) ? method.toUpperCase() : HttpMethod.GET.name();
    }

    /**
     * 是否启用代理
     *
     * @return true则启用
     */
    public boolean isEnableProxy() {
        if (this.proxy == null || this.proxy == HttpProxy.NO_PROXY) {
            return false;
        }
        return enableProxy;
    }

    /**
     * 获取MediaType,非GET默认为FORM
     *
     * @return mediaType
     */
    public String getMediaType() {
        if (StringUtils.isNotBlank(mediaType)) {
            if (MediaType.FORM.name().equalsIgnoreCase(mediaType)) {
                return MediaType.FORM.getMediaType();
            }
            if (MediaType.JSON.name().equalsIgnoreCase(mediaType)) {
                return MediaType.JSON.getMediaType();
            }
        }
        return mediaType;
    }

    /**
     * 默认请求配置
     *
     * @param url url
     * @return /
     */
    public static RequestParams create(String url) {
        return builder().url(url).build();
    }

    /**
     * 复制一份
     *
     * @return /
     */
    public RequestParams copy() {
        try {
            return (RequestParams) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone requestData error.", e);
        }
    }

    /**
     * 获取请求头
     *
     * @param headerName  名称
     * @param defaultName 默认值
     * @return 请求头
     */
    public String getHeader(String headerName, String defaultName) {
        if (CollectionUtils.isNotEmpty(headers)) {
            return headers.getOrDefault(headerName, defaultName);
        }
        return defaultName;
    }

    /**
     * 获取请求头
     *
     * @param headerName 名称
     * @return 请求头
     */
    public String getHeader(String headerName) {
        if (CollectionUtils.isNotEmpty(headers)) {
            return headers.get(headerName);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 设置请求头 存在则覆盖
     *
     * @param headerName  名称
     * @param headerValue 值
     */
    public void setHeader(String headerName, String headerValue) {
        if (headers == null) {
            headers = new HashMap<>(8);
        }
        headers.put(headerName, headerValue);
    }

    /**
     * 设置请求头 如果不存在则设置 存在则忽略
     *
     * @param headerName  名称
     * @param headerValue 值
     */
    public void addHeader(String headerName, String headerValue) {
        if (headers == null) {
            headers = new HashMap<>(8);
            headers.put(headerName, headerValue);
        } else {
            headers.putIfAbsent(headerName, headerValue);
        }
    }

    @Override
    public boolean isEffective() {
        return UrlUtils.isHttpUrl(url);
    }
}
