package com.unclezs.novel.core.analyzer;

import com.unclezs.novel.core.utils.uri.UrlUtil;
import org.junit.Test;

/**
 * @author blog.unclezs.com
 * @since 2020/12/22 15:05
 */
public class AppTest {
    @Test
    public void test() {
        String sUrl = "/read/0/269/23411303_2.html";
        String baseUri = "https://www.yqhy.org/read/0/269/23411303.html";
        System.out.println(UrlUtil.completeUrl(baseUri, sUrl));
    }
}
