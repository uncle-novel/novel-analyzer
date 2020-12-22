package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.core.model.AnalyzerConfig;
import com.unclezs.novel.analyzer.spider.model.Chapter;

import java.util.List;

/**
 * 小说解析器
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:42 下午
 */
public abstract class NovelAnalyzer {
    /**
     * 获取小说正文
     *
     * @param content        html/json
     * @param analyzerConfig 解析配置
     * @return /
     */
    abstract String content(String content, AnalyzerConfig analyzerConfig);

    /**
     * 获取小说章节列表
     *
     * @param content        html/json
     * @param analyzerConfig 解析配置
     * @return /
     */
    abstract List<Chapter> chapters(String content, AnalyzerConfig analyzerConfig);
}
