package com.unclezs.novel.analyzer.contant;

import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 8:16 下午
 */
@UtilityClass
public class RegularConstant {
    /**
     * 中文
     */
    public final String CHINESE = "\\u4E00-\\u9FFF";
    /**
     * unicode符号
     */
    public final String UNICODE_LETTER_NUMBER = "\\uFF41-\\uFF5a\\uFF21-\\uFF3a\\uFF10-\\uFF19";
    /**
     * 中文标点符号
     */
    public final String CHINESE_PUNCTUATION = "~\\u000A\\u0009\\u00A0\\u0020\\u3000\\uFEFF";
}
