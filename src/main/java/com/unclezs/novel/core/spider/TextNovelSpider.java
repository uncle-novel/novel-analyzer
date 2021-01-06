package com.unclezs.novel.core.spider;

import com.unclezs.novel.core.analyzer.AnalyzerHelper;
import com.unclezs.novel.core.analyzer.model.TextAnalyzerConfig;
import com.unclezs.novel.core.analyzer.text.TextNovelAnalyzer;
import com.unclezs.novel.core.matcher.RegexMatcher;
import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.model.Novel;
import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.util.uri.UrlUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文本小说爬虫
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:17 下午
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class TextNovelSpider extends AbstractNovelSpider {
    /**
     * 解析配置信息
     */
    private TextAnalyzerConfig config;


    public TextNovelSpider() {
        this(TextAnalyzerConfig.defaultConfig());
    }

    public TextNovelSpider(TextAnalyzerConfig config) {
        this.config = config;
    }


    @Override
    public List<Novel> search(RequestData requestData) {
        return null;
    }

    /**
     * 爬取小说正文 自动下一页
     *
     * @param requestData /
     * @return /
     * @throws IOException /
     */
    @Override
    public String content(RequestData requestData) throws IOException {
        String originalText;
        // 如果允许爬取多页
        if (config.isEnableContentNextPage()) {
            Set<String> pageLinks = new HashSet<>();
            pageLinks.add(requestData.getUrl());
            // 获取第一页
            StringBuilder pages = new StringBuilder();
            String page = Http.content(requestData);
            String title = RegexMatcher.titleWithNotNumber(page);
            pages.append(TextNovelAnalyzer.content(page, config));
            // 如果有下一页
            requestData.setUrl(AnalyzerHelper.nextPage(page, config.getNextPageRule(), requestData.getUrl()));
            // 如果下一页存在并且没有被抓取过（防止重复抓取的情况）
            if (UrlUtils.isHttpUrl(requestData.getUrl())) {
                multiPage(requestData, pageLinks, title, config.getNextPageRule(),
                    pageHtml -> pages.append(TextNovelAnalyzer.content(pageHtml, config)));
            }
            log.trace("文本小说抓取多页正文完成 - 共{}页 : {}.", pageLinks.size(), requestData.getUrl());
            originalText = pages.toString();
        } else {
            originalText = TextNovelAnalyzer.content(Http.content(requestData), config);
            log.trace("文本小说抓取单页正文完成：{}.", requestData.getUrl());
        }
        return originalText;
    }

    @Override
    public List<Chapter> chapters(RequestData requestData) throws IOException {
        // 抓取网页
        String page = Http.content(requestData);
        final List<Chapter> chapters;
        if (config.isEnableChapterNextPage()) {
            Set<String> pageLinks = new HashSet<>();
            pageLinks.add(requestData.getUrl());
            String title = RegexMatcher.titleWithNotNumber(page);
            List<Chapter> chapterList = new ArrayList<>(TextNovelAnalyzer.chapters(page, config));
            // 如果有下一页
            requestData.setUrl(AnalyzerHelper.nextPage(page, config.getNextChapterPageRule(), config.getBaseUri()));
            // 如果下一页存在并且没有被抓取过（防止重复抓取的情况）
            if (UrlUtils.isHttpUrl(requestData.getUrl())) {
                multiPage(requestData, pageLinks, title, config.getNextChapterPageRule(),
                    pageHtml -> chapterList.addAll(TextNovelAnalyzer.chapters(pageHtml, config)));
            }
            // 多页去重 首页可能不带 chapters/123/  但是最后一页后会跳回 chapters/123/_1 的情况
            chapters = chapterList.stream().distinct().collect(Collectors.toList());
            log.trace("文本小说抓取多页章节完成 - 共{}页，共{}章.", pageLinks.size(), chapters.size());
        } else {
            chapters = TextNovelAnalyzer.chapters(page, config);
            log.trace("文本小说抓取单页章节完成 - 共{}章.", chapters.size());
        }
        return chapters;
    }

}
