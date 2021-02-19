package com.unclezs.novel.analyzer.core.rule;

import lombok.experimental.UtilityClass;

/**
 * 规则的一些默认值
 *
 * @author blog.unclezs.com
 * @since 2021/02/10 11:11
 */
@UtilityClass
public class RuleConstant {
    /**
     * 下一页规则
     */
    public static final String NEXT_PAGE_RULE = "//a[text()~='.*?下[一]{0,1}[页节].*']/@href";

    /**
     * 搜索页
     */
    public static final String SEARCH_PAGE = "search";
    /**
     * 详情页
     */
    public static final String DETAIL_PAGE = "detail";
    /**
     * 目录页
     */
    public static final String TOC_PAGE = "toc";
}
