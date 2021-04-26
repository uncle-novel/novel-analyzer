package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import org.junit.Test;

/**
 * @author blog.unclezs.com
 * @date 2020/12/22 15:05
 */
public class AppTest {
  public int cnt = 1;

  @Test
  public void test() {
    String s = GsonUtils.PRETTY.toJson(new AnalyzerRule());
    System.out.println(s);
  }
}
