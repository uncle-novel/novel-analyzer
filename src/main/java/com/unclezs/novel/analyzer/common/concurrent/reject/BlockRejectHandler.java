package com.unclezs.novel.analyzer.common.concurrent.reject;

import lombok.SneakyThrows;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 添加线程之后阻塞住调用添加的线程，直到添加成功
 *
 * @author blog.unclezs.com
 * @since 2020/12/27 7:01 下午
 */
public class BlockRejectHandler implements RejectedExecutionHandler {
    private static final BlockRejectHandler BLOCK_REJECT_HANDLER = new BlockRejectHandler();

    public static BlockRejectHandler me() {
        return BLOCK_REJECT_HANDLER;
    }

    @Override
    @SneakyThrows
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        executor.getQueue().put(r);
    }
}
