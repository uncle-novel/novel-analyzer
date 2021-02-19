package com.unclezs.novel.analyzer.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 简单实现FIFO
 *
 * @author blog.unclezs.com
 * @since 2021/2/15 9:09
 */
public class FifoCache<K, V> implements Cache<K, V> {
    /**
     * 缓存
     */
    private final Map<K, V> cache = new HashMap<>();
    private final List<K> cacheKeys = new ArrayList<>();
    /**
     * 限制大小
     */
    private final int limit;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FifoCache(int limit) {
        this.limit = limit;
    }

    @Override
    public V put(K key, V value) {
        lock.writeLock().lock();
        try {
            while (cache.size() >= limit) {
                // 移除第一个缓存
                K cacheKey = cacheKeys.remove(0);
                cache.remove(cacheKey);
            }
            cacheKeys.add(key);
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
        return value;
    }

    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
}
