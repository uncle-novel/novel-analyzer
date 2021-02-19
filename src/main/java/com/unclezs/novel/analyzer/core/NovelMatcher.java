package com.unclezs.novel.analyzer.core;

import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.core.matcher.Matchers;
import com.unclezs.novel.analyzer.core.matcher.matchers.text.DefaultContentMatcher;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.model.DetailRule;
import com.unclezs.novel.analyzer.core.model.SearchRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.RuleConstant;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.spider.helper.SearchHelper;
import com.unclezs.novel.analyzer.spider.helper.SpiderHelper;
import com.unclezs.novel.analyzer.util.BeanUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 小说匹配器
 *
 * @author blog.unclezs.com
 * @date 2021/2/14 14:02
 */
@UtilityClass
public class NovelMatcher {
    /**
     * 获取小说单页正文
     *
     * @param originalText 请求文本
     * @param rule         正文规则
     * @return 返回空则是获取内容失败，否则返回解析后的正文
     */
    public static String content(String originalText, ContentRule rule) {
        if (ContentRule.isEffective(rule)) {
            return Matchers.match(originalText, rule.getContent());
        }
        // 规则不生效则使用默认的
        return DefaultContentMatcher.matching(originalText);
    }

    /**
     * 获取小说单页章节列表
     *
     * @param originalText 源文本
     * @param rule         目录规则
     * @return 单页的章节列表
     */
    public static List<Chapter> toc(String originalText, TocRule rule) {
        if (TocRule.isEffective(rule)) {
            // 子列表规则
            Map<String, CommonRule> childRuleMap = Matchers.getChildMap(rule.getList().getType(), rule, false, "url", "name");
            return Matchers.matchList(originalText, rule.getList(), childRuleMap, Chapter.class);
        } else {
            // 默认的规则
            Document document = Jsoup.parse(originalText);
            List<Element> aTags = document.body().select("a");
            // 章节过滤
            if (rule != null && rule.isFilter()) {
                aTags = AnalyzerHelper.filterImpuritiesElements(aTags);
            }
            return aTags.stream()
                .map(aTag -> new Chapter(aTag.text(), aTag.attr("href")))
                .collect(Collectors.toList());
        }
    }

    /**
     * 小说详情
     *
     * @param originalText 源文本
     * @param rule         规则
     */
    public static Novel details(String originalText, DetailRule rule) {
        Novel novel = new Novel();
        if (rule != null && rule.isEffective()) {
            // 匹配
            Novel matchedNovel = Matchers.matchMultiple(originalText, Matchers.getChildMap(rule), Novel.class);
            if (matchedNovel != null) {
                novel = matchedNovel;
            }
        }
        // 标题自动抓取
        if (StringUtils.isBlank(novel.getTitle())) {
            novel.setTitle(AnalyzerHelper.siteTitle(originalText));
        }
        return novel;
    }

    /**
     * 搜索小说，多页
     *
     * @param keyword 关键词
     */
    public static List<Novel> search(int page, String keyword, SearchRule searchRule, Consumer<Novel> itemHandler) throws IOException {
        List<Novel> novels = new ArrayList<>();
        // 搜索规则有效
        if (searchRule != null && searchRule.isEffective()) {
            RequestParams params = searchRule.getParams().copy();
            String baseUrl = params.getUrl();
            // 预处理请求参数
            SearchHelper.pretreatmentSearchParam(params, page, keyword);
            // 请求网页
            String originalText = SpiderHelper.request(null, params);
            // 列表规则
            CommonRule listRule = searchRule.getList();
            Map<String, CommonRule> childRuleMap = Matchers.getChildMap(listRule.getType(), searchRule.getDetail());
            Matchers.matchList(originalText, listRule, element -> {
                CommonRule detailPageRule = searchRule.getDetailPage();
                try {
                    Novel novel;
                    // 如果自定义了详情页
                    if (CommonRule.hasRule(detailPageRule)) {
                        // 自动保持与list一致
                        detailPageRule.setType(listRule.getType());
                        String detailPageUrl = Matchers.match(element, detailPageRule);
                        // 拼接完整URL
                        detailPageUrl = UrlUtils.completeUrl(baseUrl, detailPageUrl);
                        params.setUrl(detailPageUrl);
                        Object detailElement = SpiderHelper.request(detailPageRule, params);
                        // 通过指定 page= search | detail ，不填写默认为详情页
                        Map<String, CommonRule> detailPage = new HashMap<>(16);
                        Map<String, CommonRule> searchPage = new HashMap<>(16);
                        childRuleMap.forEach((k, v) -> {
                            if (RuleConstant.DETAIL_PAGE.equals(v.getPage())) {
                                detailPage.put(k, v);
                            } else {
                                searchPage.put(k, v);
                            }
                        });
                        // 详情页与搜索页混合匹配
                        novel = Matchers.matchMultiple(element, searchPage, Novel.class);
                        Novel detail = Matchers.matchMultiple(detailElement, detailPage, Novel.class);
                        BeanUtils.copy(detail, novel);
                    } else {
                        // 没有详情页
                        novel = Matchers.matchMultiple(element, childRuleMap, Novel.class);
                    }
                    // 完整拼接URL
                    novel.competeUrl(baseUrl);
                    // 每个结果处理
                    if (itemHandler != null) {
                        itemHandler.accept(novel);
                    }
                    novels.add(novel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return novels;
    }
}
