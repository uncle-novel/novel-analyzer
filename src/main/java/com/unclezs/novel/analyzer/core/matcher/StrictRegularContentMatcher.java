package com.unclezs.novel.analyzer.core.matcher;

import com.unclezs.novel.analyzer.utils.StringUtil;
import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.unclezs.novel.analyzer.contant.RegularConstant.*;

/**
 * 内容匹配器 - 正则 严格模式 一段一段进行匹配
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 8:06 下午
 */
@UtilityClass
public class StrictRegularContentMatcher {
    /**
     * 正文正则
     */
    private final Pattern PATTERN = Pattern.compile("([^/][\\s\\S]*?>)([\\s\\S]*?)(<)", Pattern.CASE_INSENSITIVE);
    /**
     * 段落正则
     */
    private final String PARAGRAPH = "[\\s\\S]*?[^字\\w<*][" + CHINESE + "]+[\\s\\S]*?";
    /**
     * 段落正则
     */
    private final String PARAGRAPH_SECONDARY = "[\\s\\S]*?[^字\\w<*][" + CHINESE + "]+[\\s\\S]*?";
    /**
     * 规则
     */
    private final String[] EFFECTIVE_TAG = {"br />", "br/>", "br>", "abc\">", "p>", "v>", "->"};

    /**
     * 匹配正文
     *
     * @param originalText 源字符
     * @return /
     */
    public String matching(String originalText) {
        StringBuilder content = new StringBuilder();
        Matcher matcher = PATTERN.matcher(originalText);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String paragraph = StringUtil.htmlBlank(matcher.group(2));
            boolean valid = StringUtil.isNotBlank(paragraph) && isParagraph(paragraph) && StringUtil.endWith(tag, EFFECTIVE_TAG);
            if (valid) {
                content.append(paragraph).append("\r\n");
            }
        }
        return content.toString();
    }

    /**
     * 是否是段落
     * @param paragraph 段落
     * @return /
     */
    private boolean isParagraph(String paragraph) {
        return paragraph.matches(PARAGRAPH) || paragraph.matches(PARAGRAPH_SECONDARY);
    }
}
