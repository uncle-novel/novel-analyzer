package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * @author blog.unclezs.com
 * @since 2021/6/28 11:10
 */
@Ignore
public class RegexTest {

  private String regex;

  @Before
  public void init() throws IOException {
    String url = "/test.txt";
    regex = FileUtils.readUtf8String(Objects.requireNonNull(ScriptTest.class.getResource(url)).getFile());
  }

  @Test
  public void test() {
    String rule = regex.replace("\\\\", "\\").replace("\\", "\\\\");
    System.out.println(rule);
  }
}
