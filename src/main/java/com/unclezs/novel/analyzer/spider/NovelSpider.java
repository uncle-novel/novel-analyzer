package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.request.RequestData;
import com.unclezs.novel.analyzer.spider.model.BaseNovelInfo;
import com.unclezs.novel.analyzer.spider.model.Chapter;
import com.unclezs.novel.analyzer.spider.rule.BaseRule;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:15 下午
 */
public interface NovelSpider {
    /**
     * 搜索小说
     *
     * @param requestData /
     * @return 结果列表
     */
    List<BaseNovelInfo> search(RequestData requestData);

    /**
     * 获取小说正文
     *
     * @param content html获取json
     * @param rule    规则
     * @return /
     */
    String content(String content, BaseRule rule);

    /**
     * 获取小说章节列表
     *
     * @param content /
     * @param rule    /
     * @return /
     */
    List<Chapter> chapters(String content, BaseRule rule);
}
