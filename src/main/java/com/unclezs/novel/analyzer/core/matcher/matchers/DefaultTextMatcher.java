package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.matcher.matchers.text.DefaultContentMatcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.text.FullTextContentMatcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.text.ParagraphContentMatcher;
import com.unclezs.novel.analyzer.core.rule.CommonRule;

import java.util.Collections;
import java.util.List;

/**
 * 默认的文本匹配器 自动解析正文
 * content: "auto:1"
 * content: "auto:FULL_TEXT"
 * content: {
 * type: "auto",
 * rule: "1"
 * }
 *
 * @author blog.unclezs.com
 * @date 2021/2/4 21:52
 */
public class DefaultTextMatcher extends Matcher {
    /**
     * 全文模式
     */
    public static final String FULL_TEXT = "FULL_TEXT";
    public static final String FULL_TEXT_ORDER = "3";
    /**
     * 段落模式
     */
    public static final String PARAGRAPH = "PARAGRAPH";
    public static final String PARAGRAPH_ORDER = "2";
    /**
     * 单例
     */
    private static final DefaultTextMatcher ME = new DefaultTextMatcher();

    private DefaultTextMatcher() {
    }

    /**
     * 获取单例
     *
     * @return 默认文本匹配器
     */
    public static DefaultTextMatcher me() {
        return ME;
    }

    @Override
    public MatcherAlias[] aliases() {
        return new MatcherAlias[]{MatcherAlias.alias("auto:"), MatcherAlias.alias("auto")};
    }

    /**
     * 获取列表 不支持 如需要使用默认的获取列表规则 直接不写列表规则即可
     *
     * @param src      源
     * @param listRule 规则
     * @param <E>      类型
     * @return 列表
     */
    @Override
    protected <E> List<E> list(String src, CommonRule listRule) {
        return Collections.emptyList();
    }

    /**
     * 自动解析一个
     *
     * @param element 解析目标对象 可能是element,也可能是String
     * @param rule    规则
     * @param <E>     类型 此处只能是String
     * @return 解析结果
     */
    @Override
    protected <E> String one(E element, String rule) {
        // 字符串直接匹配
        String originalText = element.toString();
        switch (rule) {
            case FULL_TEXT:
            case FULL_TEXT_ORDER:
                return FullTextContentMatcher.matching(originalText);
            case PARAGRAPH:
            case PARAGRAPH_ORDER:
                return ParagraphContentMatcher.matching(originalText);
            default:
                return DefaultContentMatcher.matching(originalText);
        }
    }
}
