package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.utils.uri.UrlUtil;
import org.junit.Test;

/**
 * @author zhanghongguo@sensorsdata.cn
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
