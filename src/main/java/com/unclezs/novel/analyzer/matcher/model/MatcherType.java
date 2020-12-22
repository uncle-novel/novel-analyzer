package com.unclezs.novel.analyzer.matcher.model;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/21 12:05
 */
public enum MatcherType {
    /**
     * CSS选择器
     */
    CSS,
    /**
     * xpath选择器
     */
    XPATH,
    /**
     * jsonpath选择器
     */
    JSON,
    /**
     * 正则选择器
     */
    REGEX;
}
