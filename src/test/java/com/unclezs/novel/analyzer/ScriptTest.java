package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.matcher.Matchers;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * @author blog.unclezs.com
 * @date 2021/01/29 10:38
 */
public class ScriptTest {

  @Test
  public void test() throws IOException {
    String url = "https://www.po18.tw/books/742514/articles";
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

  @Test
  public void runJs() throws IOException {
    String result = "";
    RequestParams params = RequestParams.create("");
    String source = "";
    String url = "";
    String scriptUrl = "/script/test.js";
    String script = FileUtils.readUtf8String(Objects.requireNonNull(ScriptTest.class.getResource(scriptUrl)).getFile());

    ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_RESULT, result);
    ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_SOURCE, source);
    ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_PARAMS, params);
    ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_URL, url);

//    Object s = ScriptUtils.executeForResult(script, ScriptContext.current());
//    System.out.println(s);
    CommonRule rule = new CommonRule();
    rule.setScript(script);
    Matchers.matchList(Http.get("https://m.biqugeu.net/123_123101/"), rule, ret -> {
      System.out.println(ret);
    });

    ScriptContext.remove();
  }

  @Test
  public void runList() throws IOException {
    String url = "https://m.biqugeu.net/123_123101/";
    String scriptUrl = "/script/test.js";
    String script = FileUtils.readUtf8String(Objects.requireNonNull(ScriptTest.class.getResource(scriptUrl)).getFile());

    ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_URL, url);

    CommonRule rule = new CommonRule();
    rule.setScript(script);
    Matchers.matchList(Http.get("https://m.biqugeu.net/123_123101/"), rule, ret -> {
      System.out.println(ret);
    });
  }
}
