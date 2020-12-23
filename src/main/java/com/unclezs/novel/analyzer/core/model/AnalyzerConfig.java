package com.unclezs.novel.analyzer.core.model;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:44 下午
 */
public interface AnalyzerConfig {
    String NEXT_PAGE_RULE = "xpath://a[text()~='下一页|下页|下节|下一节']/@href";

    /**
     * 获取baseUrl
     *
     * @return /
     */
    String getBaseUri();
}
