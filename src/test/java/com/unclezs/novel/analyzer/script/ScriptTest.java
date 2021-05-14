package com.unclezs.novel.analyzer.script;

import com.unclezs.novel.analyzer.core.matcher.Matchers;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/01/29 10:38
 */
public class ScriptTest {
  @Test
  public void testRequestParams() throws IOException {
    String content = FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\main\\resources\\test");
    CommonRule rule = CommonRule.create("//*[@id=\"play\"]/@src");
    rule.setScript(FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\test\\resources\\script\\28.js"));
    String match = Matchers.match(content, rule);
    System.out.println(match);
  }

  @Test
  public void match() throws IOException {
    String content = FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\main\\resources\\test");
    String match = Matchers.match(content, "regex:url\\d+? = '(http.+?)'##$1");
    System.out.println(match);
  }

  @Test
  public void req() throws IOException {
    String url = "https://m.28ts.com/mp3/1651/1.html";
    String content = Http.get(url);
    CommonRule rule = CommonRule.create("//*[@id=\"play\"]/@src");
    rule.setScript(FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\test\\resources\\script\\28.js"));
    String match = Matchers.match(content, rule);
    System.out.println(match);
  }
}
