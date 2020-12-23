package com.unclezs.novel.core.matcher.model;

import lombok.Data;

/**
 * 匹配规则
 *
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/21 17:15
 */
@Data
public class MatcherRule {
    public static final String DELIMITER = ":";
    public static final String ADVERTISEMENT_DELIMITER = "##";
    /**
     * 匹配器类型
     */
    private MatcherType matcherType;
    /**
     * 规则
     */
    private String rule;
    /**
     * 广告正则
     */
    private String advertisement;

    /**
     * 规则字符串格式化  eg. css://div/text()##XX广告正则
     *
     * @param rule 规则字符串
     * @return /
     */
    public static MatcherRule of(String rule) {
        String[] r = rule.split(DELIMITER);
        MatcherRule matcherRule = new MatcherRule();
        // 匹配器类型
        matcherRule.setMatcherType(MatcherType.valueOf(r[0].toUpperCase()));
        rule = r.length > 1 ? rule.substring(r[0].length() + 1) : rule;
        r = rule.split(ADVERTISEMENT_DELIMITER);
        // 规则
        matcherRule.setRule(r[0]);
        if (r.length > 1) {
            // 广告正则
            matcherRule.setAdvertisement(r[1]);
        }
        return matcherRule;
    }
}
