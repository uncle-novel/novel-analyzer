package com.unclezs.novel.core.concurrent.reject;

import lombok.SneakyThrows;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 添加线程之后阻塞住添加的线程，直到添加成功
 *
 * @author blog.unclezs.com
 * @date 2020/12/27 7:01 下午
 */
public class QueueBlockRejectHandler implements RejectedExecutionHandler {
    private static final QueueBlockRejectHandler QUEUE_BLOCK_REJECT_HANDLER = new QueueBlockRejectHandler();

    public static QueueBlockRejectHandler me() {
        return QUEUE_BLOCK_REJECT_HANDLER;
    }

    @Override
    @SneakyThrows
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        executor.getQueue().put(r);
    }
}
