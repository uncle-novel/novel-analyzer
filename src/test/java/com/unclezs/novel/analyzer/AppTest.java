package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/6/26 21:27
 */
public class AppTest {

  @Test
  public void test() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    System.out.println(RuleHelper.GSON.toJson(RuleHelper.rules()));
  }
}
