package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.core.text.TextNovelAnalyzer;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:17 下午
 */
public class TextNovelSpider {
  /**
   * 配置信息
   */
  private TextAnalyzerConfig config;

  public String content(RequestData requestData) throws IOException {
    String nextPage = requestData.getUrl();
    String originalText;
    if (config.getNextPage().isEmpty()) {
      StringBuilder pages = new StringBuilder();
      while (nextPage != null) {
        String page = Http.content(requestData);
        pages.append(TextNovelAnalyzer.content(page, config));
      }

    } else {

    }
    return null;
  }

}
