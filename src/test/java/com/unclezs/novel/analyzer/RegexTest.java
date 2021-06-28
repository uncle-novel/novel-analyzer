package com.unclezs.novel.analyzer;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author blog.unclezs.com
 * @date 2021/6/28 11:10
 */
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
