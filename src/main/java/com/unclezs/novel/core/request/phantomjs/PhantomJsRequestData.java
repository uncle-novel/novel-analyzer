package com.unclezs.novel.core.request.phantomjs;

import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.util.CollectionUtils;
import com.unclezs.novel.core.util.StringUtils;
import lombok.Data;

/**
 * PhantomJs请求数据
 *
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/25 11:17
 */
@Data
public class PhantomJsRequestData {
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

    public PhantomJsRequestData(String url) {
        this.url = url;
    }

    /**
     * 从RequestData进行转化
     *
     * @param requestData {@code com.unclezs.novel.core.request.RequestData}
     * @return /
     */
    public static PhantomJsRequestData from(RequestData requestData) {
        PhantomJsRequestData data = new PhantomJsRequestData(requestData.getUrl());
        // 请求头
        if (CollectionUtils.isNotEmpty(requestData.getHeaders())) {
            data.setUserAgent(requestData.getHeaders().getOrDefault(RequestData.USER_AGENT, StringUtils.EMPTY));
            data.setCookie(requestData.getHeaders().getOrDefault(RequestData.COOKIE, StringUtils.EMPTY));
            data.setReferer(requestData.getHeaders().getOrDefault(RequestData.REFERER, StringUtils.EMPTY));
        }
        // 代理
        if (requestData.isEnableProxy()) {
            data.setProxy(String.format("%s:%d", requestData.getProxy().getHost(), requestData.getProxy().getPort()));
        }
        return data;
    }
}
