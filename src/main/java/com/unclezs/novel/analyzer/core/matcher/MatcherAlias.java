package com.unclezs.novel.analyzer.core.matcher;

import com.unclezs.novel.analyzer.common.cache.WeakCache;
import lombok.Data;

/**
 * 匹配器别名
 * 如果是默认前缀则 规则不会移除这个别名
 * 如：
 * xpath://abc/xx   xpath是非默认 则被识别为 //abc/xx
 * //abc/xx         // 是默认别名， 则被识别为 //abc/xx
 * xpath://abc/xx   xpath如果是默认 则被识别为 xpath://abc/xx
 *
 * @author blog.unclezs.com
 * @date 2021/01/28 15:34
 * @see com.unclezs.novel.analyzer.core.helper.RuleHelper#parseRuleType(java.lang.String)
 */
@Data
public class MatcherAlias {
    private static WeakCache<String, MatcherAlias> aliasCaches = new WeakCache<>();
    /**
     * 别名名称
     */
    private String name;
    /**
     * 是否为语法默认前缀
     */
    private boolean isDefault;

    private MatcherAlias(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }

    /**
     * 默认的匹配器别名
     *
     * @param name 别名
     * @return 默认的匹配器别名
     */
    public static MatcherAlias defaultAlias(String name) {
        return create(name, true);
    }

    /**
     * 默认的匹配器别名
     *
     * @param name 别名
     * @return 默认的匹配器别名
     */
    public static MatcherAlias alias(String name) {
        return create(name, false);
    }

    /**
     * 默认的匹配器别名
     *
     * @param name 别名
     * @return 默认的匹配器别名
     */
    public static MatcherAlias create(String name, boolean isDefault) {
        MatcherAlias alias = aliasCaches.get(name);
        if (alias != null) {
            return alias;
        }
        alias = new MatcherAlias(name, isDefault);
        aliasCaches.put(name, alias);
        return alias;
    }
}
