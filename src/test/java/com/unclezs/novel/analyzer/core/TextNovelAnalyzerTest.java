package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.core.text.TextNovelAnalyzer;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;
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
    String html = Http.get("https://www.yqhy.org/read/0/269/");
    TextAnalyzerConfig config = new TextAnalyzerConfig();
    config.setBaseUri("https://www.yqhy.org/read/0/269/");
    config.setChapterFilter(true);
    List<Chapter> chapters = TextNovelAnalyzer.chapters(html, config);
    config.setRule(Rule.TEXT_TAG);
    String originalText = Http.get(chapters.get(111).getUrl());
    System.out.println(originalText);
    System.out.println(TextNovelAnalyzer.content(originalText,config));
  }

}
