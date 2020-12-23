package com.unclezs.novel.core;

import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 3:04 下午
 */
@UtilityClass
public class AnalyzerManager {
    private static final int sysMaxTaskCount = 3;
    private static final int sysMaxThreadNum = 32;
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
