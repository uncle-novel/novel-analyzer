package com.unclezs.novel.core.request.spi;

import com.unclezs.novel.core.request.proxy.HttpProxy;

/**
 * 代理提供类
 *
 * @author blog.unclezs.com
 * @date 2020/12/27 12:18 下午
 */
public interface ProxyProvider {
    /**
     * 获取一个可用代理
     *
     * @return /
     */
    HttpProxy getProxy();

    /**
     * 移除代理 在请求失败的时候会进行调用
     *
     * @param proxy 代理
     */
    void removeProxy(HttpProxy proxy);
}
