package com.unclezs.novel.core.request.proxy;

import com.jayway.jsonpath.JsonPath;
import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * 默认线程池提供类(不保证长期有效)
 * <p>
 * https://github.com/jiangxianli/ProxyIpLib#%E5%85%8D%E8%B4%B9%E4%BB%A3%E7%90%86ip%E5%BA%93
 *
 * @author blog.unclezs.com
 * @date 2020/12/27 6:01 下午
 */
@Slf4j
public class DefaultProxyProvider extends AbstractProxyProvider {
    private static final String PROXY_SITE = "https://ip.jiangxianli.com/api/proxy_ips?page=1";
    public static final int MAX_PROXY_NUMBER = 30;

    public DefaultProxyProvider() {
        super(false, MAX_PROXY_NUMBER);
    }

    @Override
    public void loadProxy() {
        String url = PROXY_SITE;
        do {
            RequestData requestData = RequestData.defaultBuilder(url).autoProxy(false).enableProxy(false).build();
            String json;
            try {
                json = Http.content(requestData);
            } catch (IOException e) {
                log.debug("代理站点抓取失败:{} 停止抓取代理", url, e);
                return;
            }
            List<String> ips = JsonPath.read(json, "$.data.data[*].ip");
            List<String> ports = JsonPath.read(json, "$.data.data[*].port");
            for (int i = 0; i < ips.size(); i++) {
                String host = ips.get(i);
                int port = Integer.parseInt(ports.get(i));
                boolean full = super.addProxy(host, port);
                if (full) {
                    log.debug("抓取代理数量达到上限：{}/{}", super.proxyNum(), MAX_PROXY_NUMBER);
                    return;
                }
                log.debug("新抓取代理：{}:{}", host, port);
            }
            url = JsonPath.read(json, "$.data.next_page_url");
        } while (StringUtils.isNotEmpty(url));
        log.info("当前代理池中代理总数：{}", proxyNum());
    }

    public static void main(String[] args) {
        DefaultProxyProvider provider = new DefaultProxyProvider();
    }
}
