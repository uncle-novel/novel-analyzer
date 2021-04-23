package com.unclezs.novel.analyzer.gson;

import com.google.gson.Gson;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import org.junit.Test;

import java.util.Collections;

/**
 * @author blog.unclezs.com
 * @date 2021/1/17 12:09
 */
public class GsonTest {
  @Test
  public void test() {
    MyJsonObj obj = new MyJsonObj();
    MyObj obj1 = new MyObj();
    obj1.setName("uncle");
    obj.setList(Collections.singletonList(obj1));
    Gson gson = new Gson();
    String x = gson.toJson(obj);
    System.out.println(x);
    MyJsonObj obj2 = gson.fromJson(x, MyJsonObj.class);
    System.out.println(gson.toJson(obj2));
  }

  @Test
  public void testAnalyzerConfig() {
    CommonRule rule = new CommonRule();
    rule.setRule("dasdasda");
    System.out.println(rule.ruleString());
    rule.setRuleString("123");
    System.out.println(rule);
  }

  @Test
  public void testDefault() {
    RequestParams requestParams = GsonUtils.parse("{'url': '12312313123'}", RequestParams.class);
    System.out.println(requestParams);
  }
}
