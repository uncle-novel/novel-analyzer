package com.unclezs.novel.analyzer.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:49 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextAnalyzerConfig {
    /**
     * BaseUrI
     */
    private String baseUri;
    /**
     * 是否NRC转中文
     */
    private boolean ncr;
    /**
     * 广告字符串列表 可以是正则
     */
    private List<String> advertisements;
    /**
     * 范围头
     */
    private String rangeHeader;
    /**
     * 范围尾
     */
    private String rangeTail;
    /**
     * 正文规则
     */
    @Builder.Default
    private Rule rule = Rule.TEXT_TAG;

    /**
     * 下一页规则（存在则会匹配下一页）
     */
    private String nextPageRule;

    /**
     * 章节乱序重排
     */
    private boolean chapterSort;
    /**
     * 章节过滤
     */
    @Builder.Default
    private boolean chapterFilter = true;

    /**
     * 默认配置
     *
     * @return TextAnalyzerConfig
     */
    public static TextAnalyzerConfig defaultConfig() {
        return defaultBuilder().build();
    }

    /**
     * 默认配置
     *
     * @return TextAnalyzerConfig
     */
    public static TextAnalyzerConfig.TextAnalyzerConfigBuilder defaultBuilder() {
        return builder().rule(Rule.TEXT_TAG).chapterFilter(true).baseUri("");
    }
}
