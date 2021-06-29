package com.unclezs.novel.analyzer.util;

import com.unclezs.novel.analyzer.util.regex.PatternPool;
import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:58 下午
 */
@UtilityClass
public class StringUtils {
  public static final int INDEX_NOT_FOUND = -1;
  /**
   * 字符串常量：空字符串 {@code StringUtil.EMPTY}
   */
  public static final String EMPTY = "";
  /**
   * 空格
   */
  public static final String BLANK = " ";
  /**
   * 回车换行
   */
  public static final String NEW_LINE = "\r\n";
  /**
   * 换行符
   */
  public static final String LF = "\n";
  /**
   * 斜杠
   */
  public static final String SLASH = "/";
  /**
   * 反斜杠
   */
  public static final String BACKSLASH = "/";
  public static final String NULL = "null";
  public static final String QUOT = "\"";
  public static final String COLON = ":";
  /**
   * HTML的空白标签
   */
  private static final String HTML_BLANK_REG = "&[#\\w]{3,6}[;:]?";
  private final Pattern ncrReg = Pattern.compile("&#([\\d]{2,6});");

  /**
   * 是否为空字符串
   *
   * @param str /
   * @return /
   */
  public boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }

  /**
   * 非空字符串
   *
   * @param str /
   * @return /
   */
  public boolean isNotEmpty(CharSequence str) {
    return !isEmpty(str);
  }

  /**
   * 是否为空白字符串
   *
   * @param str /
   * @return /
   */
  public boolean isBlank(CharSequence str) {
    int length;
    if (str != null && (length = str.length()) != 0) {
      for (int i = 0; i < length; ++i) {
        if (!isBlankChar(str.charAt(i))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 移除双引号 “xx” -》 xx
   *
   * @param str /
   * @return /
   */
  public String removeQuote(String str) {
    if (str.startsWith(StringUtils.QUOT) && str.endsWith(StringUtils.QUOT)) {
      str = str.substring(1, str.length() - 1);
    }
    return str;
  }

  /**
   * 是否以字符串开始
   *
   * @param str    目标字符串
   * @param prefix 前缀字符串
   * @return /
   */
  public boolean startWith(String str, String... prefix) {
    for (String pre : prefix) {
      if (!str.startsWith(pre)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 是否为空白字符
   *
   * @param c 字符
   * @return /
   */
  public boolean isBlankChar(int c) {
    return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234;
  }

  public boolean isNotBlank(CharSequence str) {
    return !isBlank(str);
  }

  /**
   * 去除头尾
   *
   * @param header 头
   * @param tail   尾
   * @param src    源文本
   * @return 删减后的文本
   */
  public String removeHeaderAndTail(String header, String tail, String src) {
    if (StringUtils.isNotEmpty(header) && src.contains(header)) {
      src = src.substring(header.length());
    }
    if (StringUtils.isNotEmpty(tail) && src.contains(tail)) {
      src = src.substring(0, src.indexOf(tail));
    }
    return src;
  }


  /**
   * 清楚字符串里面的指定元素
   *
   * @param src    源字符串
   * @param target 目录字符串数组
   * @return 清除后的
   */
  public String remove(String src, String... target) {
    if (StringUtils.isBlank(src)) {
      return src;
    }
    for (String s : target) {
      if (s != null) {
        src = PatternPool.get(s, Pattern.DOTALL).matcher(src).replaceAll(StringUtils.EMPTY);
      }
    }
    return trim(src);
  }

  /**
   * 格式化字符串 log一样的格式 {x}
   *
   * @param messagePattern 格式
   * @param params         参数
   * @return 格式化结果
   */
  public String format(String messagePattern, Object... params) {
    return MessageFormatter.arrayFormat(messagePattern, params).getMessage();
  }

  /**
   * 移除文件名称的非法字符
   *
   * @param str 文件名称
   * @return /
   */
  public String removeInvalidSymbol(String str) {
    return str.replaceAll("[\\\\/:*?\"<>|]", StringUtils.EMPTY);
  }

  public String trim(String text) {
    int len = text.length();
    int st = 0;
    char[] val = text.toCharArray();
    char p;
    while ((st < len) && ((p = val[st]) <= ' ' || p == 160 || p == 12288)) {
      st++;
    }
    while ((st < len) && ((p = val[len - 1]) <= ' ' || p == 160 || p == 12288)) {
      len--;
    }
    return ((st > 0) || (len < text.length())) ? text.substring(st, len) : text;
  }

  /**
   * 将{@code &#}类得字符转化为汉字
   *
   * @param src 字符集 {@code &#20491;&#30007;&#20154;&#30475;}
   * @return 转码后得字符集
   */
  public String ncr2Chinese(String src) {
    // 换行符处理
    src = src.replace(StringUtils.LF, "&#92;&#114;&#92;&#110;");
    Matcher m = ncrReg.matcher(src);
    while (m.find()) {
      src = src.replace(m.group(0), (char) Integer.parseInt(m.group(1)) + StringUtils.EMPTY);
    }
    return src.replace("\\r\\n", StringUtils.LF);
  }

  /**
   * 转换html的 &nbsp; 为空格
   *
   * @param html html
   * @return /
   */
  public String htmlBlank(String html) {
    return replaceHtmlBlank(html, StringUtils.BLANK);
  }

  /**
   * 转换html的 &nbsp; 为空格
   *
   * @param html html
   * @return /
   */
  public String replaceHtmlBlank(String html, String target) {
    return html.replaceAll(HTML_BLANK_REG, target);
  }

  /**
   * 以target结果
   *
   * @param src    /
   * @param suffix /
   * @return /
   */
  public boolean endWith(String src, String... suffix) {
    for (String end : suffix) {
      if (src.endsWith(end)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 分段，然后每段缩进四个空格
   *
   * @param originalText 原文本
   * @return 缩进后的
   */
  public String indentation(String originalText) {
    String[] paragraph = originalText.split("[\r]?\n");
    StringBuilder paragraphs = new StringBuilder();
    for (String line : paragraph) {
      if (isNotBlank(line)) {
        paragraphs.append("    ").append(StringUtils.trim(line)).append(StringUtils.LF).append(StringUtils.LF);
      }
    }
    return paragraphs.toString();
  }

  /**
   * {@link CharSequence} 转为字符串，null安全
   *
   * @param cs {@link CharSequence}
   * @return 字符串
   */
  public static String str(CharSequence cs) {
    return null == cs ? null : cs.toString();
  }

  /**
   * 正则替换全部
   *
   * @param str         源
   * @param regexes     正则
   * @param replacement 文本
   * @return /
   */
  public String replaceAllCaseInsensitive(String str, String replacement, String... regexes) {
    String ret = str;
    for (String regex : regexes) {
      ret = PatternPool.get(regex, Pattern.CASE_INSENSITIVE).matcher(ret).replaceAll(replacement);
    }
    return ret;
  }

  /**
   * 替换全部
   *
   * @param str         源
   * @param replacement 文本
   * @return /
   */
  public String replace(String src, String replacement, String... str) {
    String ret = src;
    for (String s : str) {
      ret = ret.replace(s, replacement);
    }
    return ret;
  }

  /**
   * 切割指定位置之前部分的字符串
   *
   * @param string         字符串
   * @param toIndexExclude 切割到的位置（不包括）
   * @return 切割后的剩余的前半部分字符串
   */
  public static String subPre(CharSequence string, int toIndexExclude) {
    return sub(string, 0, toIndexExclude);
  }

  /**
   * 切割指定位置之后部分的字符串
   *
   * @param string    字符串
   * @param fromIndex 切割开始的位置（包括）
   * @return 切割后后剩余的后半部分字符串
   */
  public static String subSuf(CharSequence string, int fromIndex) {
    if (isEmpty(string)) {
      return null;
    }
    return sub(string, fromIndex, string.length());
  }

  /**
   * 改进JDK subString<br>
   * index从0开始计算，最后一个字符为-1<br>
   * 如果from和to位置一样，返回 StringUtil.EMPTY <br>
   * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
   * 如果经过修正的index中from大于to，则互换from和to example: <br>
   * abcdefgh 2 3 =》 c <br>
   * abcdefgh 2 -3 =》 cde <br>
   *
   * @param str              String
   * @param fromIndexInclude 开始的index（包括）
   * @param toIndexExclude   结束的index（不包括）
   * @return 字串
   */
  public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
    if (isEmpty(str)) {
      return str(str);
    }
    int len = str.length();

    if (fromIndexInclude < 0) {
      fromIndexInclude = len + fromIndexInclude;
      if (fromIndexInclude < 0) {
        fromIndexInclude = 0;
      }
    } else if (fromIndexInclude > len) {
      fromIndexInclude = len;
    }

    if (toIndexExclude < 0) {
      toIndexExclude = len + toIndexExclude;
      if (toIndexExclude < 0) {
        toIndexExclude = len;
      }
    } else if (toIndexExclude > len) {
      toIndexExclude = len;
    }

    if (toIndexExclude < fromIndexInclude) {
      int tmp = fromIndexInclude;
      fromIndexInclude = toIndexExclude;
      toIndexExclude = tmp;
    }

    if (fromIndexInclude == toIndexExclude) {
      return EMPTY;
    }

    return str.toString().substring(fromIndexInclude, toIndexExclude);
  }

  /**
   * 指定范围内查找指定字符
   *
   * @param str        字符串
   * @param searchChar 被查找的字符
   * @return 位置
   */
  public static int indexOf(final CharSequence str, char searchChar) {
    return indexOf(str, searchChar, 0);
  }

  /**
   * 指定范围内查找指定字符
   *
   * @param str        字符串
   * @param searchChar 被查找的字符
   * @param start      起始位置，如果小于0，从0开始查找
   * @return 位置
   */
  public static int indexOf(CharSequence str, char searchChar, int start) {
    if (str instanceof String) {
      return ((String) str).indexOf(searchChar, start);
    } else {
      return indexOf(str, searchChar, start, -1);
    }
  }

  /**
   * 指定范围内查找指定字符
   *
   * @param str        字符串
   * @param searchChar 被查找的字符
   * @param start      起始位置，如果小于0，从0开始查找
   * @param end        终止位置，如果超过str.length()则默认查找到字符串末尾
   * @return 位置
   */
  public static int indexOf(final CharSequence str, char searchChar, int start, int end) {
    if (isEmpty(str)) {
      return INDEX_NOT_FOUND;
    }
    final int len = str.length();
    if (start < 0 || start > len) {
      start = 0;
    }
    if (end > len || end < 0) {
      end = len;
    }
    for (int i = start; i < end; i++) {
      if (str.charAt(i) == searchChar) {
        return i;
      }
    }
    return INDEX_NOT_FOUND;
  }

  /**
   * 当给定字符串为null时，转换为Empty
   *
   * @param str 被转换的字符串
   * @return 转换后的字符串
   */
  public static String nullToEmpty(CharSequence str) {
    return nullToDefault(str, EMPTY);
  }

  /**
   * 如果字符串是 <code>null</code>，则返回指定默认字符串，否则返回字符串本身。
   *
   * <pre>
   * nullToDefault(null, &quot;default&quot;)  = &quot;default&quot;
   * nullToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
   * nullToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
   * nullToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
   * </pre>
   *
   * @param str        要转换的字符串
   * @param defaultStr 默认字符串
   * @return 字符串本身或指定的默认字符串
   */
  public static String nullToDefault(CharSequence str, String defaultStr) {
    return (str == null) ? defaultStr : str.toString();
  }

  /**
   * 对象为空则转换为null
   *
   * @param obj 对象
   * @return 否则调用toString
   */
  public static String toString(Object obj) {
    if (obj == null) {
      return null;
    }
    return obj.toString();
  }

  /**
   * 对象为空则转换为null
   *
   * @param obj 对象
   * @return 否则调用toString
   */
  public static String toStringNullToEmpty(Object obj) {
    if (obj == null) {
      return StringUtils.EMPTY;
    }
    return obj.toString();
  }

}
