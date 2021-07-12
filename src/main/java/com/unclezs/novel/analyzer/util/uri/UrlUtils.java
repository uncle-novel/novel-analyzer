package com.unclezs.novel.analyzer.util.uri;

import com.unclezs.novel.analyzer.common.exception.UtilException;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * URL工具类
 *
 * @author uncle
 * @date 2020/3/25 21:11
 */
@UtilityClass
public class UrlUtils {

  /**
   * 获取重定向地址
   *
   * @param path /
   * @return /
   */
  public static String getRedirectUrl(String path) {
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(path)
        .openConnection();
      conn.setInstanceFollowRedirects(false);
      conn.setConnectTimeout(5000);
      return conn.getHeaderField("Location");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return path;
  }

  /**
   * 获取URL的域名
   * <p>
   * https://www.unclezs.com/xx/xx?q=x  得到  www.unclezs.com
   *
   * @param url /
   * @return 域名
   */
  public static String getHost(String url) {
    return UrlUtils.isHttpUrl(url) ? RegexUtils.get("http[s]{0,1}://(.+?)/", url + "/", 1) : url;
  }

  /**
   * 获取URL的不带Path的
   * <p>
   * https://www.unclezs.com/xx/xx?q=x  得到  https://www.unclezs.com/
   *
   * @param url /
   * @return 域名
   */
  public static String getSite(String url) {
    if (UrlUtils.isHttpUrl(url)) {
      String site = RegexUtils.get("(http[s]{0,1}://.+?)/", url + "/", 1);
      if (StringUtils.isNotBlank(site)) {
        return site.concat(StringUtils.BACKSLASH);
      }
    }
    return url;
  }


  /**
   * 获取host
   *
   * @param url /   https://www.unclezs.com  得到 unclezs
   * @return /
   */
  public static String getDomain(String url) {
    String host = RegexUtils.get("http[s]{0,1}://(.+?)/", url + "/", 1);
    String[] str = host.split("\\.");
    if (str.length == 3) {
      return str[1];
    } else {
      return str[0];
    }
  }

  /**
   * 获取不带参数的URL
   * https://www.unclezs.com?ab=1&amp;c=2  得到 https://www.unclezs.com
   *
   * @param url 带参URL
   * @return 不带参URL
   */
  public static String getUrlNoParams(String url) {
    int index = url.indexOf("?");
    if (index < 0) {
      return url;
    }
    return url.substring(0, index);
  }

  /**
   * 获取URL参数
   * https://www.unclezs.com?ab=1&amp;c=2  得到 ab=1&amp;c=2
   *
   * @param url 带参URL
   * @return URL参数
   */
  public static String getUrlParams(String url) {
    int index = url.indexOf("?");
    if (index < 0) {
      return StringUtils.EMPTY;
    }
    return url.substring(index + 1);
  }

  /**
   * 取出URL中最后一段
   * https://unclezs.com/abc.html  则得到 abc
   *
   * @param url /
   * @return /
   */
  public static String getUrlLastPathNotSuffix(String url) {
    String str = url.replaceAll("\\.htm.*", StringUtils.EMPTY);
    if (str.endsWith(StringUtils.BACKSLASH)) {
      str = str.substring(0, str.length() - 1);
    }
    int i = str.lastIndexOf(StringUtils.BACKSLASH);
    return str.substring(i + 1);
  }


  /**
   * 是否为http链接
   *
   * @param url /
   * @return /
   */
  public static boolean isHttpUrl(String url) {
    return StringUtils.isNotEmpty(url) && url.length() > 5 && "http".equalsIgnoreCase(url.substring(0, 4));
  }

  /**
   * 补全相对路径
   *
   * @param baseUrl      基准URL
   * @param relativePath 相对URL
   * @return 相对路径
   * @throws UtilException MalformedURLException
   */
  public static String completeUrl(String baseUrl, String relativePath) {
    baseUrl = normalize(baseUrl, false);
    if (StringUtils.isBlank(baseUrl)) {
      return relativePath;
    }
    try {
      final URL absoluteUrl = new URL(baseUrl);
      final URL parseUrl = new URL(absoluteUrl, relativePath);
      return parseUrl.toString();
    } catch (MalformedURLException e) {
      return baseUrl;
    }
  }

  /**
   * 标准化URL字符串，包括：
   *
   * <pre>
   * 1. 多个/替换为一个
   * </pre>
   *
   * @param url          URL字符串
   * @param isEncodePath 是否对URL中path部分的中文和特殊字符做转义（不包括 http:, /和域名部分）
   * @return 标准化后的URL字符串
   * @date 4.4.1
   */
  public static String normalize(String url, boolean isEncodePath) {
    if (StringUtils.isBlank(url)) {
      return url;
    }
    final int sepIndex = url.indexOf("://");
    String protocol;
    String body;
    if (sepIndex > 0) {
      protocol = StringUtils.subPre(url, sepIndex + 3);
      body = StringUtils.subSuf(url, sepIndex + 3);
    } else {
      protocol = "http://";
      body = url;
    }

    final int paramsSepIndex = StringUtils.indexOf(body, '?');
    String params = null;
    if (paramsSepIndex > 0) {
      params = StringUtils.subSuf(body, paramsSepIndex);
      body = StringUtils.subPre(body, paramsSepIndex);
    }

    if (StringUtils.isNotEmpty(body)) {
      // 去除开头的\或者/
      //noinspection ConstantConditions
      body = body.replaceAll("^[\\\\/]+", StringUtils.EMPTY);
      // 替换多个\或/为单个/
      body = body.replace("\\", StringUtils.BACKSLASH);
      //issue#I25MZL，双斜杠在URL中是允许存在的，不做替换
    }

    final int pathSepIndex = StringUtils.indexOf(body, '/');
    String domain = body;
    String path = null;
    if (pathSepIndex > 0) {
      domain = StringUtils.subPre(body, pathSepIndex);
      path = StringUtils.subSuf(body, pathSepIndex);
    }
    if (isEncodePath) {
      path = encode(path);
    }
    return protocol + domain + StringUtils.nullToEmpty(path) + StringUtils.nullToEmpty(params);
  }

  /**
   * 编码URL，默认使用UTF-8编码<br>
   * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
   * 此方法用于URL自动编码，类似于浏览器中键入地址自动编码，对于像类似于“/”的字符不再编码
   *
   * @param url URL
   * @return 编码后的URL
   * @throws UtilException UnsupportedEncodingException
   * @date 3.1.2
   */
  public static String encode(String url) throws UtilException {
    return encode(url, StandardCharsets.UTF_8);
  }

  /**
   * 编码字符为 application/x-www-form-urlencoded<br>
   * 将需要转换的内容（ASCII码形式之外的内容），用十六进制表示法转换出来，并在之前加上%开头。<br>
   * 此方法用于URL自动编码，类似于浏览器中键入地址自动编码，对于像类似于“/”的字符不再编码
   *
   * @param url     被编码内容
   * @param charset 编码
   * @return 编码后的字符
   * @date 4.4.1
   */
  public static String encode(String url, Charset charset) {
    if (StringUtils.isEmpty(url)) {
      return url;
    }
    if (null == charset) {
      charset = StandardCharsets.UTF_8;
    }
    return UrlEncoder.encode(url, charset.displayName());
  }

  /**
   * url是否不包含锚点
   *
   * @param url /
   * @return /
   */
  public static boolean notAnchor(String url) {
    return !url.contains("#");
  }


}
