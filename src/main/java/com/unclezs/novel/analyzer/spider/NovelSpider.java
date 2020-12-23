package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.core.AnalyzerHelper;
import com.unclezs.novel.analyzer.matcher.RegexMatcher;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;
import com.unclezs.novel.analyzer.spider.model.BaseNovelInfo;
import com.unclezs.novel.analyzer.spider.pipline.Pipeline;
import com.unclezs.novel.analyzer.utils.uri.UrlUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:15 下午
 */
public abstract class NovelSpider {
    /**
     * 搜索小说
     *
     * @param requestData /
     * @return 结果列表
     */
    public abstract List<BaseNovelInfo> search(RequestData requestData);

    /**
     * 获取小说正文
     *
     * @param requestData 请求
     * @return /
     * @throws IOException /
     */
    public String content(RequestData requestData) throws IOException {
        return content(requestData, null);
    }

    /**
     * 获取小说正文
     *
     * @param requestData 请求
     * @param pipeline    数据处理管道
     * @return /
     * @throws IOException /
     */
    public abstract String content(RequestData requestData, Pipeline<String> pipeline) throws IOException;

    /**
     * 获取小说章节列表
     *
     * @param requestData 请求
     * @return /
     * @throws IOException /
     */
    public List<Chapter> chapters(RequestData requestData) throws IOException {
        return chapters(requestData, null);
    }

    /**
     * 获取小说章节列表
     *
     * @param requestData 请求
     * @param pipeline    数据处理管道
     * @return /
     * @throws IOException /
     */
    public abstract List<Chapter> chapters(RequestData requestData, Pipeline<List<Chapter>> pipeline)
        throws IOException;

    /**
     * 获取小说章节列表
     *
     * @param requestData 请求
     * @return /
     * @throws IOException /
     */
    public List<Chapter> crawling(RequestData requestData) throws IOException {
        return crawling(requestData, null);
    }

    /**
     * 爬取一本小说
     *
     * @param requestData 请求
     * @param pipeline    数据处理管道
     * @return /
     * @throws IOException /
     */
    public abstract <T> T crawling(RequestData requestData, Pipeline<T> pipeline) throws IOException;


    /**
     * 多页爬取
     *
     * @param requestData  请求数据
     * @param pageLinks    已经爬取了的页面链接
     * @param title        页面标题
     * @param nextPageRule 下一页规则
     * @param baseUri      用于拼接完整URI
     * @param consumer     处理下一页html的处理器
     * @throws IOException /
     */
    public void multiPage(RequestData requestData, Set<String> pageLinks, String title, String nextPageRule,
        String baseUri, Consumer<String> consumer)
        throws IOException {
        String page;
        // 如果下一页存在并且没有被抓取过（防止重复抓取的情况）
        while (UrlUtil.isHttpUrl(requestData.getUrl()) && !pageLinks.contains(requestData.getUrl())) {
            // 已经爬取过
            pageLinks.add(requestData.getUrl());
            // 获取网页内容
            page = Http.content(requestData);
            // 获取网页标题 只留下了中文（不包含零到十）
            String pageTitle = RegexMatcher.titleWithNotNumber(page);
            // 通过标题是否一样来判断是否有下一页
            if (!pageTitle.equals(title)) {
                pageLinks.remove(requestData.getUrl());
                break;
            }
            consumer.accept(page);
            // 继续翻页
            requestData.setUrl(AnalyzerHelper.nextPage(page, nextPageRule, baseUri));
        }
    }

    /**
     * 执行pipeline
     *
     * @param pipeline /
     * @param data     数据
     * @param <T>      数据类型
     */
    public <T> void processPipeline(Pipeline<T> pipeline, T data) {
        if (pipeline != null) {
            pipeline.process(data);
        }
    }
}
