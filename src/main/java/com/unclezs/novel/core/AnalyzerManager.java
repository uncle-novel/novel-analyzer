package com.unclezs.novel.core;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解析器配置类 单例
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 3:04 下午
 */
public class AnalyzerManager {
    /**
     * 系统最多允许 同时运行多少个任务
     */
    private static final int SYS_MAX_TASK_COUNT = 6;
    /**
     * 系统最多允许 最多实现多少个线程
     */
    private static final int SYS_MAX_THREAD_NUM = 32;
    /**
     * 配置标志类，只能在调用获取实例之前调用
     */
    private static boolean configurable;
    /**
     * 单例实例
     */
    private static final AnalyzerManager ANALYZER_MANAGER = new AnalyzerManager();
    /**
     * 当前线程数量
     */
    private AtomicInteger currentThreadCount = new AtomicInteger();
    /**
     * 当前任务数量
     */
    private AtomicInteger currentTaskCount = new AtomicInteger();
    /**
     * 最大线程数量
     */
    private int maxThreadNum = 10;
    /**
     * 每个章节下载后延迟
     */
    @Getter
    @Setter
    private int delaySecond = 0;
    /**
     * 最大任务数量
     */
    @Getter
    @Setter
    private int maxTaskCount = 3;
    /**
     * 是否启用自动代理
     */
    @Getter
    @Setter
    private boolean autoProxy = false;

    private AnalyzerManager() {
    }

    public void config(int maxThreadNum, int maxTaskCount) {

    }

    /**
     * 获取实例
     *
     * @return ANALYZER_MANAGER
     */
    public static AnalyzerManager me() {
        return ANALYZER_MANAGER;
    }


    /**
     * 开始任务 添加占用线程
     *
     * @return 任务是否被添加成功
     */
    public static boolean startTask() {
        if (isTaskNotFull()) {
            ANALYZER_MANAGER.currentTaskCount.incrementAndGet();
            return true;
        }
        return false;
    }

    /**
     * 完成任务 移除所占用线程
     */
    public static void finishedTask() {
        ANALYZER_MANAGER.currentTaskCount.decrementAndGet();
    }

    /**
     * 判断是否已经达到最大任务数量
     *
     * @return true 可以添加任务
     */
    public static boolean isTaskNotFull() {
        return ANALYZER_MANAGER.currentTaskCount.get() < SYS_MAX_TASK_COUNT;
    }

    /**
     * 设置是否启动自动获取代理
     *
     * @param enable /
     */
    public static void autoProxy(boolean enable) {
        ANALYZER_MANAGER.autoProxy = enable;

    }

    /**
     * 是否启用自动代理
     *
     * @return true 启用
     */
    public static boolean enableAutoProxy() {
        return ANALYZER_MANAGER.autoProxy;
    }
}
