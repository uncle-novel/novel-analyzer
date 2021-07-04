package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.Http;
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
    String url = "https://m.miaojiang8.net/0_687/63971.html";
    String cookie = "";
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    AnalyzerRule rule = RuleHelper.getOrDefault(url);
    rule.getContent().setNext(CommonRule.create("xpath://a[text()~='.*?下[一]{0,1}[页节章].*']/@href"));
    rule.getParams().setCookie(cookie);
    rule.getContent().setEnableNext(true);
    NovelSpider spider = new NovelSpider(rule);
    System.out.println(spider.content(url, System.out::println));
  }


  @Test
  public void testSearch() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    SearchSpider searchSpider = new SearchSpider(RuleHelper.rules());
    searchSpider.setOnNewItemAddHandler(e -> {
      System.out.println(e.getTitle() + "  -  " + e.getUrl());
    });
    searchSpider.search("完美");
  }

  @Test
  public void testDetail() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    AnalyzerRule rule = RuleHelper.getRule("https://m.miaojiang8.net/3_3155/");
    Novel novel = NovelMatcher.details(Http.get("https://m.miaojiang8.net/3_3155/"), rule.getDetail());
    System.out.println(novel);
  }

}
