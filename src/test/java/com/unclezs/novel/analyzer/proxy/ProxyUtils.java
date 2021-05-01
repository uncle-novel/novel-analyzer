package com.unclezs.novel.analyzer.proxy;

import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * 系统属性工具类
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 10:22
 */
@UtilityClass
public class ProxyUtils {

  public static final String SYSTEM_PROXIES = "java.net.useSystemProxies";
  public static final String HTTP_PROXY_HOST = "http.proxyHost";
  public static final String HTTP_PROXY_PORT = "http.proxyPort";
  public static final String HTTP_PROXY_USER = "http.proxyUser";
  public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
  public static final String GOOGLE = "https://www.google.com";
  public static final String SOCKS_PROXY_HOST = "socksProxyHost";
  public static final String SOCKS_PROXY_PORT = "socksProxyPort";
  private static final ProxySelector DEFAULT_SELECTOR;
  private static SystemProxySelector systemProxySelector;

  static {
    System.setProperty(SYSTEM_PROXIES, "true");
    systemProxySelector = new SystemProxySelector();
    DEFAULT_SELECTOR = ProxySelector.getDefault();
    ProxySelector.setDefault(systemProxySelector);
  }

  public static void setEnabled(boolean en) {
    systemProxySelector.setEnabledSystemProxy(en);
  }

  /**
   * 设置代理Host
   *
   * @param host 主机地址
   */
  public static void setHttpProxyHost(String host) {
    if (StringUtils.isBlank(host)) {
      System.clearProperty(HTTP_PROXY_HOST);
    } else {
      System.setProperty(HTTP_PROXY_HOST, host);
    }
  }

  /**
   * 设置代理端口
   *
   * @param port 端口
   */
  public static void setHttpProxyPort(String port) {
    if (StringUtils.isBlank(port)) {
      System.clearProperty(HTTP_PROXY_PORT);
    } else {
      System.setProperty(HTTP_PROXY_PORT, port);
    }
  }

  /**
   * 设置代理认证用户
   *
   * @param user 用户
   */
  public static void setHttpProxyUser(String user) {
    if (StringUtils.isBlank(user)) {
      System.clearProperty(HTTP_PROXY_USER);
    } else {
      System.setProperty(HTTP_PROXY_USER, user);
    }
  }

  /**
   * 设置代理认证密码
   *
   * @param password 密码
   */
  public static void setHttpProxyPassword(String password) {
    if (StringUtils.isBlank(password)) {
      System.clearProperty(HTTP_PROXY_PASSWORD);
    } else {
      System.setProperty(HTTP_PROXY_PASSWORD, password);
    }
  }

  /**
   * 清除代理信息
   */
  public static void clearProxy() {
    System.clearProperty(HTTP_PROXY_HOST);
    System.clearProperty(HTTP_PROXY_PORT);
    System.clearProperty(HTTP_PROXY_USER);
    System.clearProperty(HTTP_PROXY_PASSWORD);
  }

  /**
   * 获取系统HTTP代理
   *
   * @return 系统代理
   */
  public static HttpProxy getSystemProxy() {
    Proxy proxy = DEFAULT_SELECTOR.select(URI.create(GOOGLE)).get(0);
    if (proxy != Proxy.NO_PROXY) {
      String[] s = proxy.address().toString().split(":");
      return new HttpProxy(s[0], Integer.parseInt(s[1]));
    }
    return HttpProxy.NO_PROXY;
  }

  /**
   * 系统代理选择器
   */
  static class SystemProxySelector extends ProxySelector {

    @Setter
    private boolean enabledSystemProxy;

    @Override
    public List<Proxy> select(URI uri) {
      if (enabledSystemProxy) {
        return DEFAULT_SELECTOR.select(URI.create(GOOGLE));
      }
      return Collections.singletonList(Proxy.NO_PROXY);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
      DEFAULT_SELECTOR.connectFailed(uri, sa, ioe);
    }
  }

  /**
   * 无代理
   */
  static class NullProxySelector extends ProxySelector {

    @Override
    public List<Proxy> select(URI uri) {

      return Collections.singletonList(Proxy.NO_PROXY);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
      // ignored
    }
  }
}
