package com.unclezs.novel.core.request.phantomjs;

import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.utils.CollectionUtil;
import com.unclezs.novel.core.utils.StringUtil;
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
    private String proxy = StringUtil.EMPTY;
    /**
     * User-Agent 为空则使用默认
     */
    private String userAgent = StringUtil.EMPTY;
    /**
     * 防盗链 为空则默认URL
     */
    private String referer = StringUtil.EMPTY;
    /**
     * Cookie
     */
    private String cookie = StringUtil.EMPTY;
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
        if (CollectionUtil.isNotEmpty(requestData.getHeaders())) {
            data.setUserAgent(requestData.getHeaders().getOrDefault(RequestData.USER_AGENT, StringUtil.EMPTY));
            data.setCookie(requestData.getHeaders().getOrDefault(RequestData.COOKIE, StringUtil.EMPTY));
            data.setReferer(requestData.getHeaders().getOrDefault(RequestData.REFERER, StringUtil.EMPTY));
        }
        // 代理
        if (requestData.proxyValid() && requestData.isEnableProxy()) {
            data.setProxy(String.format("%s:%d", requestData.getProxyHost(), requestData.getProxyPort()));
        }
        return data;
    }
}
