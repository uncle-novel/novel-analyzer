package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.common.page.AbstractPageable;
import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlEncoder;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author blog.unclezs.com
 * @date 2021/2/12 18:36
 */
@Slf4j
public class SearchSpider extends AbstractPageable<Novel> {
    public static final String BAIDU_SEARCH = "https://www.baidu.com/s?wd=";
    public static final String BAIDU_SEARCH_KEYWORD = "title: (阅读 \"%s\" (最新章节) -(官方网站))";
    public static final String BAIDU_SEARCH_COOKIE_PAGE_SIZE = "SL=0:NR=50:FG=1";
    /**
     * 规则列表
     */
    private final List<AnalyzerRule> rules;
    /**
     * 关键词
     */
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
        boolean hasMore = false;
        for (AnalyzerRule rule : rules) {
            if (!isVisited(rule.getSite())) {
                try {
                    AtomicBoolean siteHasMore = new AtomicBoolean();
                    List<Novel> novels = NovelMatcher.search(page, keyword, rule.getSearch(), novel -> {
                        // 如果已经取消了则不再执行 并且如果有新的则认为还有下一页
                        if (!isCanceled() && addItem(novel)) {
                            siteHasMore.set(true);
                        }
                    });
                    // 如果已经取消了则不再执行
                    if (isCanceled()) {
                        break;
                    }
                    log.trace("站点:{} 搜索第{}页完成 关键词：《{}》，结果数量：{}", rule.getSite(), page, keyword, novels.size());
                    // 如果还有新的 则认为还没有搜索完
                    if (!siteHasMore.get()) {
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
