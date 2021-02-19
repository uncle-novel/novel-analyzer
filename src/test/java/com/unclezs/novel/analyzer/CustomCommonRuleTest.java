package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.spider.NovelMatcherTest;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/01/27 19:06
 */
public class CustomCommonRuleTest {

    @Test
    public void test() {
        String rule = "匹配规则 | 广告##模板 | js:";
        String byTemplate = RegexUtils.findByTemplate("(xpath:){0,1}(.+)", rule, "$2");
        System.out.println(byTemplate);
    }

    @Test
    public void testRuleInLine() {
        String rule = "xpath://xxx/div[@class='xx']";
        System.out.println(RuleHelper.parseRuleType(rule));
    }

    @Test
    public void testCommonRule() throws IOException {
        String configJson = FileUtils.readUtf8String(NovelMatcherTest.class.getResource("/common_rule.json").getFile());
        CommonRule commonRule = RuleHelper.parseRule(configJson, CommonRule.class);
        System.out.println(commonRule);
    }
}
