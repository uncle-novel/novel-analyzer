package com.unclezs.novel.core.analyzer;

import com.unclezs.novel.core.matcher.Matcher;
import com.unclezs.novel.core.util.StringUtils;
import com.unclezs.novel.core.util.uri.UrlUtils;
import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * 小说解析器辅助类
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:42 下午
 */
@UtilityClass
public class AnalyzerHelper {
    /**
     * 获取下一页
     *
     * @param content      原文本
     * @param nextPageRule nextPage必须得有，不然返回StringUtil.EMPTY
     * @param baseUri      baseUri用于拼接完整路径
     * @return 下一页URL
     */
    public String nextPage(String content, String nextPageRule, String baseUri) {
        if (StringUtils.isNotEmpty(nextPageRule)) {
            String next = Matcher.matching(content, nextPageRule);
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
    public List<Element> filterImpuritiesLinks(List<Element> aTags) {
        final int depth = maxTimesKey(aTags, aTag -> aTag.parents().size());
        final int part = maxTimesKey(aTags, aTag -> aTag.absUrl("href").split("/").length);
        return aTags.stream().filter(tag -> {
            String href = tag.absUrl("href");
            return tag.parents().size() == depth && UrlUtils.notAnchor(href) && part == href.split("/").length
                && tag.hasText();
        }).collect(Collectors.toList());
    }

    /**
     * 找出出现次数最多的key  key在这里可以是
     * 1. <a>标签在哪一级出现次数最多  key=哪一级
     * 2. <a>标签的href属性 通过 / 分割，一共多少段，出现次数最多的段数=key
     * <p>
     * 相当于找出一些数字中出现次数最多的那个数
     *
     * @param elements 节点列表
     * @param keyFunc  key值计算器
     * @return 出现次数最多的key
     */
    public int maxTimesKey(List<Element> elements, ToIntFunction<Element> keyFunc) {
        //  时间复杂度 On
        Map<Integer, Integer> map = new HashMap<>(10);
        for (Element a : elements) {
            int key = keyFunc.applyAsInt(a);
            map.compute(key, (k, c) -> {
                if (c == null) {
                    return 1;
                }
                return c + 1;
            });
        }
        // 出现次数最多的key的次数
        int keyMaxCount = Collections.max(map.values());
        // 找出key是谁
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == keyMaxCount) {
                return entry.getKey();
            }
        }
        return 0;
    }
}
