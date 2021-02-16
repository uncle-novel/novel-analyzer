package com.unclezs.novel.analyzer;

import lombok.Getter;
import lombok.Setter;

/**
 * 解析器全局配置类 单例
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 3:04 下午
 */
public class AnalyzerManager {
    /**
     * 单例实例
     */
    private static final AnalyzerManager ANALYZER_MANAGER = new AnalyzerManager();
    /**
     * 是否启用自动代理,启用自动代理前需要打开这个开关
     */
    @Getter
    @Setter
    private boolean autoProxy = false;

    private AnalyzerManager() {
    }

    /**
     * 获取实例
     *
     * @return ANALYZER_MANAGER
     */
    public static AnalyzerManager me() {
        return ANALYZER_MANAGER;
    }

}
