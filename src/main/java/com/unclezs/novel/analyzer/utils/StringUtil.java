package com.unclezs.novel.analyzer.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:58 下午
 */
@UtilityClass
public class StringUtil {
  private static final String HTML_BLANK_REG = "&[#\\w]{3,6}[;:]?";

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

  private Pattern ncrReg = Pattern.compile("&#([\\d]{2,6});");

  /**
   * 去除头尾
   *
   * @param header 头
   * @param tail   尾
   * @param src    源文本
   * @return 删减后的文本
   */
  public String removeHeaderAndTail(String header, String tail, String src) {
    if (StringUtil.isNotEmpty(header) && src.contains(header)) {
      src = src.substring(header.length());
    }
    if (StringUtil.isNotEmpty(tail) && src.contains(tail)) {
      src = src.substring(0, src.indexOf(tail));
    }
    return src;
  }


  /**
   * 移除文本中的文本(去除空格)
   *
   * @param src    源文本
   * @param target 要移除的文本
   * @return /
   */
  public String removeText(String src, String target) {
    return src.replace(remove(target, " "), "");
  }

  /**
   * 移除文本中的标题
   *
   * @param src    源文本
   * @param target 要移除的文本
   * @return /
   */
  public String removeTitle(String src, String target) {
    if (!target.startsWith("第")) {
      return src;
    }
    String[] lines = src.split("\n");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      if (StringUtil.isNotBlank(lines[i])) {
        if (i < 3) {
          sb.append(lines[i].replace(remove(target, " "), "")).append("\r\n");
        } else {
          sb.append(lines[i]).append("\r\n");
        }
      }
    }
    return sb.toString();
  }

  /**
   * 清楚字符串里面的指定元素
   *
   * @param src    源字符串
   * @param target 目录字符串数组
   * @return 清除后的
   */
  public String remove(String src, String... target) {
    if (StringUtil.isBlank(src)) {
      return src;
    }
    for (String s : target) {
      if (s != null) {
        src = src.replaceAll(s, "");
      }
    }
    return trim(src);
  }

  /**
   * 清楚字符串里面的指定元素
   *
   * @param src    源字符串
   * @param target 目录字符串数组
   * @return 清除后的
   */
  public String removePlain(String src, String... target) {
    if (StringUtil.isBlank(src)) {
      return src;
    }
    for (String s : target) {
      if (s != null) {
        src = src.replace(s, "");
      }
    }
    return trim(src);
  }

  /**
   * 移除文件名称的非法字符
   *
   * @param str 文件名称
   * @return /
   */
  public String removeInvalidSymbol(String str) {
    return str.replaceAll("[\\\\/:*?\"<>|]", "");
  }

  /**
   * 获取添加范围后的html
   *
   * @param header 范围头
   * @param tail   范围尾部
   * @param src    源
   * @return 删减后的html
   */
  public String getRange(String header, String tail, String src) {
    int end = tail != null && tail.length() > 1 ? src.indexOf(tail) : src.length();
    int st = header != null && header.length() > 1 ? src.indexOf(header) : 0;
    if (st == -1) {
      st = 0;
    }
    if (end == -1) {
      end = src.length();
    }
    if (st != 0) {
      st -= 5;
    }
    if (end != src.length()) {
      end += 5;
    }
    return src.substring(st, end);
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
   * 将&#类得字符转化为汉字
   *
   * @param src 字符集&#20491;&#30007;&#20154;&#30475;
   * @return 转码后得字符集
   */
  public String ncr2Chinese(String src) {
    // 换行符处理
    src = src.replace("\r\n", "&#92;&#114;&#92;&#110;");
    Matcher m = ncrReg.matcher(src);
    while (m.find()) {
      src = src.replace(m.group(0), (char) Integer.parseInt(m.group(1)) + "");
    }
    return src.replace("\\r\\n", "\r\n");
  }

  /**
   * 转换html的 &nbsp; 为空格
   *
   * @param html html
   * @return /
   */
  public String htmlBlank(String html) {
    return replaceHtmlBlank(html, " ");
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
        paragraphs.append("    ").append(line.trim()).append("\r\n\r\n");
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
}
