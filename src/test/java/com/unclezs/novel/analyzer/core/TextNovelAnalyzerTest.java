package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.core.text.TextNovelAnalyzer;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;
import com.unclezs.novel.analyzer.spider.TextNovelSpider;
import com.unclezs.novel.analyzer.utils.regex.RegexUtil;
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
    /**
     * 文本小说解析器
     */
    private String originalText = "";

    public void initContent() {
        String url = "https://www.yqhy.org/read/0/269/23411325_3.html";
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
        String url = "https://www.yqhy.org/read/0/269/23411325_3.html";
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_TAG);
        String content = TextNovelAnalyzer.content(Http.get(url), config);
        Assert.assertFalse(content.isEmpty());
        System.out.println(content);
    }

    @Test
    public void testContentRegular() {
        String url = "https://www.yqhy.org/read/0/269/23411325_3.html";
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setRule(Rule.TEXT_REGEX);
        String content = TextNovelAnalyzer.content(Http.get(url), config);
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
        String url = "https://m.175wx.com/chapters/269/";
        String content = Http.get(url);
        String ruleXpath = "xpath://a[text()~='下一页|下页|下节|下一节']/@href";
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultBuilder().baseUri(url).nextPageRule(ruleXpath).build();
        System.out.println(AnalyzerHelper.nextPage(content, config.getNextPageRule(), config.getBaseUri()));
    }

    /**
     * 测试抓取内容 多页
     *
     * @throws IOException
     */
    @Test
    public void testSpiderContent() throws IOException {
        String url = "https://www.175wx.com/0/269/23411303.html";
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultBuilder()
            .baseUri(url)
            .rule(Rule.TEXT_TAG)
            .enableContentNextPage(true)
            .build();
        TextNovelSpider spider = new TextNovelSpider(config);
        System.out.println(spider.content(RequestData.builder().url(url).build()));
    }

    /**
     * 测试抓取章节 多页
     *
     * @throws IOException /
     */
    @Test
    public void testSpiderChapter() throws IOException {
        String url = "https://m.175wx.com/chapters/269/";
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultBuilder()
            .baseUri(url)
            .rule(Rule.TEXT_TAG)
            .enableChapterNextPage(true)
            .build();
        TextNovelSpider spider = new TextNovelSpider(config);
        System.out.println(spider.chapters(RequestData.builder().url(url).build()));
    }

    @Test
    public void testRegex() {
        String text = "第一章 金汤匙中的战斗机（1 / 2）";
        String s = text.replaceAll("([^" + RegexUtil.CHINESE + "]|[一二三四五六七八九十])", "");
        String url = "https://www.yqhy.org/read/0/269/23411303.html";
        String html = Http.get(url);
        long start = System.currentTimeMillis();
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultConfig();
        System.out.println(AnalyzerHelper.nextPage(html, config.getNextPageRule(), config.getBaseUri()));
        System.out.println(System.currentTimeMillis() - start);
    }
}
