package com.unclezs.novel.analyzer.request.spi;

import com.unclezs.novel.analyzer.request.proxy.HttpProxy;

/**
 * HTTP代理提供类
 * <p>
 * 请在全局代理中开启自动代理配置 并且RequestData中设置autoProxy为true
 * 这么做是为了方便全局控制代理的热拔插
 * 优先级 先判断此此字段为true 再判断全局AnalyzerManager.autoProxy是否开启
 *
 * @author blog.unclezs.com
 * @since 2020/12/27 12:18 下午
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
