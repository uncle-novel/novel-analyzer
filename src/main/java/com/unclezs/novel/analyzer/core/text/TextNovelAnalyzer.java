package com.unclezs.novel.analyzer.core.text;

import com.unclezs.novel.analyzer.core.AnalyzerHelper;
import com.unclezs.novel.analyzer.core.comparator.ChapterComparator;
import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.utils.CollectionUtil;
import com.unclezs.novel.analyzer.utils.StringUtil;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文本小说解析器
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:42 下午
 */
@UtilityClass
public class TextNovelAnalyzer {

    /**
     * 获取小说正文
     *
     * @param originalText html/json
     * @param config       解析配置
     * @return /
     */
    public String content(String originalText, TextAnalyzerConfig config) {
        // 自定义范围
        Rule rule = config.getRule();
        // 支持范围匹配 则进行范围截取
        if (rule.isSupportRange()) {
            originalText = StringUtil.getRange(config.getRangeHeader(), config.getRangeTail(), originalText);
        }
        // 匹配正文
        String content = rule.matching(originalText);
        // 缩进处理 每段缩进4个空格
        content = StringUtil.indentation(content);
        // html空格处理
        content = StringUtil.htmlBlank(content);
        // ncr转中文
        if (config.isNcr()) {
            content = StringUtil.ncr2Chinese(content);
        }
        // 去广告
        if (CollectionUtil.isNotEmpty(config.getAdvertisements())) {
            content = StringUtil.remove(content, config.getAdvertisements().toArray(new String[0]));
        }
        return content;
    }

    /**
     * 获取小说章节列表
     *
     * @param originalText html/json
     * @param config       解析配置
     * @return /
     */
    public List<Chapter> chapters(String originalText, TextAnalyzerConfig config) {
        // 支持范围匹配 则进行范围截取
        if (config.getRule().isSupportRange()) {
            originalText = StringUtil.getRange(config.getRangeHeader(), config.getRangeTail(), originalText);
        }
        Document document = Jsoup.parse(originalText, config.getBaseUri());
        List<Element> elements = document.body().select("a");
        //章节过滤
        if (config.isChapterFilter()) {
            elements = AnalyzerHelper.filterImpuritiesLinks(elements);
        }
        Stream<Chapter> chapterStream = elements.stream()
            .map(a -> new Chapter(a.text(), a.absUrl("href")))
            // 去重
            .distinct();
        //乱序重排
        if (config.isChapterSort()) {
            chapterStream = chapterStream.sorted(new ChapterComparator());
        }
        return chapterStream.collect(Collectors.toList());
    }

}
