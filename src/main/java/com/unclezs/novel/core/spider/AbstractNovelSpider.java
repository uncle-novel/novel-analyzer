package com.unclezs.novel.core.spider;

import com.unclezs.novel.core.AnalyzerManager;
import com.unclezs.novel.core.analyzer.AnalyzerHelper;
import com.unclezs.novel.core.concurrent.pool.ThreadPool;
import com.unclezs.novel.core.concurrent.pool.ThreadPoolUtil;
import com.unclezs.novel.core.matcher.RegexMatcher;
import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.model.Novel;
import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.spider.pipline.Pipeline;
import com.unclezs.novel.core.utils.uri.UrlUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 小说爬虫
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:15 下午
 */
@Slf4j
public abstract class AbstractNovelSpider {
    /**
     * 搜索小说
     *
     * @param requestData /
     * @return 结果列表
     */
    public abstract List<Novel> search(RequestData requestData);

    /**
     * 获取小说正文
     *
     * @param requestData 请求
     * @return /
     * @throws IOException /
     */
    public abstract String content(RequestData requestData) throws IOException;

    /**
     * 获取小说正文
     *
     * @param requestData 请求
     * @param pipeline    数据处理管道
     * @return /
     * @throws IOException /
     */
    public String content(RequestData requestData, Pipeline<String> pipeline) throws IOException {
        String content = content(requestData);
        processPipeline(pipeline, content);
        return content;
    }

    /**
     * 获取小说章节列表
     *
     * @param requestData 请求
     * @return /
     * @throws IOException /
     */
    public abstract List<Chapter> chapters(RequestData requestData) throws IOException;

    /**
     * 获取小说章节列表
     *
     * @param requestData 请求
     * @param pipeline    数据处理管道
     * @return /
     * @throws IOException /
     */
    public List<Chapter> chapters(RequestData requestData, Pipeline<List<Chapter>> pipeline)
        throws IOException {
        List<Chapter> chapters = chapters(requestData);
        processPipeline(pipeline, chapters);
        return chapters;
    }

    /**
     * 爬取一本小说
     *
     * @param requestData 请求
     * @param pipeline    数据处理管道
     * @throws IOException 目录地址爬取失败
     */
    public void crawling(RequestData requestData, Pipeline<Chapter> pipeline) throws IOException {
        crawling(chapters(requestData), pipeline);
    }


    /**
     * 爬取一本小说
     *
     * @param pipeline 数据处理管道，传入爬取的每一个章节
     */
    public void crawling(List<Chapter> chapters, Pipeline<Chapter> pipeline) {
        ThreadPool threadPool =
            ThreadPoolUtil.newFixedThreadPoolExecutor(AnalyzerManager.me().getThreadNum(), "chapter-spider");
        log.debug("开始爬取小说：共{}章", chapters.size());
        AtomicInteger order = new AtomicInteger(1);
        for (Chapter chapter : chapters) {
            threadPool.execute(() -> {
                String content = null;
                try {
                    content = content(RequestData.defaultRequestData(chapter.getUrl()));
                } catch (IOException e) {
                    log.warn("小说正文爬取失败：order:{} - {} - {}", order.get(), chapter.getName(), chapter.getName(), e);
                }
                chapter.setContent(content);
                chapter.setOrder(order.getAndIncrement());
                pipeline.process(chapter);
            });
        }
        threadPool.waitCompeted(chapters.size(), true);
        log.debug("爬取小说完成：共{}章", chapters.size());
    }

    /**
     * 多页爬取
     *
     * @param requestData  请求数据
     * @param pageLinks    已经爬取了的页面链接
     * @param title        页面标题
     * @param nextPageRule 下一页规则
     * @param consumer     处理下一页html的处理器
     * @throws IOException /
     */
    public void multiPage(RequestData requestData, Set<String> pageLinks, String title, String nextPageRule, Consumer<String> consumer)
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
            requestData.setUrl(AnalyzerHelper.nextPage(page, nextPageRule, requestData.getUrl()));
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
