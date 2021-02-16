package com.unclezs.novel.analyzer.core.matcher.matchers.text;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 内容匹配器 - 正则 全文匹配
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 8:06 下午
 */
public class FullTextContentMatcher {
    /**
     * 全文正则
     */
    private static final String
        REGEX = "[pvri\\-/\"]>([^字<*][\\pP\\w\\pN\\pL\\pM" + RegexUtils.CHINESE + RegexUtils.UNICODE_LETTER_NUMBER
        + RegexUtils.CHINESE_PUNCTUATION + "]{3,}[^字\\w>]{0,2})(<br|</p|</d|<p|<!|<d|</li)";
    /**
     * 预编译
     */
    private static final Pattern CORE_PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private FullTextContentMatcher() {
    }

    /**
     * 匹配正文
     *
     * @param originalText 源文本
     * @return /
     */
    public static String matching(String originalText) {
        StringBuilder content = new StringBuilder();
        Matcher m = CORE_PATTERN.matcher(originalText);
        while (m.find()) {
            String paragraph = m.group(1);
            if (!paragraph.isEmpty()) {
                content.append(paragraph).append(StringUtils.NEW_LINE);
            }
        }
        return content.toString();
    }
}
