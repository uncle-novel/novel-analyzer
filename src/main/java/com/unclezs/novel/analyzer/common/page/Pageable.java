package com.unclezs.novel.analyzer.common.page;

import java.io.IOException;

/**
 * 可以翻页的
 *
 * @author blog.unclezs.com
 * @date 2021/2/12 14:26
 */
public interface Pageable {
  /**
   * 加载更多
   */
  void loadMore() throws IOException;
}
