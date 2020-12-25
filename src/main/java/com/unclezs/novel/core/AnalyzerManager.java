package com.unclezs.novel.core;

import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 3:04 下午
 */
@UtilityClass
public class AnalyzerManager {
    /**
     * 系统最多允许 同时运行多少个任务
     */
    private static final int SYS_MAX_TASK_COUNT = 3;
    /**
     * 系统最多允许 最多实现多少个线程
     */
    private static final int SYS_MAX_THREAD_NUM = 32;
    /**
     * 最大线程数量
     */
    public static int threadNum = 1;
    /**
     * 每个章节下载后延迟
     */
    public static int delaySecond = 0;
    /**
     * 最大任务数量
     */
    public static int maxTaskCount;
}
