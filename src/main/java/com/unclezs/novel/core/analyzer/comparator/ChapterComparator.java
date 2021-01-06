package com.unclezs.novel.core.analyzer.comparator;

import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.util.regex.RegexUtils;
import com.unclezs.novel.core.util.uri.UrlUtils;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * @author blog.unclezs.com
 * @since 2020/12/21 17:54
 */
public class ChapterComparator implements Comparator<Chapter> {
    @Override
    public int compare(Chapter o1, Chapter o2) {
        String one = UrlUtils.getUrlLastPathNotSuffix(o1.getUrl());
        String two = UrlUtils.getUrlLastPathNotSuffix(o2.getUrl());
        if (RegexUtils.isNumber(one) && RegexUtils.isNumber(two)) {
            BigInteger v1 = new BigInteger(one);
            BigInteger v2 = new BigInteger(two);
            return v1.compareTo(v2);
        }
        return 0;
    }
}

