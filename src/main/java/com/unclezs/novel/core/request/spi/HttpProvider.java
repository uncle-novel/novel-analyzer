package com.unclezs.novel.core.request.spi;

import com.unclezs.novel.core.request.RequestData;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author blog.unclezs.com
 * @since 2020/12/24 11:09
 */
public interface HttpProvider {
    /**
     * 获取网页内容
     *
     * @param requestData /
     * @return /
     * @throws IOException /
     */
    String content(RequestData requestData) throws IOException;

    /**
     * 获取流
     *
     * @param requestData /
     * @return /
     * @throws IOException /
     */
    InputStream stream(RequestData requestData) throws IOException;

    /**
     * 是否为动态网页客户端
     *
     * @return /
     */
    boolean isDynamic();
}
