package com.unclezs.novel.analyzer.matcher;

import com.unclezs.novel.analyzer.utils.regex.ReUtil;
import lombok.experimental.UtilityClass;

/**
 *
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/21 11:39
 */
@UtilityClass
public class RegexMatcher {
  /**
   * 正则匹配
   *
   * @param src   源
   * @param regex 正则
   * @param index 组
   * @return /
   */
  public String matcher(String src, String regex, int index) {
    String ret = ReUtil.get(regex, src, index);
    return ret == null ? "" : ret;
  }
}
