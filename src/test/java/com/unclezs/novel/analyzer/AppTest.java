package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.helper.RuleTester;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2020/12/22 15:05
 */
public class AppTest {
  public int cnt = 1;

  @Test
  public void test() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("G:\\coder\\self-coder\\novel-analyzer\\src\\main\\resources\\analyzer\\rule.json"));
//    AnalyzerRule rule = RuleHelper.getRule("https://www.yousxs.com/player/11743_1.html");
    AnalyzerRule rule = RuleHelper.getRule("https://www.zhaishuyuan.com/read/7585");
    RuleTester tester = new RuleTester(rule);
    tester.search("完美世界");
  }
}
