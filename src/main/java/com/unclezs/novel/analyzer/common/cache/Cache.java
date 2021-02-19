package com.unclezs.novel.analyzer.common.cache;

/**
 * 缓存
 *
 * @author blog.unclezs.com
 * @since 2021/2/15 8:55
 */
public interface Cache<K, V> {
    /**
     * 添加缓存
     *
     * @param key   健
     * @param value 值
     * @return 返回缓存
     */
    V put(K key, V value);

    /**
     * 读取缓存
     *
     * @param key 值
     * @return 返回缓存
     */
    V get(K key);
}
