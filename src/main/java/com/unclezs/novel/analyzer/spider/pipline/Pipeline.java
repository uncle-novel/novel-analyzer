package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;

/**
 * 数据处理管道 用户爬取完成之后的数据处理
 *
 * @author blog.unclezs.com
 * @since 2020/12/23 15:51
 */
public interface Pipeline {
    /**
     * 注入小说信息
     *
     * @param novel 小说信息
     */
    void injectNovel(Novel novel);

    /**
     * 处理数据
     *
     * @param chapter 数据
     */
    void process(Chapter chapter);
}
