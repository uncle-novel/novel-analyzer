package com.unclezs.novel.core.analyzer;

import com.unclezs.novel.core.analyzer.model.Rule;
import com.unclezs.novel.core.analyzer.model.TextAnalyzerConfig;
import com.unclezs.novel.core.analyzer.text.TextNovelAnalyzer;
import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.spider.AbstractNovelSpider;
import com.unclezs.novel.core.spider.TextNovelSpider;
import com.unclezs.novel.core.spider.pipline.ConsolePipeline;
import com.unclezs.novel.core.util.StringUtils;
import com.unclezs.novel.core.util.regex.PatternPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author blog.unclezs.com
 * @date 2020/12/21 2:14 上午
 */
@Slf4j
public class TextNovelAnalyzerTest {
    /**
     * 文本小说解析器
     */
    private String originalText = StringUtils.EMPTY;

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
        String url = StringUtils.EMPTY;
        String html = Http.get(url);
        TextAnalyzerConfig config = new TextAnalyzerConfig();
        config.setBaseUri(url);
        config.setChapterFilter(false);
        List<Chapter> chapters = TextNovelAnalyzer.chapters(html, config);
        chapters.forEach(System.out::println);
        String content = TextNovelAnalyzer.content(Http.get(StringUtils.EMPTY), config);
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
     * @throws IOException /
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
        String url = "http://www.dmbj.cc/daomubiji1/";
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultBuilder()
            .baseUri(url)
            .rule(Rule.TEXT_TAG)
            .enableChapterNextPage(true)
            .build();
        AbstractNovelSpider spider = new TextNovelSpider(config);
        System.out.println(spider.chapters(RequestData.builder().url(url).build()));
    }

    @Test
    public void testRegex() {
        String text = "\n" +
            "    上一页\n" +
            "\n" +
            "    返回目录\n" +
            "\n" +
            "    阅读记录\n" +
            "\n" +
            "    下一章\n" +
            "2020-12-23";
        System.out.println(PatternPool.get("上一页.+?下一章", Pattern.DOTALL).matcher(text).replaceAll(StringUtils.EMPTY));
    }

    /**
     * 测试爬取一本完整小说
     *
     * @throws IOException /
     */
    @Test
    public void testCrawling() throws IOException {
        String url = "https://m.175wx.com/chapters/269/";
        TextAnalyzerConfig config = TextAnalyzerConfig.defaultBuilder()
            .baseUri(url).enableContentNextPage(true).enableChapterNextPage(false)
            .advertisements(Collections.singletonList("上一页[\\s\\S]+?下一."))
            .build();
        AbstractNovelSpider spider = new TextNovelSpider(config);
        spider.crawling(RequestData.builder().url(url).build(), new ConsolePipeline<>());
    }
}
