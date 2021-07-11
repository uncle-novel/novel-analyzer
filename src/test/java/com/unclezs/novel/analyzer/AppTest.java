package com.unclezs.novel.analyzer;

import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.matcher.Matchers;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/6/26 21:27
 */
@Ignore
public class AppTest {

  @Before
  public void init() throws IOException {
    RuleHelper.loadRules(FileUtils.readUtf8String("rule.json"));
  }


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

  @Test
  public void testMatcher() {
    String match = Matchers.match("{\n" +
      "    \"name\": \"BeJson\",\n" +
      "    \"url\": \"http://www.bejson.com\",\n" +
      "    \"page\": 88,\n" +
      "    \"isNonProfit\": true,\n" +
      "    \"address\": {\n" +
      "        \"street\": \"科技园路.\",\n" +
      "        \"city\": \"江苏苏州\",\n" +
      "        \"country\": \"中国\"\n" +
      "    },\n" +
      "    \"links\": null\n" +
      "}", "$.links");
    System.out.println(match);
  }
}
