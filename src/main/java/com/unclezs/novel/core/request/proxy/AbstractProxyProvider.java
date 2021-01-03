package com.unclezs.novel.core.request.proxy;

import com.unclezs.novel.core.concurrent.pool.ThreadPoolUtil;
import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.request.spi.ProxyProvider;
import com.unclezs.novel.core.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 默认代理提供抽象类
 * <p>
 * 1.提供一个代理缓存池<br/>
 * 2.代理池的CRUD方法<br/>
 * 3.getProxy方法每次都随机返回一个代理<br/>
 * 4.可以限制代理池的最大容量<br/>
 * 5.可以控制是否在获取代理时校验代理是否有效<br/>
 * <p>
 * 并非线程安全的类，但是高效
 *
 * @author blog.unclezs.com
 * @date 2020/12/27 12:24 下午
 */
@Slf4j
public abstract class AbstractProxyProvider implements ProxyProvider {
    private static final HttpProxy NO_PROXY = new HttpProxy("127.0.0.1", 80);
    private static final String VERIFY_URL = "http://httpbin.org/ip";
    /**
     * 代理池, 采用双向队列 key:host  value:port
     */
    private final Map<String, Integer> proxyPool = new HashMap<>();
    /**
     * 索引，用于随机定位一个proxy，存proxy host，链表实现快速删除
     */
    private final LinkedList<String> index = new LinkedList<>();
    /**
     * 是否启用获取代理的时候自动校验，会影响速度
     */
    private boolean verify = false;
    /**
     * 最多容纳多少个代理 -1代表无限
     */
    private int maxProxyNumber = Integer.MAX_VALUE;

    /**
     * 不允许直接创建，需要用子类实现新增代理的逻辑
     */
    protected AbstractProxyProvider() {
    }

    protected AbstractProxyProvider(boolean verify, int maxProxyNumber) {
        this.verify = verify;
        this.maxProxyNumber = maxProxyNumber;
    }

    /**
     * 设置是否校验Proxy
     *
     * @param verify 是否开启校验代理是否有效
     */
    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    /**
     * 校验IP是否有效
     *
     * @param proxy /
     * @return true则失效
     */
    public boolean verifyFailed(HttpProxy proxy) {
        RequestData requestData = RequestData.defaultBuilder(VERIFY_URL).autoProxy(false).enableProxy(true).proxy(proxy).build();
        try {
            return !Http.content(requestData).contains(proxy.getHost());
        } catch (IOException e) {
            log.trace("代理IP无效,Host:{} - Port:{}", proxy.getHost(), proxy.getPort());
            return true;
        }
    }

    /**
     * 加载代理,用于代理池中没有代理的时候调用
     */
    public abstract void loadProxy();

    /**
     * 重置代理池,清空数据
     */
    public void reset() {
        // 清空代理池
        proxyPool.clear();
        // 清空代理池索引
        index.clear();
    }

    @Override
    public HttpProxy getProxy() {
        // 没有代理则进行加载
        if (index.isEmpty()) {
            loadProxy();
        }
        return verify ? randomProxy() : randomVerifiedProxy();
    }

    /**
     * 随机获取代理，如果没有代理则不使用代理
     *
     * @return proxy
     */
    public HttpProxy randomProxy() {
        if (proxyPool.isEmpty()) {
            return NO_PROXY;
        }
        // 随机位置
        int randomInt = RandomUtil.randomInt(index.size());
        // 获取host
        String host = index.get(randomInt);
        // 生成proxy
        return new HttpProxy(host, proxyPool.get(host));
    }

    /**
     * 随机获取一个校验后的代理
     *
     * @return verifiedProxy
     */
    public HttpProxy randomVerifiedProxy() {
        HttpProxy verifiedProxy = randomProxy();
        while (verifyFailed(verifiedProxy)) {
            // 校验失败直接移除代理
            removeProxy(verifiedProxy.getHost());
            verifiedProxy = randomProxy();
        }
        return verifiedProxy;
    }

    /**
     * 校验是否有效，如果有效则不清理(异步执行)
     *
     * @param proxy 代理
     */
    @Override
    public void removeProxy(HttpProxy proxy) {
        if (index.isEmpty()) {
            return;
        }
        // 异步移除
        CleanThreadPoolHolder.EXECUTOR.execute(() -> {
            if (verifyFailed(proxy)) {
                removeProxy(proxy.getHost());
            }
        });
    }

    /**
     * 清理无效线程池的 静态内部类懒加载 不清理则不创建此线程池
     * 单线程池，队列无限长
     */
    static class CleanThreadPoolHolder {
        private CleanThreadPoolHolder() {
        }

        public static final ExecutorService EXECUTOR = ThreadPoolUtil.newSingleThreadExecutor("clean_proxy");
    }

    /**
     * 直接移除代理
     *
     * @param host 代理
     */
    public void removeProxy(String host) {
        if (index.isEmpty()) {
            return;
        }
        index.remove(host);
        proxyPool.remove(host);
    }

    /**
     * 获取当前代理数量
     *
     * @return 代理数量
     */
    public int proxyNum() {
        return index.size();
    }

    /**
     * 添加代理
     *
     * @param host 代理Host
     * @param port 代理端口
     * @return true已经满了
     */
    public boolean addProxy(String host, Integer port) {
        // 线程不安全的控制代理池最大容量与重复添加，不需要线程安全，不重要，高效就完事儿
        if (index.size() < maxProxyNumber && !proxyPool.containsKey(host)) {
            proxyPool.put(host, port);
            index.add(host);
        }
        return index.size() >= maxProxyNumber;
    }
}
