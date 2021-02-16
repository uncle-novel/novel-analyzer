package com.unclezs.novel.analyzer.request.proxy;

import lombok.Data;

import java.util.Objects;

/**
 * Http代理Model
 *
 * @author blog.unclezs.com
 * @date 2020/12/27 12:39 下午
 */
@Data
public class HttpProxy {
    public static final HttpProxy NO_PROXY = new HttpProxy("127.0.0.1", 80);
    private String host;
    private Integer port;

    public HttpProxy() {
    }

    public HttpProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpProxy httpProxy = (HttpProxy) o;
        return Objects.equals(host, httpProxy.host) && Objects.equals(port, httpProxy.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
