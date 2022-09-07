package com.unclezs.novel.analyzer.core.matcher.matchers.text;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 内容匹配器 - 正则 严格模式 一段一段进行匹配
 *
 * @author blog.unclezs.com
 * @since 2020/12/20 8:06 下午
 */
public class ParagraphContentMatcher {
  /**
   * 正文正则
   */
  private static final Pattern PATTERN =
    Pattern.compile("([^/][\\s\\S]*?>)([\\s\\S]*?)(<)", Pattern.CASE_INSENSITIVE);
  /**
   * 段落正则
   */
  private static final String PARAGRAPH = "[\\s\\S]*?[^字\\w<*][" + RegexUtils.CHINESE + "]+[\\s\\S]*?";
  /**
   * 段落正则
   */
  private static final String PARAGRAPH_SECONDARY = "[\\s\\S]*?[^字\\w<*][" + RegexUtils.CHINESE + "]+[\\s\\S]*?";
  /**
   * 规则
   */
  private static final String[] EFFECTIVE_TAG = {"br />", "br/>", "br>", "abc\">", "p>", "v>", "->"};

  private ParagraphContentMatcher() {
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
      String paragraph = StringUtils.htmlBlank(matcher.group(2));
      if (StringUtils.isNotBlank(paragraph) && isParagraph(paragraph) && StringUtils.endWith(tag, EFFECTIVE_TAG)) {
        content.append(paragraph).append(StringUtils.LF);
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
