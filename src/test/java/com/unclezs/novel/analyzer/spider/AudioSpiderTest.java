package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.core.matcher.MatchersTest;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.spider.pipline.MediaFilePipeline;
import com.unclezs.novel.analyzer.spider.pipline.TxtFilePipeline;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @since 2021/2/3 22:10
 */
public class AudioSpiderTest {
    public static void main(String[] args) throws IOException {
        AnalyzerRule config = new AnalyzerRule();
        CommonRule contentRule = new CommonRule();
        contentRule.setType("regex");
        contentRule.setScript(FileUtils.readUtf8String(MatchersTest.class.getResource("/script/520tingshu.js").getFile()));
        Spider.create("http://www.520tingshu.com/book/book17837.html")
            .pipeline(new MediaFilePipeline())
            .progressChangeHandler((progress, text) -> System.out.println(text))
            .analyzerRule(config)
            .run();
    }

    @Test
    public void testTxt() throws IOException {
        Spider.create("https://m.jx.la/book/394/index_91.html")
            .pipeline(new TxtFilePipeline())
            .progressChangeHandler((progress, text) -> System.out.println(text))
            .run();
    }

}
