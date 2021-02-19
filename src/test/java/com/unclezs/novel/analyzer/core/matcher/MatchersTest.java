package com.unclezs.novel.analyzer.core.matcher;

import com.unclezs.novel.analyzer.core.matcher.matchers.XpathMatcher;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @author blog.unclezs.com
 * @since 2020/12/21 15:48
 */
@Slf4j
public class MatchersTest {
    private String originalText = StringUtils.EMPTY;

    @Before
    public void initContent() throws IOException {
        URL resource = MatchersTest.class.getResource("/chapters.txt");
        originalText = FileUtils.readUtf8String(resource.getFile());
    }

    @Test
    public void testJsonPath() {
        log.trace("matched:{}", Matchers.match(originalText, "json:$.headers['X-Amzn-Trace-Id']"));
    }

    @Test
    public void testXpath() {
        Chapter chapter = new Chapter();
        chapter.setUrl("@href");
        chapter.setName("text()");
        String listRule = "xpath://div/ul/li/a";
//        List<Chapter> chapters = Matchers.matchList(originalText, listRule,  chapter);
//        chapters.forEach(System.out::println);
    }

    @Test
    public void testCSS() {
        Chapter chapter = new Chapter();
        chapter.setUrl("a@href");
        chapter.setName("a@text");
        String listRule = "css:#content-list > div.book-list.clearfix > ul > li";
//        List<Chapter> chapters = Matchers.matchList(originalText, listRule,  chapter);
//        chapters.forEach(System.out::println);
    }

    @Test
    public void testContent() throws IOException {
        System.out.println(XpathMatcher.me().match(originalText, "//*[@id='content']/allText()"));
    }

    @Test
    public void testJudgeStrType() {
        String s = "5285099.html";
        int ret = 0;
        if (s.matches(".*?\\d.*?")) {
            ret = 1;
        }
        if (s.matches(".*?[a-zA-Z].*?")) {
            ret += 2;
        }
        System.out.println(ret);
    }
}
