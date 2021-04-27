package com.unclezs.novel.analyzer.script;

import com.unclezs.novel.analyzer.core.matcher.Matchers;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
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
    CommonRule rule = new CommonRule();
    rule.setScript(FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\test\\resources\\script\\yousxs.js"));
    String match = Matchers.match(content, rule);
    System.out.println(match);
  }
}
