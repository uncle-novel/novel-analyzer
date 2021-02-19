package com.unclezs.novel.analyzer.request.spi;

import com.unclezs.novel.analyzer.request.RequestParams;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2020/12/24 11:09
 */
public interface HttpProvider {
    /**
     * 获取网页内容
     *
     * @param requestParams /
     * @return /
     * @throws IOException /
     */
    String content(RequestParams requestParams) throws IOException;

    /**
     * 获取流
     *
     * @param requestParams /
     * @return /
     * @throws IOException /
     */
    byte[] bytes(RequestParams requestParams) throws IOException;

    /**
     * 是否为动态网页客户端
     *
     * @return /
     */
    boolean isDynamic();
}
