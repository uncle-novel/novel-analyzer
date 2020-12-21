package com.unclezs.novel.analyzer.core.text.matcher;

import com.unclezs.novel.analyzer.utils.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
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
    private static final String[] WHITELIST = {"br", "p", "div"};
    private static final String NEW_LINE = "♥";
    public static final String BLANK = "\u3000";
    private static final String P_TAG = "(\n|\r\n|<.*?p.*?>)";
    private static final String BR_TAG = "<.*?br.*>";

    private TagContentMatcher() {
    }

    /**
     * 匹配正文
     *
     * @param originalText 源来的
     * @return /
     */
    public static String matching(String originalText) {
        Whitelist whitelist = new Whitelist();
        whitelist.addTags(WHITELIST);
        // 解析 忽略标签大小写
        Parser parser = Parser.htmlParser();
        parser.settings(new ParseSettings(false, false));
        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(parser.parseInput(originalText, ""));
        String cleanedHtml = StringUtil.remove(clean.body().html(), BLANK)
                .replaceAll(P_TAG, NEW_LINE)
                .replaceAll(BR_TAG, NEW_LINE);
        Document document = parser.parseInput(cleanedHtml, "");
        Elements divs = document.select("div");
        System.out.println(document);
        String text = "";
        int maxLen = 0;
        for (Element div : divs) {
            String ownText = div.ownText();
            if (ownText.length() > maxLen) {
                text = div.ownText();
                maxLen = ownText.length();
            }
        }
        return text.replace(NEW_LINE, "\r\n");
    }
}
