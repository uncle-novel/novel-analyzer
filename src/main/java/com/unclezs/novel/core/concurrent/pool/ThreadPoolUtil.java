package com.unclezs.novel.core.concurrent.pool;

import com.unclezs.novel.core.concurrent.AnalyzerThreadFactory;
import com.unclezs.novel.core.concurrent.reject.BlockRejectHandler;
import lombok.experimental.UtilityClass;

import java.util.concurrent.*;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 4:50 下午
 */
@UtilityClass
public class ThreadPoolUtil {
    /**
     * 创建一个单线程池，同newFixedThreadPoolExecutor
     *
     * @param name 线程池名称
     * @return /
     */
    public static ExecutorService newSingleThreadExecutor(String name) {
        return new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new AnalyzerThreadFactory(name));
    }

    /**
     * 创建一个队列长度无限的，任务可以随便添加不会阻塞，线程数固定
     *
     * @param name      线程池名称
     * @param threadNum 线程数量
     * @return /
     */
    public static ExecutorService newFixedThreadPoolExecutor(int threadNum, String name) {
        return new ThreadPoolExecutor(threadNum, threadNum,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new AnalyzerThreadFactory(name));
    }

    /**
     * 创建一个线程池 当队列满之后，则阻塞调用线程直到队列为空
     *
     * @param name      线程池名称
     * @param threadNum 线程数量
     * @return /
     */
    public static ExecutorService newBlockCallThreadPool(int threadNum, String name) {
        return new ThreadPoolExecutor(threadNum, threadNum,
            5L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new AnalyzerThreadFactory(name), BlockRejectHandler.me());
    }
}
