package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.core.text.TextNovelAnalyzer;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2020/12/21 2:14 上午
 */
@Slf4j
public class AnalyzerTest {
    private String originalText = "";

    @Before
    public void initContent() {
        String url = "https://vipreader.qidian.com/ajax/chapter/chapterInfo?_csrfToken=omcSdg2IPa5AEfOc1xzW9ZQGTOMVHxysmTKlBCWT&bookId=1012284323&chapterId=421406437&authorId=402573440";
        RequestData requestData = new RequestData();
        requestData.setUrl(url);
        try {
            originalText = Http.content(requestData);
        } catch (IOException e) {
            log.error("请求失败：{}", url, e);
        }
        System.out.println(originalText);
        Assert.assertFalse(originalText.isEmpty());
    }

    @Test
    public void testContentTag() {
        TextNovelAnalyzer analyzer = new TextNovelAnalyzer();
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_TAG);
        String content = analyzer.content(originalText, config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }

    @Test
    public void testContentRegular() {
        TextNovelAnalyzer analyzer = new TextNovelAnalyzer();
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_REGULAR_SIMPLE);
        String content = analyzer.content(originalText, config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }

    @Test
    public void testContentRegularStrict() {
        TextNovelAnalyzer analyzer = new TextNovelAnalyzer();
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_REGULAR);
        String content = analyzer.content(originalText, config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }
}
