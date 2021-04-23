package com.unclezs.novel.analyzer.request;


import com.unclezs.novel.analyzer.request.phantomjs.PhantomJsClient;
import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.analyzer.util.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * @author blog.unclezs.com
 * @date 2020/12/24 11:35
 */
public class PhantomJsClientTest {
    private static final String exePath = "/Users/zhanghongguo/coder/uncle-novel/novel-com.unclezs.novel.analyzer.analyzer/lib/phantomjs";
    private static final String jsBasePath = "/Users/zhanghongguo/coder/uncle-novel/novel-com.unclezs.novel.analyzer.analyzer/lib/%s.js";
    public static final String URL = "http://httpbin.org/get?sb=1";
    private static final String proxyUrl = "http://myip.ipip.net/";

    @Test
    public void testArguments() {
        System.out.println(Http.get(URL, true));
    }


    @Test
    public void testProxy() throws IOException {
        HttpProxy proxy = new HttpProxy("116.117.134.134", 82);
        RequestParams requestParams =
            RequestParams.builder().url(proxyUrl).dynamic(true).enableProxy(true).proxy(proxy).build();
        String content = Http.content(requestParams);
        System.out.println(content);
    }

    public static void main(String[] args) throws URISyntaxException {
        System.out.println(PhantomJsClient.class.getResource("/script/spider.js").toURI().toString());
    }

    // 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。
    public static String html(String url, String userAgent, String cookie, String referer) throws IOException {
        return executePhantomJs("com/unclezs/novel/analyzer/spider", url);
    }

    /**
     * 执行phantomJS
     *
     * @param script 脚本名称
     * @param args   参数
     */
    public static String executePhantomJs(String script, String... args) throws IOException {
        String scriptPath = String.format(jsBasePath, script);
        StringBuilder command = new StringBuilder();
        command.append(exePath).append(StringUtils.BLANK).append(scriptPath);
        for (String arg : args) {
            command.append(StringUtils.BLANK).append(arg);
        }
        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command.toString());
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder buffer = new StringBuilder();
        String tmp;
        while ((tmp = br.readLine()) != null) {
            buffer.append(tmp).append(StringUtils.LF);
        }
        return buffer.toString();
    }
}
