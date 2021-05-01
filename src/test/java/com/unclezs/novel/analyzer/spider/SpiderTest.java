package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.pipline.BasePipeline;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/4/29 21:13
 */
public class SpiderTest {
  @Before
  public void loadRule() throws IOException {
    String ruleConfigs = FileUtils.readUtf8String(NovelMatcherTest.class.getResource("/analyzer/rule.json").getFile());
    RuleHelper.loadRules(ruleConfigs);
  }

  @Test
  public void test() throws IOException {
    String url = "https://www.zhaishuyuan.com/read/34175";
    Spider spider = Spider.create(url)
      .pipeline(new BasePipeline() {
        @Override
        public void process(Chapter chapter) {
          System.out.println(chapter.getName());
          System.out.println(chapter.getContent());
        }
      })
      .rule(RuleHelper.rule(url));
    spider.run();
  }

  @Test
  public void testInterrupt() throws IOException {
    String url = "https://www.zhaishuyuan.com/read/34175";
    Spider spider = Spider.create(url)
      .pipeline(new BasePipeline() {
        @Override
        public void process(Chapter chapter) {
        }
      })
      .rule(RuleHelper.rule(url));
    spider.runAsync();
    ThreadUtils.sleep(2000);
    spider.pause();
    ThreadUtils.sleep(2000);
    spider.runAsync();
    ThreadUtils.sleep(2000);
    spider.pause();
    ThreadUtils.sleep(2000);
    spider.dump("./spider.json");
  }

  @Test
  public void testResumeFromDump() throws IOException, InterruptedException {
    Spider spider = Spider.loadFromFile("./spider.json")
      .pipeline(new BasePipeline() {
        @Override
        public void process(Chapter chapter) {
        }
      });
    spider.runAsync();
    ThreadUtils.sleep(10000);
    spider.dump("./spider.json");
    Thread.currentThread().join();
  }

  @Test
  public void testProgress() throws IOException, InterruptedException {
    Spider spider = Spider.loadFromFile("./spider.json")
      .pipeline(new BasePipeline() {
        @Override
        public void process(Chapter chapter) {
        }
      })
      .progressChangeHandler((d, s) -> {
        System.out.println(d + " - " + s);
      });
    spider.runAsync();
    Thread.currentThread().join();
  }
}
