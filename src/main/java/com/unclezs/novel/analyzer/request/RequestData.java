package com.unclezs.novel.analyzer.request;

import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 请求数据
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 5:51 下午
 */
@Data
public class RequestData {
    /**
     * 请求链接
     */
    private String url;
    /**
     * 是否为post请求
     */
    private boolean post = false;
    /**
     * 网页编码
     */
    private String charset = StandardCharsets.UTF_8.toString();
    /**
     * 请求头
     */
    private Map<String, String> headers;
    /**
     * 请求方式
     */
    private String mediaType = MediaType.NONE.getMediaType();
    /**
     * 请求体
     */
    private String body;

}
