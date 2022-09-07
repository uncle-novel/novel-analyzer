package com.unclezs.novel.analyzer.request.proxy;

import com.jayway.jsonpath.JsonPath;
import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认线程池提供类(不保证长期有效)
 * <p>
 * https://github.com/jiangxianli/ProxyIpLib#%E5%85%8D%E8%B4%B9%E4%BB%A3%E7%90%86ip%E5%BA%93
 *
 * @author blog.unclezs.com
 * @since 2020/12/27 6:01 下午
 */
@Slf4j
public class DefaultProxyProvider extends AbstractProxyProvider {
  public static final int MAX_PROXY_NUMBER = 30;
  private static final String PROXY_SITE = "https://ip.jiangxianli.com/api/proxy_ips?page=1";
  private static final AtomicBoolean LOADING = new AtomicBoolean(false);

  public DefaultProxyProvider() {
    super(false, MAX_PROXY_NUMBER);
  }

  /**
   * 异步加载代理
   */
  @Override
  public synchronized void loadProxy() {
    // 正在抓取标志位
    if (LOADING.get()) {
      return;
    }
    LOADING.set(true);
    ThreadUtils.execute(() -> {
      try {
        String url = PROXY_SITE;
        do {
          RequestParams requestParams = RequestParams.builder()
            .url(url)
            .autoProxy(false)
            .enableProxy(false).build();
          String json;
          try {
            json = Http.content(requestParams);
          } catch (IOException e) {
            log.debug("代理站点抓取失败:{} 停止抓取代理", url, e);
            return;
          }
          List<String> ips = JsonPath.read(json, "$.data.data[*].ip");
          List<String> ports = JsonPath.read(json, "$.data.data[*].port");
          for (int i = 0; i < ips.size(); i++) {
            String host = ips.get(i);
            int port = Integer.parseInt(ports.get(i));
            HttpProxy proxy = new HttpProxy(host, port);
            if (verifyFailed(proxy)) {
              continue;
            }
            boolean full = super.addProxy(proxy);
            if (full) {
              log.debug("抓取代理数量达到上限：{}/{}", super.proxyNum(), MAX_PROXY_NUMBER);
              return;
            }
            log.debug("新抓取代理：{}:{}", host, port);
          }
          url = JsonPath.read(json, "$.data.next_page_url");
        } while (StringUtils.isNotEmpty(url));
        log.info("当前代理池中代理总数：{}", proxyNum());
      } finally {
        LOADING.set(false);
      }
    });
    // 等待有可用代理，等待10s后如果还无代理直接返回
    long start = System.currentTimeMillis();
    while (super.proxyNum() == 0) {
      if (System.currentTimeMillis() - start > 10000L) {
        return;
      }
      ThreadUtils.sleep(1000L);
    }
  }
}
