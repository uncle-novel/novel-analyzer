package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.analyzer.spider.TocSpider;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/6/22 16:04
 */
public class SpiderTest {

  @Test
  public void testToc() throws IOException {
    String url = "";
    String cookie = "";
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    AnalyzerRule rule = RuleHelper.getOrDefault(url);
//    rule.getToc().getUrl().setScript(FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\test\\resources\\script\\test.js"));
    rule.getParams().setCookie(cookie);
    TocSpider spider = new TocSpider();
    spider.setRule(rule);
    spider.setOnNewItemAddHandler(System.out::println);
    spider.toc(url);
  }

  @Test
  public void testContent() throws IOException {
    String url = "https://www.7huan.org/book/136335/6113932.html";
    String cookie = "";
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    AnalyzerRule rule = RuleHelper.getOrDefault(url);
    rule.getParams().setCookie(cookie);
    rule.getContent().setEnableNext(true);
    NovelSpider spider = new NovelSpider(rule);
    System.out.println(spider.content(url));
  }


  @Test
  public void testSearch() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    SearchSpider searchSpider = new SearchSpider(RuleHelper.rules());
    searchSpider.setOnNewItemAddHandler(e -> {
      System.out.println(e.getTitle() + "  -  " + e.getUrl());
    });
    searchSpider.search("完美世界");
  }
}
