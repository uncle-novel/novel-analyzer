package com.unclezs.novel.analyzer.request.okhttp;

import com.unclezs.novel.analyzer.common.exception.RequestFailedException;
import com.unclezs.novel.analyzer.request.HttpConfig;
import com.unclezs.novel.analyzer.request.HttpMethod;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import com.unclezs.novel.analyzer.request.ssl.SslTrustAllCerts;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.io.IoUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.BufferedSource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okHttp请求工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 12:31 上午
 */
@Slf4j
public class OkHttpClient implements HttpProvider {
  /**
   * OkHttp客户端 静态网页
   */
  @Getter
  private okhttp3.OkHttpClient staticHttpClient;

  public OkHttpClient() {
    configuration(HttpConfig.defaultConfig());
  }

  /**
   * 配置OkHttpClient
   *
   * @param config 配置信息
   */
  public void configuration(HttpConfig config) {
    // SSL配置
    SslTrustAllCerts sslTrustAllCerts = new SslTrustAllCerts();
    SSLSocketFactory sslSocketFactory = createSslSocketFactory(sslTrustAllCerts);
    staticHttpClient = new okhttp3.OkHttpClient.Builder()
      // 连接池
      .connectionPool(new ConnectionPool(config.getMaxIdleConnections(), config.getKeepAliveDuration(), TimeUnit.SECONDS))
      // 超时
      .connectTimeout(config.getConnectionTimeout(), TimeUnit.SECONDS)
      .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
      // 信任所有SSL
      .sslSocketFactory(sslSocketFactory, sslTrustAllCerts)
      .hostnameVerifier((requestedHost, remoteServerSession) -> requestedHost.equalsIgnoreCase(remoteServerSession.getPeerHost()))
      // 自动跟随重定向
      .followRedirects(config.isFollowRedirect())
      // 连接失败自动重试
      .retryOnConnectionFailure(config.isRetryOnFailed())
      .build();
  }

  /**
   * 创建SSL factory工厂
   *
   * @param manager X509TrustManager
   * @return /
   */
  private SSLSocketFactory createSslSocketFactory(X509TrustManager manager) {
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, new TrustManager[]{manager}, new SecureRandom());
      return sc.getSocketFactory();
    } catch (Exception ignored) {
      log.warn("SSL Factory 创建失败，使用默认的SSL Factory");
    }
    return (SSLSocketFactory) SSLSocketFactory.getDefault();
  }

  /**
   * 发起HTTP请求
   *
   * @param params 请求数据
   * @return /
   */
  public Call init(RequestParams params) {
    Request.Builder requestBuilder = new Request.Builder().url(params.getUrl());
    // 请求头
    if (CollectionUtils.isNotEmpty(params.getHeaders())) {
      for (Map.Entry<String, String> header : params.getHeaders().entrySet()) {
        requestBuilder.addHeader(header.getKey(), header.getValue());
      }
    }
    // 请求方法
    if (HttpMethod.GET.name().equalsIgnoreCase(params.getMethod())) {
      requestBuilder.get();
    } else {
      requestBuilder.method(params.getMethod(), RequestBody.create(MediaType.parse(params.getMediaType()), params.getBody()));
    }
    // 请求头
    if (CollectionUtils.isNotEmpty(params.getHeaders())) {
      for (Map.Entry<String, String> entry : params.getHeaders().entrySet()) {
        requestBuilder.header(entry.getKey(), entry.getValue());
      }
    }
    // 如果要使用代理
    if (params.isEnableProxy()) {
      // 创建代理
      InetSocketAddress inetSocketAddress = new InetSocketAddress(params.getProxy().getHost(), params.getProxy().getPort());
      Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
      // 复用client线程池与连接池及配置 使用代理
      return staticHttpClient.newBuilder().proxy(proxy).build().newCall(requestBuilder.build());
    }
    return staticHttpClient.newCall(requestBuilder.build());
  }

  /**
   * 获取http请求内容
   *
   * @param requestParams /
   * @return /
   */
  @Override
  public String content(RequestParams requestParams) throws IOException {
    Call request = init(requestParams);
    try (Response response = request.execute()) {
      handleFailed(response);
      ResponseBody body = response.body();
      if (body == null) {
        return StringUtils.EMPTY;
      } else {
        // 指定了编码则使用指定编码
        if (StringUtils.isNotBlank(requestParams.getCharset())) {
          return body.source().readByteString().string(Charset.forName(requestParams.getCharset()));
        }
        return getString(body.source(), body.contentType());
      }
    }
  }

  /**
   * 获取 get http请求内容
   *
   * @param url /
   * @return null if error.
   */
  public String get(String url) {
    RequestParams requestParams = RequestParams.builder().url(url).build();
    try {
      return content(requestParams);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return StringUtils.EMPTY;
  }

  /**
   * 获取流
   *
   * @param requestParams 请求数据
   * @return /
   * @throws IOException 请求失败
   */
  @Override
  public byte[] bytes(RequestParams requestParams) throws IOException {
    Call request = init(requestParams);
    try (Response response = request.execute()) {
      handleFailed(response);
      ResponseBody body = response.body();
      if (body == null) {
        return new byte[0];
      } else {
        return IoUtils.readBytes(body.byteStream());
      }
    }
  }

  @Override
  public boolean validate(RequestParams requestParams) throws IOException {
    Call call = init(requestParams);
    Response response = call.execute();
    response.close();
    return response.isSuccessful();
  }

  @Override
  public boolean isDynamic() {
    return false;
  }

  /**
   * 处理失败
   *
   * @param response 响应
   * @throws IOException 失败
   */
  private void handleFailed(Response response) throws IOException {
    if (!response.isSuccessful()) {
      throw new RequestFailedException("错误的状态码，非200-299 ：" + response);
    }
  }

  /**
   * 推断编码，并且获取正确编码的HTML
   *
   * @param source      源
   * @param contentType 响应头contentType
   * @return 结果
   * @throws IOException IO异常
   */
  private String getString(BufferedSource source, MediaType contentType) throws IOException {
    Charset charset = null;
    boolean isHtml = false;
    byte[] contentBytes = source.readByteArray();
    // 请求头中读取编码
    if (contentType != null) {
      charset = contentType.charset(null);
      if (charset == null) {
        // 根据字节前几位特征读取编码
        charset = Util.bomAwareCharset(source, StandardCharsets.UTF_16LE);
        isHtml = "text/html".equalsIgnoreCase(contentType.toString());
      }
    }
    // 没有读取到 , 如果是html则尝试从<meta/>中读取
    if (isHtml && StandardCharsets.UTF_16LE.equals(charset)) {
      // html中读取编码
      String htmlCharset = RegexUtils.matchHtmlCharset(new String(contentBytes, StandardCharsets.UTF_8));
      if (htmlCharset != null) {
        charset = Charset.forName(htmlCharset);
      }
    }
    // 都没有匹配到则使用GBK编码
    if (charset == null || StandardCharsets.UTF_16LE.equals(charset)) {
      charset = Charset.forName("GBK");
    }
    return new String(contentBytes, charset);
  }
}
