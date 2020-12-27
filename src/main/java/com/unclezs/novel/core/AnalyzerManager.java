package com.unclezs.novel.core;

import lombok.Data;

/**
 * 解析器配置类 单例
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 3:04 下午
 */
@Data
public class AnalyzerManager {
    private static final AnalyzerManager ANALYZER_MANAGER = new AnalyzerManager();

    private AnalyzerManager() {
    }

    public static AnalyzerManager me() {
        return ANALYZER_MANAGER;
    }

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
    private int threadNum = 1;
    /**
     * 每个章节下载后延迟
     */
    private int delaySecond = 0;
    /**
     * 最大任务数量
     */
    private int maxTaskCount;
    /**
     * 是否启用自动代理
     */
    private boolean autoProxy;


}
