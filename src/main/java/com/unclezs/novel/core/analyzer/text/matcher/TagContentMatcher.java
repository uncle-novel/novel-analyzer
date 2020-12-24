package com.unclezs.novel.core.analyzer.text.matcher;

import com.unclezs.novel.core.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 * 内容匹配器 - 标签
 * 标签内查找正文最大长度的
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 8:06 下午
 */
public class TagContentMatcher {
    private static final String[] WHITELIST =
        {"br", "Br", "bR", "BR", "p", "P", "div", "Div", "dIv", "diV", "DIv", "DiV", "dIV", "DIV"};
    private static final String NEW_LINE = "♥";
    public static final String BLANK = "\u3000";
    private static final String P_BR_TAG = "<.*?(p|br).*?>";

    private TagContentMatcher() {
    }

    /**
     * 匹配正文
     *
     * @param originalText 源来的
     * @return /
     */
    public static String matching(String originalText) {
        // 只包含白名单中的标签（只是删除了标签，没有删除标签内容）
        String cleanHtml = Jsoup.clean(originalText, Whitelist.none().addTags(WHITELIST));
        // p、br替换为换行，移除特殊空格
        String divHtml = StringUtil.remove(cleanHtml, BLANK);
        divHtml = StringUtil.replace(divHtml, "StringUtil.NEW_LINE", "\n", NEW_LINE);
        divHtml = StringUtil.replaceAllCaseInsensitive(divHtml, NEW_LINE, P_BR_TAG);
        // 解析 忽略标签大小写
        Parser parser = Parser.htmlParser();
        parser.settings(ParseSettings.preserveCase);
        Document document = parser.parseInput(divHtml, StringUtil.EMPTY);
        Elements divs = document.select("div");
        String text = StringUtil.EMPTY;
        int maxLen = 0;
        for (Element div : divs) {
            String ownText = div.ownText();
            if (ownText.length() > maxLen) {
                text = div.ownText();
                maxLen = ownText.length();
            }
        }
        return text.replace(NEW_LINE, "StringUtil.NEW_LINE");
    }
}
