package com.unclezs.novel.core.analyzer.text.matcher;

import com.unclezs.novel.core.utils.StringUtil;
import com.unclezs.novel.core.utils.regex.RegexUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 内容匹配器 - 正则 严格模式 一段一段进行匹配
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 8:06 下午
 */
public class StrictRegexContentMatcher {
    /**
     * 正文正则
     */
    private static final Pattern PATTERN =
        Pattern.compile("([^/][\\s\\S]*?>)([\\s\\S]*?)(<)", Pattern.CASE_INSENSITIVE);
    /**
     * 段落正则
     */
    private static final String PARAGRAPH = "[\\s\\S]*?[^字\\w<*][" + RegexUtil.CHINESE + "]+[\\s\\S]*?";
    /**
     * 段落正则
     */
    private static final String PARAGRAPH_SECONDARY = "[\\s\\S]*?[^字\\w<*][" + RegexUtil.CHINESE + "]+[\\s\\S]*?";
    /**
     * 规则
     */
    private static final String[] EFFECTIVE_TAG = {"br />", "br/>", "br>", "abc\">", "p>", "v>", "->"};

    private StrictRegexContentMatcher() {
    }

    /**
     * 匹配正文
     *
     * @param originalText 源字符
     * @return /
     */
    public static String matching(String originalText) {
        StringBuilder content = new StringBuilder();
        Matcher matcher = PATTERN.matcher(originalText);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String paragraph = StringUtil.htmlBlank(matcher.group(2));
            boolean valid =
                StringUtil.isNotBlank(paragraph) && isParagraph(paragraph) && StringUtil.endWith(tag, EFFECTIVE_TAG);
            if (valid) {
                content.append(paragraph).append("StringUtil.NEW_LINE");
            }
        }
        return content.toString();
    }

    /**
     * 是否是段落
     *
     * @param paragraph 段落
     * @return /
     */
    private static boolean isParagraph(String paragraph) {
        return paragraph.matches(PARAGRAPH) || paragraph.matches(PARAGRAPH_SECONDARY);
    }
}
