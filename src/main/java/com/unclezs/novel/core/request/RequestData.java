package com.unclezs.novel.core.request;

import com.unclezs.novel.core.utils.StringUtil;
import com.unclezs.novel.core.utils.regex.PatternPool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 请求数据
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 5:51 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestData {
    public static final String REFERER = "Referer";
    public static final String COOKIE = "Cookie";
    public static final String USER_AGENT = "User-Agent";
    /**
     * 请求链接
     */
    private String url;
    /**
     * 是否为post请求
     */
    @Builder.Default
    private boolean post = false;
    /**
     * 网页编码
     */
    @Builder.Default
    private String charset = StandardCharsets.UTF_8.toString();
    /**
     * 请求头
     */
    private Map<String, String> headers;
    /**
     * 请求方式
     */
    @Builder.Default
    private String mediaType = MediaType.NONE.getMediaType();
    /**
     * 请求体
     */
    private String body;

    /**
     * 是否为动态网页
     */
    @Builder.Default
    private boolean dynamic = false;

    /**
     * HTTP代理端口
     */
    private Integer proxyPort;
    /**
     * HTTP代理HOST
     */
    private String proxyHost;
    /**
     * 启用HTTP代理
     */
    @Builder.Default
    private boolean enableProxy = false;

    /**
     * 默认请求配置
     *
     * @param url url
     * @return /
     */
    public static RequestData defaultRequestData(String url) {
        return defaultBuilder(url).build();
    }

    /**
     * 默认请求配置
     *
     * @param url url
     * @return /
     */
    public static RequestDataBuilder defaultBuilder(String url) {
        return builder().charset(StandardCharsets.UTF_8.toString())
            .mediaType(MediaType.NONE.getMediaType()).enableProxy(false).dynamic(false).post(false).url(url);
    }

    /**
     * 代理是否合法
     *
     * @return true 合法
     */
    public boolean proxyValid() {
        return StringUtil.isNotEmpty(proxyHost) && PatternPool.IPV4.matcher(this.proxyHost).matches()
            && proxyPort != null && proxyPort > 0;
    }
}
