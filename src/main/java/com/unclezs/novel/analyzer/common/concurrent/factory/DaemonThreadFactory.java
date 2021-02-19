package com.unclezs.novel.analyzer.common.concurrent.factory;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建daemon线程的 线程工厂
 *
 * @author blog.unclezs.com
 * @date 2020/12/27 6:55 下午
 */
public class DaemonThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public DaemonThreadFactory(String name) {
        namePrefix = name + "-thread-";
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
            namePrefix + threadNumber.getAndIncrement(),
            0);
        t.setDaemon(true);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
