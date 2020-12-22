package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.core.text.TextNovelAnalyzer;
import com.unclezs.novel.analyzer.matcher.Matcher;
import com.unclezs.novel.analyzer.matcher.RegexMatcher;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;
import com.unclezs.novel.analyzer.spider.TextNovelSpider;
import com.unclezs.novel.analyzer.spider.model.Chapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2020/12/21 2:14 上午
 */
@Slf4j
public class TextNovelAnalyzerTest {
    private String originalText = "";

    public void initContent() {
        String url =
            "https://vipreader.qidian.com/ajax/chapter/chapterInfo?_csrfToken=omcSdg2IPa5AEfOc1xzW9ZQGTOMVHxysmTKlBCWT&bookId=1012284323&chapterId=421406437&authorId=402573440";
        RequestData requestData = RequestData.builder().url(url).build();
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
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_TAG);
        String content = TextNovelAnalyzer.content(originalText, config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }

    @Test
    public void testContentRegular() {
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_REGEX);
        String content = TextNovelAnalyzer.content(originalText, config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }

    @Test
    public void testContentRegularStrict() {
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_REGEX_STRICT);
        String content = TextNovelAnalyzer.content(originalText, config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }

    @Test
    public void testChapters() {
        String url = "";
        String html = Http.get(url);
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setBaseUri(url);
        config.setChapterFilter(false);
        List<Chapter> chapters = TextNovelAnalyzer.chapters(html, config);
        chapters.stream().forEach(System.out::println);
        String content = TextNovelAnalyzer.content(Http.get(""), config);
        System.out.println(content);
    }


    @Test
    public void nextPage() {
        String content = Http.get("https://www.yqhy.org/read/0/269/23411303.html");
        System.out.println(RegexMatcher.matcher(content, "<a href=\"/read/0/269/\">目录</a>\n"
            + "\t\t\t<a href=\"(.+?)\">下一章</a>@@$1"));
        System.out.println(Matcher.matching(content, "xpath://a[contains(text(),'下一章')]/@href"));
    }

    @Test
    public void testSpiderContent() throws IOException {
        String url = "https://www.yqhy.org/read/0/269/23411303.html";
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultBuilder()
            .baseUri(url)
            .nextPageRule("xpath://a[contains(text(),'下一章')]/@href").build();
        TextNovelSpider spider = new TextNovelSpider(config);
        System.out.println(spider.content(RequestData.builder().url(url).build()));
    }
}
