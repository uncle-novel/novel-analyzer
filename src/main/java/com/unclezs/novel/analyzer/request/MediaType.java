package com.unclezs.novel.analyzer.request;

import com.unclezs.novel.analyzer.util.StringUtils;

/**
 * @author blog.unclezs.com
 * @since 2020/12/21 1:37 上午
 */
public enum MediaType {
    /**
     * 传统表单
     */
    FORM("application/x-www-form-urlencoded"),
    /**
     * GET请求使用
     */
    NONE(StringUtils.EMPTY),
    /**
     * JSON
     */
    JSON("application/json;charset=utf-8");

    /**
     * content-type
     */
    private final String mediaType;

    MediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }
}
