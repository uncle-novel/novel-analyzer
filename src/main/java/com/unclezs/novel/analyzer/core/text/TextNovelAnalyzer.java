package com.unclezs.novel.analyzer.core.text;

import com.unclezs.novel.analyzer.core.model.AnalyzerConfig;
import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.spider.model.Chapter;
import com.unclezs.novel.analyzer.utils.CollectionUtil;
import com.unclezs.novel.analyzer.utils.StringUtil;

import java.util.List;

/**
 * 文本小说解析器
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:42 下午
 */
public class TextNovelAnalyzer {
    private static final long serialVersionUID = 1L;

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
     * @param content        html/json
     * @param analyzerConfig 解析配置
     * @return /
     */
    public List<Chapter> chapters(String content, AnalyzerConfig analyzerConfig) {
        return null;
    }
}
