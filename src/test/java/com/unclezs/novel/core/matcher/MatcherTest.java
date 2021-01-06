package com.unclezs.novel.core.matcher;

import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @since 2020/12/21 15:48
 */
@Slf4j
public class MatcherTest {
    private String originalText = StringUtils.EMPTY;

    public void initContent(String url) {
        RequestData requestData = new RequestData();
        requestData.setUrl(url);
        try {
            originalText = Http.content(requestData);
        } catch (IOException e) {
            log.error("请求失败：{}", url, e);
        }
        Assert.assertFalse(originalText.isEmpty());
    }

    @Test
    public void testJsonPath() {
        initContent("http://httpbin.org/get");
        log.trace("matched:{}", Matcher.matching(originalText, "json:$.headers['X-Amzn-Trace-Id']"));
    }

    @Test
    public void testXpath() {
        initContent("https://www.cnblogs.com/notayeser/p/12654551.html");
        log.trace("matched:{}",
            Matcher.matching(originalText, "xpath://*[@id=\"cnblogs_post_body\"]/p[21]//allText()"));
    }

    @Test
    public void testCSS() {
        initContent("https://www.cnblogs.com/notayeser/p/12654551.html");
        log.trace("matched:{}", Matcher.matching(originalText, "css:#cnblogs_post_body > p:nth-child(34)@text"));
    }

    @Test
    public void testRegex() {
        initContent("https://www.cnblogs.com/notayeser/p/12654551.html");
        log.trace("matched:{}", Matcher.matching(originalText, "regex:不难发现(.+?)</p>"));
    }
}
