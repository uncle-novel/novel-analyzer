package com.unclezs.novel.analyzer.core.helper;

import com.unclezs.novel.analyzer.core.matcher.Matchers;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 小说解析器辅助类
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:42 下午
 */
@UtilityClass
public class AnalyzerHelper {
    /**
     * 变为绝对路径
     *
     * @param baseUrl baseUrl
     * @param getter  字段的getter
     * @param setter  字段的setter
     */
    public static void completeUrl(String baseUrl, Supplier<String> getter, Consumer<String> setter) {
        String url = getter.get();
        if (StringUtils.isNotBlank(url)) {
            setter.accept(UrlUtils.completeUrl(baseUrl, url));
        }
    }

    /**
     * 重命名章节
     *
     * @param name  章节
     * @param order 章节序号
     * @return 重名名结果
     */
    public static String renameChapter(String name, int order) {
        name = StringUtils.remove(name, "[0-9]", "第.*?章");
        return String.format("第%s章 %s", order, name);
    }

    /**
     * 获取小说网站的title提取标题
     *
     * @param originalText 源文本
     * @return 小说网站标题
     */
    public String siteTitle(String originalText) {
        // title标签中文本
        String titleText = RegexMatcher.me().title(originalText);
        String title = RegexMatcher.me().match(titleText, "(.{1,10}?)最新##$1");
        // 获取不到精确标题
        if (StringUtils.isEmpty(title)) {
            title = RegexUtils.removeNotChineseAndNotNumber(titleText);
        }
        return StringUtils.removeInvalidSymbol(title);
    }

    /**
     * 格式化文本
     *
     * @param content 正文
     * @return 格式化后的正文
     */
    public static String formatContent(String content) {
        // 缩进处理 每段缩进4个空格
        content = StringUtils.indentation(content);
        // html空格处理 &nbsp; -> 空格
        content = StringUtils.htmlBlank(content);
        return content;
    }

    /**
     * 获取下一页
     *
     * @param content      原文本
     * @param nextPageRule nextPage必须得有，不然返回StringUtil.EMPTY
     * @param baseUri      baseUri用于拼接完整路径
     * @return 下一页URL
     */
    public static String nextPage(String content, CommonRule nextPageRule, String baseUri) {
        if (nextPageRule != null && nextPageRule.isEffective()) {
            String next = Matchers.match(content, nextPageRule);
            // 已经是完整的URL了
            if (UrlUtils.isHttpUrl(next)) {
                return next;
            }
            // 获得完整的URL
            return UrlUtils.completeUrl(baseUri, next);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 过滤URL
     * 找出节点所在dom树深度次数最多的a标签
     *
     * @param aTags a节点列表
     */
    public static List<Element> filterImpuritiesElements(List<Element> aTags) {
        if (CollectionUtils.isNotEmpty(aTags)) {
            Map<Integer, List<Element>> results = new HashMap<>(aTags.size() / 2);
            for (Element element : aTags) {
                String url = element.attr("href");
                if (StringUtils.isNotBlank(url)) {
                    int features = element.parents().size() + (element.hasText() ? 1 : 0);
                    computeUrlFeatures(results, features, url, element);
                }
            }
            return getFeaturesResult(results);
        }
        return aTags;
    }

    /**
     * 过滤URL
     * 通过一些特征 过滤章节列表的杂质
     *
     * @param chapters 章节列表
     */
    public static List<Chapter> filterImpuritiesChapters(List<Chapter> chapters) {
        if (CollectionUtils.isNotEmpty(chapters)) {
            Map<Integer, List<Chapter>> results = new HashMap<>(chapters.size() / 2);
            for (Chapter chapter : chapters) {
                String url = chapter.getUrl();
                if (StringUtils.isNotBlank(url)) {
                    computeUrlFeatures(results, 0, url, chapter);
                }
            }
            return getFeaturesResult(results);
        }
        return chapters;
    }

    /**
     * 计算URL特征码
     *
     * @param results  结果集
     * @param features 特征码
     * @param url      要计算的URL
     * @param item     当前解析的类目
     * @param <T>      类型
     */
    private static <T> void computeUrlFeatures(Map<Integer, List<T>> results, int features, String url, T item) {
        // 移除末尾的 /
        if (url.endsWith(StringUtils.DELIMITER)) {
            url = url.substring(0, url.length() - 1);
        }
        String[] parts = url.split(StringUtils.DELIMITER);
        // 特征为 URL目录深度
        features += parts.length;
        if (parts.length > 1) {
            String prefix = parts[parts.length - 2];
            String suffix = parts[parts.length - 1];
            // 倒数第二个前缀hash  + 后缀类型
            features += prefix.hashCode() + RegexUtils.getStringType(suffix);
        } else {
            // 后缀类型
            features += RegexUtils.getStringType(url);
        }
        results.computeIfAbsent(features, k -> new ArrayList<>()).add(item);
    }

    /**
     * 计算出现次数最多的特征码
     *
     * @param results 结果集
     * @param <T>     类型
     * @return 最终结果集
     */
    private static <T> List<T> getFeaturesResult(Map<Integer, List<T>> results) {
        int resultFeatures = 0;
        int resultNumber = 0;
        for (Map.Entry<Integer, List<T>> entry : results.entrySet()) {
            if (entry.getValue().size() >= resultNumber) {
                resultFeatures = entry.getKey();
                resultNumber = entry.getValue().size();
            }
        }
        return results.get(resultFeatures);
    }
}
