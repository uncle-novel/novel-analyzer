package com.unclezs.novel.analyzer.request;

import com.unclezs.novel.analyzer.AnalyzerManager;
import com.unclezs.novel.analyzer.request.proxy.DefaultProxyProvider;
import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import com.unclezs.novel.analyzer.request.spi.ProxyProvider;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Http请求工具，支持动态与静态网页
 * 兼容SPI机制加载自定义HTTP客户端与代理提供商
 *
 * @author blog.unclezs.com
 * @since 2020/12/21 12:31 上午
 */
@UtilityClass
public class Http {
  private static final Logger LOG = LoggerFactory.getLogger(Http.class);
  /**
   * 代理提供类
   */
  private static final ProxyProvider PROXY_PROVIDER;
  /**
   * 动态网页Http客户端
   */
  @Getter
  private static HttpProvider dynamicHttpClient;
  /**
   * 静态网页Http客户端
   */
  @Getter
  private static HttpProvider staticHttpClient;

  static {
    // 加载自定义的 动态/静态网页Http客户端
    ServiceLoader<HttpProvider> httpProviders = ServiceLoader.load(HttpProvider.class);
    for (HttpProvider provider : httpProviders) {
      if (provider.isDynamic()) {
        dynamicHttpClient = provider;
      } else {
        staticHttpClient = provider;
      }
    }
    // 没有提供则使用默认
    if (dynamicHttpClient == null) {
      try {
        dynamicHttpClient = (HttpProvider) Class.forName("com.unclezs.novel.analyzer.request.phantomjs.PhantomJsClient").newInstance();
      } catch (Exception e) {
        LOG.error("未配置动态网页客户端，并且加载默认客户端PhantomJsClient失败 :com.unclezs.novel.analyzer.request.phantomjs.PhantomJsClient", e);
      }
    }
    if (staticHttpClient == null) {
      try {
        staticHttpClient = (HttpProvider) Class.forName("com.unclezs.novel.analyzer.request.okhttp.OkHttpClient").newInstance();
      } catch (Exception e) {
        LOG.error("未配置静态态网页客户端，并且加载默认客户端OkHttpClient失败，请确定已经引入Okhttp依赖 : com.unclezs.novel.analyzer.request.okhttp.OkHttpClient", e);
      }
    }
    // 初始化ProxyProvider 没有就使用默认的
    ServiceLoader<ProxyProvider> proxyProviders = ServiceLoader.load(ProxyProvider.class);
    Iterator<ProxyProvider> proxyProviderIterator = proxyProviders.iterator();
    if (proxyProviderIterator.hasNext()) {
      PROXY_PROVIDER = proxyProviderIterator.next();
    } else {
      PROXY_PROVIDER = new DefaultProxyProvider();
    }
  }


  /**
   * 获取http请求内容
   *
   * @param requestParams /
   * @return /
   */
  public static String content(RequestParams requestParams) throws IOException {
    initDefaultRequestParams(requestParams);
    try {
      String content = client(requestParams).content(requestParams);
      return scriptPreHandle(requestParams, content);
    } catch (IOException e) {
      proxyFailed(requestParams);
      throw new IOException(e);
    }
  }

  /**
   * 获取 get http请求内容
   *
   * @param url     /
   * @param dynamic 动态网页
   * @return null if error.
   */
  public static String get(String url, boolean dynamic) {
    RequestParams requestParams = RequestParams.builder().dynamic(dynamic).url(url).build();
    try {
      return content(requestParams);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return StringUtils.EMPTY;
  }

  /**
   * 获取 get http请求内容 静态网页
   *
   * @param url /
   * @return null if error.
   */
  public static String get(String url) {
    return get(url, false);
  }

  /**
   * 获取流 默认动态网页客户端不支持流
   *
   * @param requestParams 请求数据
   * @return /
   * @throws IOException 请求失败
   */
  public static byte[] bytes(RequestParams requestParams) throws IOException {
    initDefaultRequestParams(requestParams);
    try {
      return client(requestParams).bytes(requestParams);
    } catch (IOException e) {
      proxyFailed(requestParams);
      throw new IOException(e);
    }
  }

  /**
   * 验证链接是否有效
   *
   * @param requestParams 请求参数
   * @return this
   * @throws IOException IO异常
   */
  public static boolean validate(RequestParams requestParams) throws IOException {
    initDefaultRequestParams(requestParams);
    try {
      return client(requestParams).validate(requestParams);
    } catch (IOException e) {
      proxyFailed(requestParams);
      throw new IOException(e);
    }
  }

  /**
   * 根据请求参数获取是静态请求还是动态请求
   *
   * @param params 请求参数
   * @return HttpProvider
   */
  private HttpProvider client(RequestParams params) {
    if (Boolean.TRUE.equals(params.getDynamic())) {
      return dynamicHttpClient;
    }
    return staticHttpClient;
  }

  /**
   * 请求失败移除代理
   *
   * @param requestParams /
   */
  private void proxyFailed(RequestParams requestParams) {
    if (Boolean.TRUE.equals(requestParams.getAutoProxy())) {
      PROXY_PROVIDER.removeProxy(requestParams.getProxy());
    }
  }

  /**
   * 初始化请求的默认值
   *
   * @param requestParams /
   */
  private void initDefaultRequestParams(RequestParams requestParams) {
    // 是否使用自动获取代理 需要全局配置中也要打开 不然不会进行代理控制
    if (Boolean.TRUE.equals(requestParams.getAutoProxy()) && AnalyzerManager.me().isAutoProxy()) {
      requestParams.setProxy(PROXY_PROVIDER.getProxy());
    }
    if (requestParams.getMediaType() == null) {
      requestParams.setMediaType(MediaType.NONE.getMediaType());
    }
    // 初始化请求头
    if (RequestParams.AUTO_REFERER.equals(requestParams.getHeader(RequestParams.REFERER))) {
      requestParams.setHeader(RequestParams.REFERER, requestParams.getUrl());
    }
    requestParams.addHeader(RequestParams.USER_AGENT, RequestParams.USER_AGENT_DEFAULT_VALUE);
  }

  /**
   * 脚本预处理
   *
   * @param params 请求参数
   * @param source 源码
   * @return 处理后的结果
   */
  private String scriptPreHandle(RequestParams params, String source) {
    if (Boolean.FALSE.equals(params.getDynamic()) && StringUtils.isNotBlank(params.getScript())) {
      try {
        ScriptContext.put(ScriptContext.VAR_SOURCE, source);
        ScriptContext.put(ScriptContext.VAR_URL, params.getUrl());
        ScriptContext.put(ScriptContext.VAR_PARAMS, params);
        return ScriptUtils.execute(params.getScript(), ScriptContext.current());
      } catch (Throwable e) {
        LOG.error("预处理脚本执行失败:{},url:{}", params.getScript(), params.getUrl(), e);
      } finally {
        ScriptContext.remove(ScriptContext.VAR_SOURCE, ScriptContext.VAR_URL, ScriptContext.VAR_PARAMS);
      }
    }
    return source;
  }
}
