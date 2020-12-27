package com.unclezs.novel.core.concurrent.pool;

import com.unclezs.novel.core.concurrent.AnalyzerThreadFactory;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 4:50 下午
 */
@UtilityClass
public class ThreadPoolUtil {
    /**
     * 创建一个单线程池
     *
     * @return
     */
    public static ExecutorService newSingleThreadExecutor(String name) {
        return new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new AnalyzerThreadFactory(name));
    }

    /**
     * 创建一个队列无限长的队列
     *
     * @return /
     */
    public static ExecutorService newFixedThreadPoolExecutor(int threadNum, String name) {
        return new ThreadPoolExecutor(threadNum, threadNum,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new AnalyzerThreadFactory(name));
    }
}
