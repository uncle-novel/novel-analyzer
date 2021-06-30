package com.unclezs.novel.analyzer;

import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/6/26 21:27
 */
public class AppTest {

  @Test
  public void test() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
    Object o = RuleHelper.GSON.fromJson(FileUtils.readUtf8String("rule.json"), new TypeToken<List<AnalyzerRule>>() {
    }.getType());
    System.out.println(o);
//    System.out.println(RuleHelper.GSON.toJson(RuleHelper.rules()));
//    System.out.println(GsonUtils.PRETTY.toJson(RuleHelper.rules()));
    System.out.println(RuleHelper.GSON.toJson(RuleHelper.rules()));
  }
}
