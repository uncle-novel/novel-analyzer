package com.unclezs.novel.core.request;

import com.unclezs.novel.core.utils.StringUtil;

/**
 * @author blog.unclezs.com
 * @date 2020/12/21 1:37 上午
 */
public enum MediaType {
    /**
     * 传统表单
     */
    FORM("application/x-www-form-urlencoded"),
    /**
     * GET请求使用
     */
    NONE(StringUtil.EMPTY),
    /**
     * JSON
     */
    JSON("application/json;charset=utf-8");

    /**
     * content-type
     */
    private String mediaType;

    public String getMediaType() {
        return mediaType;
    }

    MediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
