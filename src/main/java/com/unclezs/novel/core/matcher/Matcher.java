package com.unclezs.novel.core.matcher;

import com.unclezs.novel.core.matcher.model.MatcherRule;
import com.unclezs.novel.core.utils.StringUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 匹配器  正则,xpath,jsonpath,css
 *
 * @author blog.unclezs.com
 * @since 2020/12/21 11:24
 */
@Slf4j
@UtilityClass
public class Matcher {
    /**
     * 匹配 规则带有选择器标识 css:xxx
     *
     * @param src  源文档
     * @param rule 规则
     * @return /
     */
    public String matching(String src, String rule) {
        MatcherRule matcherRule = MatcherRule.of(rule);
        if (matcherRule.getMatcherType() == null) {
            log.trace("不支持的解析器类型：{}", matcherRule.getMatcherType());
            return StringUtil.EMPTY;
        }
        return matching(matcherRule, src);
    }

    /**
     * 匹配 规则不带选择器标识 css:xxx
     *
     * @param src  源文档
     * @param rule 规则
     * @return /
     */
    public String matching(MatcherRule rule, String src) {
        String ret;
        switch (rule.getMatcherType()) {
            case REGEX:
                // 正则取第一组
                ret = RegexMatcher.matcher(src, rule.getRule());
                break;
            case JSON:
                ret = JsonMatcher.matching(src, rule.getRule());
                break;
            case CSS:
                ret = CssMatcher.matcher(src, rule.getRule());
                break;
            case XPATH:
            default:
                ret = XpathMatcher.matching(src, rule.getRule());
        }
        // 去广告
        ret = StringUtil.remove(ret, rule.getAdvertisement());
        return ret;
    }
}
