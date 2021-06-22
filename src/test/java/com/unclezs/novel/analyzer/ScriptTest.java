package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/01/29 10:38
 */
public class ScriptTest {

  @Test
  public void test() throws IOException {
    String url = "";
    NovelSpider spider = new NovelSpider();
    // 正文规则封装
    AnalyzerRule analyzerRule = new AnalyzerRule();
    analyzerRule.setAudio(true);
    analyzerRule.setSite(url);
    ContentRule contentRule = new ContentRule();
    CommonRule rule = CommonRule.create("//div[4]/script[1]/@src");
    rule.setScript(FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\test\\resources\\script\\test.js"));
    contentRule.setContent(rule);
    analyzerRule.setContent(contentRule);
    spider.setRule(analyzerRule);
    spider.content(url, System.out::println);
  }
}
