package com.unclezs.novel.analyzer.utils.regex;

import com.unclezs.novel.analyzer.utils.CollectionUtil;
import com.unclezs.novel.analyzer.utils.StringUtil;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 7:24 下午
 */
@UtilityClass
public class ReUtil {
  /**
   * 数字
   */
  public final Pattern NUMBERS = Pattern.compile("\\d+");
  /**
   * 中文
   */
  public final String CHINESE = "\\u4E00-\\u9FFF";
  /**
   * unicode符号
   */
  public final String UNICODE_LETTER_NUMBER = "\\uFF41-\\uFF5a\\uFF21-\\uFF3a\\uFF10-\\uFF19";
  /**
   * 中文标点符号
   */
  public final String CHINESE_PUNCTUATION = "~\\u000A\\u0009\\u00A0\\u0020\\u3000\\uFEFF";
  /**
   * 字母
   */
  public final Pattern WORD = Pattern.compile("[a-zA-Z]+");
  /**
   * 正则中需要被转义的关键字
   */
  public final Set<Character> RE_KEYS =
      CollectionUtil.newSet('$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|');

  public boolean isNumber(String src) {
    return NUMBERS.matcher(src).matches();
  }

  /**
   * 是否为单词
   *
   * @param src 源
   * @return /
   */
  public boolean isWord(String src) {
    return WORD.matcher(src).matches();
  }

  /**
   * 获得匹配的字符串
   *
   * @param regex      匹配的正则
   * @param content    被匹配的内容
   * @param groupIndex 匹配正则的分组序号
   * @return 匹配后得到的字符串，未匹配返回null
   */
  public static String get(String regex, CharSequence content, int groupIndex) {
    if (null == content || null == regex) {
      return null;
    }
    final Pattern pattern = PatternPool.get(regex, Pattern.CASE_INSENSITIVE);
    return get(pattern, content, groupIndex);
  }

  /**
   * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
   *
   * @param pattern    编译后的正则模式
   * @param content    被匹配的内容
   * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
   * @return 匹配后得到的字符串，未匹配返回null
   */
  public static String get(Pattern pattern, CharSequence content, int groupIndex) {
    if (null == content || null == pattern) {
      return null;
    }

    final Matcher matcher = pattern.matcher(content);
    if (matcher.find()) {
      return matcher.group(groupIndex);
    }
    return null;
  }

  /**
   * 获得匹配的字符串匹配到的所有分组
   *
   * @param pattern    编译后的正则模式
   * @param content    被匹配的内容
   * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
   * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
   * @since 4.0.13
   */
  public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
    if (null == content || null == pattern) {
      return null;
    }

    ArrayList<String> result = new ArrayList<>();
    final Matcher matcher = pattern.matcher(content);
    if (matcher.find()) {
      final int startGroup = withGroup0 ? 0 : 1;
      final int groupCount = matcher.groupCount();
      for (int i = startGroup; i <= groupCount; i++) {
        result.add(matcher.group(i));
      }
    }
    return result;
  }

  /**
   * 给定内容是否匹配正则
   *
   * @param regex   正则
   * @param content 内容
   * @return 正则为null或者""则不检查，返回true，内容为null返回false
   */
  public static boolean isMatch(String regex, CharSequence content) {
    if (content == null) {
      // 提供null的字符串为不匹配
      return false;
    }

    if (StringUtil.isEmpty(regex)) {
      // 正则不存在则为全匹配
      return true;
    }

    final Pattern pattern = PatternPool.get(regex, Pattern.CASE_INSENSITIVE);
    return isMatch(pattern, content);
  }

  /**
   * 给定内容是否匹配正则
   *
   * @param pattern 模式
   * @param content 内容
   * @return 正则为null或者""则不检查，返回true，内容为null返回false
   */
  public static boolean isMatch(Pattern pattern, CharSequence content) {
    if (content == null || pattern == null) {
      // 提供null的字符串为不匹配
      return false;
    }
    return pattern.matcher(content).matches();
  }

  /**
   * 转义字符，将正则的关键字转义
   *
   * @param c 字符
   * @return 转义后的文本
   */
  public static String escape(char c) {
    final StringBuilder builder = new StringBuilder();
    if (RE_KEYS.contains(c)) {
      builder.append('\\');
    }
    builder.append(c);
    return builder.toString();
  }

  /**
   * 转义字符串，将正则的关键字转义
   *
   * @param content 文本
   * @return 转义后的文本
   */
  public static String escape(CharSequence content) {
    if (StringUtil.isBlank(content)) {
      return StringUtil.str(content);
    }

    final StringBuilder builder = new StringBuilder();
    int len = content.length();
    char current;
    for (int i = 0; i < len; i++) {
      current = content.charAt(i);
      if (RE_KEYS.contains(current)) {
        builder.append('\\');
      }
      builder.append(current);
    }
    return builder.toString();
  }


}
