package com.unclezs.novel.analyzer.request.phantomjs;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Data;

/**
 * PhantomJs请求参数
 *
 * @author blog.unclezs.com
 * @date 2020/12/25 11:17
 */
@Data
public class PhantomJsRequestParams {
    /**
     * URL
     */
    private String url;
    /**
     * 代理 127.0.0.1:80
     */
    private String proxy = StringUtils.EMPTY;
    /**
     * User-Agent 为空则使用默认
     */
    private String userAgent = StringUtils.EMPTY;
    /**
     * 防盗链 为空则默认URL
     */
    private String referer = StringUtils.EMPTY;
    /**
     * Cookie
     */
    private String cookie = StringUtils.EMPTY;
    /**
     * 是否加载图片 默认false 提高速度
     */
    private boolean loadImg = false;
    /**
     * 忽略SSL错误
     */
    private boolean ignoreSslError = true;

    public PhantomJsRequestParams(String url) {
        this.url = url;
    }

    /**
     * 从RequestData进行转化
     *
     * @param requestParams {@code com.unclezs.novel.core.com.unclezs.novel.analyzer.request.RequestData}
     * @return /
     */
    public static PhantomJsRequestParams from(RequestParams requestParams) {
        PhantomJsRequestParams data = new PhantomJsRequestParams(requestParams.getUrl());
        // 请求头
        if (CollectionUtils.isNotEmpty(requestParams.getHeaders())) {
            data.setUserAgent(requestParams.getHeaders().getOrDefault(RequestParams.USER_AGENT, StringUtils.EMPTY));
            data.setCookie(requestParams.getHeaders().getOrDefault(RequestParams.COOKIE, StringUtils.EMPTY));
            data.setReferer(requestParams.getHeaders().getOrDefault(RequestParams.REFERER, StringUtils.EMPTY));
        }
        // 代理
        if (requestParams.isEnableProxy()) {
            data.setProxy(String.format("%s:%d", requestParams.getProxy().getHost(), requestParams.getProxy().getPort()));
        }
        return data;
    }
}
