package com.unclezs.novel.analyzer.matcher;

import lombok.experimental.UtilityClass;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.util.Objects;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/21 16:20
 */
@UtilityClass
public class XpathMatcher {
  /**
   * XPath匹配
   *
   * @param src  /
   * @param rule /
   * @return /
   */
  public String matching(String src, String rule) {
    JXNode ret = JXDocument.create(src).selNOne(rule);
    if (ret != null) {
      return Objects.toString(ret);
    }
    return "";
  }
}
