package com.unclezs.novel.core.spider;

import com.unclezs.novel.core.AnalyzerManager;
import com.unclezs.novel.core.analyzer.AnalyzerHelper;
import com.unclezs.novel.core.concurrent.pool.ThreadPoolUtil;
import com.unclezs.novel.core.matcher.RegexMatcher;
import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.model.Novel;
import com.unclezs.novel.core.request.Http;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.spider.pipline.Pipeline;
import com.unclezs.novel.core.util.StringUtils;
import com.unclezs.novel.core.util.uri.UrlUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
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
     * 单线程爬取一本小说
     *
     * @param requestData     请求
     * @param chapterPipeline 章节数据处理管道
     * @return 抓取错误的章节列表
     * @throws IOException 目录地址爬取失败
     */
    public List<Chapter> crawling(RequestData requestData, Pipeline<Chapter> chapterPipeline) throws IOException {
        return crawling(chapters(requestData), chapterPipeline, 1);
    }


    /**
     * 指定线程数量爬取一本小说
     *
     * @param requestData     请求
     * @param chapterPipeline 章节数据处理管道
     * @param threadNum       线程数量
     * @return 抓取错误的章节列表
     * @throws IOException 目录地址爬取失败
     */
    public List<Chapter> crawling(RequestData requestData, Pipeline<Chapter> chapterPipeline, int threadNum) throws IOException {
        return crawling(chapters(requestData), chapterPipeline, threadNum);
    }

    /**
     * 爬取一本小说
     *
     * @param chapterPipeline 数据处理管道，传入爬取的每一个章节
     * @param chapters        章节数据
     * @param threadNum       线程数量
     * @return 抓取错误的章节列表
     */
    public List<Chapter> crawling(List<Chapter> chapters, Pipeline<Chapter> chapterPipeline, int threadNum) {
        if (chapters == null || chapters.isEmpty() || chapterPipeline == null) {
            log.warn("缺少关键参数");
            throw new IllegalArgumentException("缺少关键参数");
        }
        if (threadNum < 1) {
            log.debug("线程数量小于1，自动重置为1");
            threadNum = 1;
        }
        if (!AnalyzerManager.startTask()) {
            log.warn("任务数量已满");
            throw new IllegalStateException("任务数量已满，请自行控制任务调度，挂起等待或是丢弃");
        }
        // 局部变量方式创建线程池 没有必要长期存在仅仅用于一个爬取任务
        ThreadPoolExecutor threadPool = ThreadPoolUtil.newFixedThreadPoolExecutor(threadNum, "chapter-spider");
        log.debug("开始爬取小说：共{}章 开启{}线程 是否启用自动代理：{}", chapters.size(), threadNum, AnalyzerManager.me().isAutoProxy());
        List<Chapter> failedChapters = new ArrayList<>();
        CountDownLatch counter = new CountDownLatch(chapters.size());
        int order = 1;
        for (Chapter chapter : chapters) {
            // 章节顺序编号
            chapter.setOrder(order++);
            threadPool.execute(() -> {
                try {
                    String content = content(RequestData.defaultRequestData(chapter.getUrl()));
                    // 内容是空白也当做错误处理
                    if (StringUtils.isBlank(content)) {
                        throw new IOException("未知的，未抓取的章节内容");
                    }
                    chapter.setContent(content);
                    chapterPipeline.process(chapter);
                } catch (IOException e) {
                    chapter.setMsg(e.getMessage());
                    failedChapters.add(chapter);
                    log.warn("小说内容爬取失败：order:{} - {} - {}", chapter.getOrder(), chapter.getName(), chapter.getUrl(), e);
                } finally {
                    counter.countDown();
                }
            });
        }
        try {
            // 关闭线程池，禁止提交新的任务
            threadPool.shutdown();
            // 等待章节爬取完成
            counter.await();
        } catch (InterruptedException e) {
            log.warn("小说爬取中 被中断", e);
        } finally {
            // 总控任务数量减少1
            AnalyzerManager.finishedTask();
        }
        log.debug("爬取小说完成：共{}章", chapters.size());
        return failedChapters;
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
        while (UrlUtils.isHttpUrl(requestData.getUrl()) && !pageLinks.contains(requestData.getUrl())) {
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
