package com.unclezs.novel.analyzer.core.comparator;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * 章节排序比较器
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 17:54
 */
public class ChapterComparator implements Comparator<Chapter> {
  @Override
  public int compare(Chapter o1, Chapter o2) {
    String one = UrlUtils.getUrlLastPathNotSuffix(o1.getUrl());
    String two = UrlUtils.getUrlLastPathNotSuffix(o2.getUrl());
    if (RegexUtils.isNumber(one) && RegexUtils.isNumber(two)) {
      BigInteger v1 = new BigInteger(one);
      BigInteger v2 = new BigInteger(two);
      return v1.compareTo(v2);
    }
    return 0;
  }
}

