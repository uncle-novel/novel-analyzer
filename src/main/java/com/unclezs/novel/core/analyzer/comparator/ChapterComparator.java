package com.unclezs.novel.core.analyzer.comparator;

import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.utils.regex.RegexUtil;
import com.unclezs.novel.core.utils.uri.UrlUtil;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * @author blog.unclezs.com
 * @since 2020/12/21 17:54
 */
public class ChapterComparator implements Comparator<Chapter> {
    @Override
    public int compare(Chapter o1, Chapter o2) {
        String one = UrlUtil.getUrlLastPathNotSuffix(o1.getUrl());
        String two = UrlUtil.getUrlLastPathNotSuffix(o2.getUrl());
        if (RegexUtil.isNumber(one) && RegexUtil.isNumber(two)) {
            BigInteger v1 = new BigInteger(one);
            BigInteger v2 = new BigInteger(two);
            return v1.compareTo(v2);
        }
        return 0;
    }
}

