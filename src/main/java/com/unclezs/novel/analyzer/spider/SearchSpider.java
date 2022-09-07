package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.common.page.AbstractPageable;
import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.helper.DebugHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author blog.unclezs.com
 * @since 2021/2/12 18:36
 */
@Slf4j
public class SearchSpider extends AbstractPageable<Novel> {
  /**
   * 规则列表
   */
  private final List<AnalyzerRule> rules;
  /**
   * 一页抓取完成后的回调
   */
  @Setter
  private Consumer<List<Novel>> pageConsumer;
  /**
   * 关键词
   */
  @Getter
  private String keyword;

  public SearchSpider(List<AnalyzerRule> rules) {
    this.rules = rules;
  }

  /**
   * 搜索
   *
   * @param keyword 关键词
   */
  public void search(String keyword) throws IOException {
    this.keyword = keyword;
    super.firstLoad();
  }

  /**
   * 获取数据项的唯一ID
   *
   * @param item 数据项
   * @return 唯一标识
   */
  @Override
  protected String getUniqueId(Novel item) {
    return item.getUrl();
  }

  /**
   * 加载一页数据
   *
   * @param page 下一页页码
   * @return 是否已经搜索完成
   */
  @Override
  protected boolean loadPage(int page) {
    DebugHelper.debug("【搜索】：正在搜索第{}页", page);
    boolean hasMore = false;
    for (AnalyzerRule rule : rules) {
      if (!isVisited(rule.getSite())) {
        try {
          AtomicBoolean siteHasMore = new AtomicBoolean();
          List<Novel> novels = NovelMatcher.search(page, keyword, rule, novel -> {
            // 如果已经取消了则不再执行 并且如果有新的则认为还有下一页
            if (!isCanceled()) {
              // 调试模式不校验标题有效
              if (DebugHelper.enabled() && StringUtils.isBlank(novel.getTitle())) {
                return;
              }
              if (addItem(novel)) {
                siteHasMore.set(true);
              }
            }
          });
          // 如果已经取消了则不再执行
          if (isCanceled()) {
            break;
          }
          // 如果还有新的 则认为还没有搜索完
          if (siteHasMore.get()) {
            log.trace("站点:{} 搜索第{}页完成 关键词：《{}》，结果数量：{}", rule.getSite(), page, keyword, novels.size());
            if (pageConsumer != null) {
              pageConsumer.accept(novels);
            }
            hasMore = true;
          } else {
            // 没有新的小说 则认为这个书源已经被搜索完了
            addVisited(rule.getSite());
            log.trace("站点:{} 搜索结束 关键词：《{}》，共{}页", rule.getSite(), keyword, page - 1);
          }
        } catch (IOException e) {
          // 请求异常 则认为这个书源已经被搜索完了
          addVisited(rule.getSite());
          log.warn("搜索失败,page={},keyword={}，搜索规则：{}", page, keyword, rule.getSearch(), e);
        }
      }
    }
    return hasMore;
  }
}
