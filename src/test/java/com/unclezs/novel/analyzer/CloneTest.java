package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import org.junit.Test;

/**
 * @author blog.unclezs.com
 * @date 2021/2/15 10:29
 */
public class CloneTest {
    @Test
    public void test() {
        AnalyzerRule analyzerRule = new AnalyzerRule();
        TocRule toc = new TocRule();
        analyzerRule.setToc(toc);
        toc.setList(CommonRule.create("xpath:sdasdasdasd"));
        System.out.println(toc.getList());
        AnalyzerRule rule = SerializationUtils.deepClone(analyzerRule);
        rule.getToc().setList(CommonRule.create("xpath:123"));
        System.out.println(toc.getList());
    }
}
