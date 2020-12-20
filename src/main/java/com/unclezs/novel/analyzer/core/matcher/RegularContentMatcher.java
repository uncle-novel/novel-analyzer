package com.unclezs.novel.analyzer.core.matcher;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.unclezs.novel.analyzer.contant.RegularConstant.*;

/**
 * 内容匹配器 - 正则 全文匹配
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 8:06 下午
 */
@UtilityClass
public class RegularContentMatcher {
    /**
     * 全文正则
     */
    private final String REGULAR = "[pvri\\-/\"]>([^字<*][\\pP\\w\\pN\\pL\\pM" + CHINESE + UNICODE_LETTER_NUMBER + CHINESE_PUNCTUATION + "]{3,}[^字\\w>]{0,2})(<br|</p|</d|<p|<!|<d|</li)";
    /**
     * 预编译
     */
    private final Pattern PATTERN = Pattern.compile(REGULAR, Pattern.CASE_INSENSITIVE);

    /**
     * 匹配正文
     *
     * @param originalText 源来的
     * @return /
     */
    public String matching(String originalText) {
        StringBuilder content = new StringBuilder();
        Matcher m = PATTERN.matcher(originalText);
        while (m.find()) {
            String paragraph = m.group(1);
            if (!paragraph.isEmpty()) {
                content.append(paragraph).append("\r\n");
            }
        }
        return content.toString();
    }
}
