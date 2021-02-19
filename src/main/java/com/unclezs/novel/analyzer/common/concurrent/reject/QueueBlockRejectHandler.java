package com.unclezs.novel.analyzer.common.concurrent.reject;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 队列无限长
 *
 * @author blog.unclezs.com
 * @since 2020/12/27 7:01 下午
 */
public class QueueBlockRejectHandler implements RejectedExecutionHandler {
    private static final QueueBlockRejectHandler QUEUE_BLOCK_REJECT_HANDLER = new QueueBlockRejectHandler();

    public static QueueBlockRejectHandler me() {
        return QUEUE_BLOCK_REJECT_HANDLER;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        executor.getQueue().add(r);
    }
}
