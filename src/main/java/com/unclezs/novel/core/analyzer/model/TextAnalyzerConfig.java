package com.unclezs.novel.core.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文本小说解析配置类
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:49 下午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TextAnalyzerConfig implements AnalyzerConfig {
    public static final String NEXT_PAGE_RULE = "xpath://a[text()~='下一页|下页|下节|下一节']/@href";
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
    private String nextPageRule = NEXT_PAGE_RULE;
    /**
     * 正文翻页
     */
    private boolean enableContentNextPage = true;
    /**
     * 章节乱序重排
     */
    private boolean chapterSort;
    /**
     * 下一页规则（存在则会匹配下一页）
     */
    private String nextChapterPageRule = NEXT_PAGE_RULE;
    /**
     * 章节过滤
     */
    @Builder.Default
    private boolean chapterFilter = true;
    /**
     * 章节翻页
     */
    private boolean enableChapterNextPage = true;

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
    public static TextAnalyzerConfigBuilder defaultBuilder() {
        return builder().rule(Rule.TEXT_TAG).enableChapterNextPage(true).enableContentNextPage(true).chapterFilter(
            true).nextPageRule(NEXT_PAGE_RULE).baseUri(StringUtil.EMPTY);
    }
}
