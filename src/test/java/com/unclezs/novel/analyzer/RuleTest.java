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
    System.setProperty("http.proxyPort", "1081");
    System.setProperty("https.proxyHost", "127.0.0.1");
    System.setProperty("https.proxyPort", "1081");
  }

  @Test
  public void test() {
    AnalyzerRule rule = RuleHelper.rules().get(0);
    RuleTester tester = new RuleTester(rule);
    tester.setShowRule(false);
    tester.setShowSource(false);
    tester.setShowAllData(true);
    tester.search("完美");
  }

  @Test
  public void testToc() {
    AnalyzerRule rule = RuleHelper.rules().get(0);
    RuleTester tester = new RuleTester(rule);
    tester.setShowRule(false);
    tester.setShowSource(false);
    tester.setShowAllData(true);
    tester.toc("https://www.myhtebooks.com/?act=showinfo&bookwritercode=EB20150423223134241467&bookid=2834&pavilionid=a");
  }

  @Test
  public void testContent() {
    AnalyzerRule rule = RuleHelper.rules().get(0);
    RuleTester tester = new RuleTester(rule);
    tester.setShowRule(true);
    tester.setShowSource(false);
    tester.setShowAllData(false);
    tester.content("https://www.myhtebooks.com/?act=showpaper&paperid=54165");
  }
}
