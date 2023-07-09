package com.unclezs.novel.analyzer.request;

import com.unclezs.novel.analyzer.core.model.Params;
import com.unclezs.novel.analyzer.model.Verifiable;
import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求数据
 *
 * @author blog.unclezs.com
 * @since 2020/12/20 5:51 下午
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestParams implements Verifiable, Serializable {
  public static final String REFERER = "Referer";
  public static final String AUTO_REFERER = "auto";
  public static final String COOKIE = "Cookie";
  public static final String USER_AGENT = "User-Agent";
  public static final String USER_AGENT_DEFAULT_VALUE = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";
  public static final String HEADER_SEPARATOR = ": ";
  private static final long serialVersionUID = 5277637191594804618L;
  /**
   * 请求链接
   */
  private String url;
  /**
   * 请求方法
   */
  private String method;
  /**
   * 网页编码
   */
  private String charset;
  /**
   * 请求头
   */
  private Map<String, String> headers;
  /**
   * 请求方式
   */
  private String mediaType;
  /**
   * 请求体
   */
  private String body;

  /**
   * URL参数(查询字符串)
   */
  private String urlParams;
  /**
   * 是否为动态网页
   */
  private Boolean dynamic;
  /**
   * HTTP代理信息
   */
  private HttpProxy proxy;
  /**
   * 启用HTTP代理 标记允许代理 如果开发了全局代理配置这个字段将会自动设置为true
   */
  private Boolean enableProxy;
  /**
   * 自动代理
   * 请在全局代理中开启自动代理配置 并且设置此字段为true 才会真正启用全局代理
   * 这么做是为了方便全局控制代理的热拔插
   * 优先级 先判断此此字段为true 再判断全局AnalyzerManager.autoProxy是否开启
   */
  private Boolean autoProxy;
  /**
   * 处理脚本，在请求结束后（比如webview中执行）
   */
  private String script;
  /**
   * 动态网页延迟时间
   */
  private Long dynamicDelayTime = 500L;

  /**
   * 默认请求配置
   *
   * @param url url
   * @return /
   */
  public static RequestParams create(String url) {
    return builder().url(url).build();
  }

  /**
   * 创建一个请求
   *
   * @param url    网页链接
   * @param params 请求参数
   * @return RequestParams
   */
  public static RequestParams create(String url, RequestParams params) {
    if (params == null) {
      params = new RequestParams();
    } else {
      params = params.copy();
    }
    // 添加URL参数
    if (StringUtils.isNotBlank(params.getUrlParams())) {
      url = url+ (url.contains("?") ? "&" : "?") + params.getUrlParams();
    }
    params.setUrl(url);
    return params;
  }

  public String getMethod() {
    return StringUtils.isNotEmpty(method) ? method.toUpperCase() : HttpMethod.GET.name();
  }

  /**
   * 是否启用代理
   *
   * @return true则启用
   */
  public boolean isEnableProxy() {
    if (this.proxy == null || this.proxy == HttpProxy.NO_PROXY) {
      return false;
    }
    return Boolean.TRUE.equals(enableProxy);
  }

  /**
   * 获取MediaType,非GET默认为FORM
   *
   * @return mediaType
   */
  public String getMediaType() {
    if (StringUtils.isNotBlank(mediaType)) {
      if (MediaType.FORM.name().equalsIgnoreCase(mediaType)) {
        return MediaType.FORM.getMediaType();
      }
      if (MediaType.JSON.name().equalsIgnoreCase(mediaType)) {
        return MediaType.JSON.getMediaType();
      }
    }
    return mediaType;
  }

  /**
   * 复制一份
   *
   * @return /
   */
  public RequestParams copy() {
    return SerializationUtils.deepClone(this);
  }

  /**
   * 获取请求头
   *
   * @param headerName  名称
   * @param defaultName 默认值
   * @return 请求头
   */
  public String getHeader(String headerName, String defaultName) {
    if (CollectionUtils.isNotEmpty(headers)) {
      return headers.getOrDefault(headerName, defaultName);
    }
    return defaultName;
  }

  /**
   * 获取请求头
   *
   * @param headerName 名称
   * @return 请求头
   */
  public String getHeader(String headerName) {
    if (CollectionUtils.isNotEmpty(headers)) {
      return headers.get(headerName);
    }
    return null;
  }

  /**
   * 设置请求头 存在则覆盖
   *
   * @param headerName  名称
   * @param headerValue 值
   */
  public void setHeader(String headerName, String headerValue) {
    if (headers == null) {
      headers = new HashMap<>(8);
    }
    headers.put(headerName, headerValue);
  }



  /**
   * 设置请求头 如果不存在则设置 存在则忽略
   *
   * @param headerName  名称
   * @param headerValue 值
   */
  public void addHeader(String headerName, String headerValue) {
    if (headers == null) {
      headers = new HashMap<>(8);
      headers.put(headerName, headerValue);
    } else {
      headers.putIfAbsent(headerName, headerValue);
    }
  }

  /**
   * 获取请求头字符串
   *
   * @return key: value\n ...
   */
  public String getHeaderString() {
    if (headers == null) {
      return null;
    }
    StringBuilder headersStr = new StringBuilder();
    this.headers.forEach((key, value) -> headersStr.append(key).append(HEADER_SEPARATOR).append(value).append(StringUtils.LF));
    return headersStr.toString();
  }

  /**
   * <pre>
   *   key: value\n ...
   *   key: value\n ...
   * </pre>
   * <p>
   * 设置请求头字符串
   */
  public void setHeaderString(String headersString) {
    if (StringUtils.isBlank(headersString) && headers != null) {
      headers.clear();
      return;
    }
    String[] headerLines = headersString.split(StringUtils.LF);
    // 校验合法性
    for (String headerLine : headerLines) {
      if (!headerLine.contains(HEADER_SEPARATOR) || headerLine.endsWith(HEADER_SEPARATOR)) {
        return;
      }
    }
    headers = new HashMap<>(16);
    for (String headerLine : headerLines) {
      String[] split = headerLine.split(HEADER_SEPARATOR);
      headers.put(split[0], split[1]);
    }
  }

  /**
   * 覆盖默认参数
   *
   * @param params 参数
   */
  public void overrideParams(Params params) {
    if (params == null) {
      return;
    }
    // cookie
    if (StringUtils.isNotBlank(params.getCookie()) && getHeader(RequestParams.COOKIE) == null) {
      setHeader(RequestParams.COOKIE, params.getCookie());
    }
    // ua
    if (StringUtils.isNotBlank(params.getUserAgent()) && getHeader(RequestParams.USER_AGENT) == null) {
      setHeader(RequestParams.USER_AGENT, params.getUserAgent());
    }
    // 动态网页
    if (Boolean.TRUE.equals(params.getDynamic())) {
      setDynamic(true);
    }
    // 代理
    if (Boolean.TRUE.equals(params.getAutoReferer())) {
      addHeader(RequestParams.REFERER, AUTO_REFERER);
    }
    // 代理
    if (Boolean.TRUE.equals(params.getEnabledProxy())) {
      setEnableProxy(true);
    }
  }

  @Override
  public boolean isEffective() {
    return UrlUtils.isHttpUrl(url);
  }
}
