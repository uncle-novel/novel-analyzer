package com.unclezs.novel.analyzer.request;

import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.ServiceLoader;

/**
 * @author blog.unclezs.com
 * @date 2020/12/25 12:58 上午
 */
public class ProviderTest {
    @Test
    public void test() throws IOException {
        ServiceLoader<HttpProvider> httpProvider = ServiceLoader.load(HttpProvider.class);
        for (HttpProvider s : httpProvider) {
            System.out.println(s.content(null));
        }
    }
}
