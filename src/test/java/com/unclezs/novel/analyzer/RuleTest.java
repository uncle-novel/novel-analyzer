package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.helper.RuleTester;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/07/06
 */
public class RuleTest {

  @Before
  public void init() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    System.setProperty("http.proxyHost", "127.0.0.1");
    System.setProperty("http.proxyPort", "1087");
    System.setProperty("https.proxyHost", "127.0.0.1");
    System.setProperty("https.proxyPort", "1087");
  }

  @Test
  public void test() {
    AnalyzerRule rule = RuleHelper.rules().get(0);
    RuleTester tester = new RuleTester(rule);
    tester.setShowRule(false);
    tester.search("完美");
  }
}
