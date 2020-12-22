package com.unclezs.novel.analyzer.matcher;

import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.utils.CollectionUtil;
import com.unclezs.novel.analyzer.utils.StringUtil;
import com.unclezs.novel.analyzer.utils.regex.RegexUtil;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Set;

/**
 * 支持 @text @ownText  等其他attr
 *
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/21 14:08
 */
@UtilityClass
public class CssMatcher {
    /**
     * 绝对路径  eg. abs:href   abs:src
     */
    private final String ABS_URL = "abs:";
    Set<String> supportAttr = CollectionUtil.newSet("href", "src", "text", "ownText");


    /**
     * 正则匹配
     *
     * @param src      源
     * @param cssQuery 正则
     * @return /
     */
    public String matcher(String src, String cssQuery) {
        return matcher(Jsoup.parse(src), cssQuery);
    }

    /**
     * 正则匹配
     *
     * @param src      源
     * @param cssQuery 正则
     * @return /
     */
    public String matcher(Document src, String cssQuery) {
        Pair<String, String> css = getCss(cssQuery);
        return matcher(src, css.getLeft(), css.getRight());
    }

    /**
     * 正则匹配
     *
     * @param document 源
     * @param cssQuery 正则
     * @return /
     */
    public String matcher(Element document, String cssQuery, String attr) {
        Element element = selectFirst(document, cssQuery);
        if (element == null) {
            return "";
        }
        if (StringUtil.isEmpty(attr)) {
            Element first = selectFirst(element, cssQuery);
            if (first == null) {
                return "";
            } else {
                return first.text();
            }
        } else {
            String attribute;
            switch (attr) {
                case "text":
                    attribute = element.text();
                    break;
                case "ownText":
                    attribute = element.ownText();
                    break;
                default:
                    attribute = element.attr(attr);
            }
            return attribute;
        }
    }

    /**
     * 是否支持的属性
     *
     * @param attr /
     * @return /
     */
    private boolean support(String attr) {
        return supportAttr.contains(attr) || attr.startsWith(ABS_URL) || RegexUtil.isWord(attr);
    }

    /**
     * 选择第一个
     *
     * @param document 文档
     * @param cssQuery cssQuery
     * @return /
     */
    private Element selectFirst(Element document, String cssQuery) {
        Elements elements = document.select(cssQuery);
        if (elements.isEmpty()) {
            return null;
        }
        return elements.get(0);
    }

    /**
     * 获取css  left-> Jsoup支持的css选择器  right-> 自定义的属性选择器
     *
     * @param cssQuery query
     * @return /
     */
    private Pair<String, String> getCss(String cssQuery) {
        int mid = cssQuery.lastIndexOf("@");
        Pair<String, String> css = new Pair<>();
        if (mid != -1) {
            String attr = cssQuery.substring(mid + 1);
            String cssSelector = cssQuery.substring(0, mid);
            // 不支持的标签不当做属性
            if (support(attr)) {
                css.setLeft(cssSelector);
                css.setRight(attr);
                return css;
            }
        }
        css.setLeft(cssQuery);
        return css;
    }
}
