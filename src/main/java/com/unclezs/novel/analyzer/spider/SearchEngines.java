package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlEncoder;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @since 2021/4/16 12:26
 */
public class SearchEngines {
  public static final String BAIDU_SEARCH = "https://www.baidu.com/s?wd=";
  public static final String BAIDU_SEARCH_KEYWORD = "title: (阅读 \"%s\" (最新章节) -(官方网站))";
  public static final String BAIDU_SEARCH_COOKIE_PAGE_SIZE = "SL=0:NR=50:FG=1";

  /**
   * 默认搜索 采用百度搜索
   *
   * @param keyword 关键词
   * @return 结果列表
   */
  public List<Novel> searchBaidu(String keyword) {
    String url = BAIDU_SEARCH + UrlEncoder.encode(String.format(BAIDU_SEARCH_KEYWORD, keyword));
    System.out.println(url);
    RequestParams params = RequestParams.create(url);
    params.addHeader(RequestParams.COOKIE, BAIDU_SEARCH_COOKIE_PAGE_SIZE);
    String content = null;
    try {
      content = Http.content(params);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<JXNode> nodes = JXDocument.create(content).selN("//h3[@class='t']/a");
    List<Novel> novels = new ArrayList<>(nodes.size() * 2);
    for (JXNode node : nodes) {
      // 获取标题和链接
      String href = node.selOne("@href").asString();
      href = UrlUtils.getRedirectUrl(href);
      String title = node.selOne("allText()").asString().replace("...", StringUtils.EMPTY);
      // 封装结果
      Novel novel = new Novel();
      novel.setUrl(href);
      novel.setTitle(title);
      novels.add(novel);
    }
    return novels;
  }
}
