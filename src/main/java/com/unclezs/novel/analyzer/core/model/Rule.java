package com.unclezs.novel.analyzer.core.model;

import com.unclezs.novel.analyzer.core.text.matcher.RegexContentMatcher;
import com.unclezs.novel.analyzer.core.text.matcher.StrictRegexContentMatcher;
import com.unclezs.novel.analyzer.core.text.matcher.TagContentMatcher;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:44 下午
 */
public enum Rule {
  /**
   * JSON解析
   */
  JSON(false) {
    @Override
    public String matching(String originalText) {
      throw new UnsupportedOperationException("不支持JSON");
    }
  },
  /**
   * 宽松模式正则
   */
  TEXT_REGEX(true) {
    @Override
    public String matching(String originalText) {
      return RegexContentMatcher.matching(originalText);
    }
  },
  /**
   * 严格模式正则
   */
  TEXT_REGEX_STRICT(true) {
    @Override
    public String matching(String originalText) {
      return StrictRegexContentMatcher.matching(originalText);
    }
  },
  /**
   * 通过TAG分析出包含最多文本的标签 而取得正文内容(推荐)
   */
  TEXT_TAG(false) {
    @Override
    public String matching(String originalText) {
      return TagContentMatcher.matching(originalText);
    }
  };

  Rule(boolean supportRange) {
    this.supportRange = supportRange;
  }

  private final boolean supportRange;

  public boolean isSupportRange() {
    return supportRange;
  }

  /**
   * 是否支持正则
   *
   * @return /
   */
  public abstract String matching(String originalText);
}