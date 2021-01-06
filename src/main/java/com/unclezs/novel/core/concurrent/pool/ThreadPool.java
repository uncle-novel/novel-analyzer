package com.unclezs.novel.core.concurrent.pool;

import lombok.SneakyThrows;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池 增加的无限等待的方法
 *
 * @author blog.unclezs.com
 * @date 2021/1/2 12:03
 */
public class ThreadPool extends ThreadPoolExecutor {


    public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                      BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                      BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 无限等待线程终止，可能会引起一直等待，请在可控情况下调用
     * 自动调用shutdown
     */
    @SneakyThrows
    public void waitCompeted() {
        boolean termination = awaitTermination(1, TimeUnit.MINUTES);
        if (!isShutdown() && !termination) {
            shutdown();
        }
        while (!termination) {
            termination = awaitTermination(1, TimeUnit.DAYS);
        }
    }

    /**
     * 等待任务完成
     *
     * @param taskCount    /
     * @param autoShutdown 自动shutdown
     */
    @SneakyThrows
    public void waitCompeted(int taskCount, boolean autoShutdown) {
        if (!isShutdown() && !isTerminated()) {
            shutdown();
        }
        while (taskCount != getCompletedTaskCount() && !isTerminated()) {
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }
}

