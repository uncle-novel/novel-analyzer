package com.unclezs.novel.analyzer.util.uri;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author unclezs.com
 * @date 2019.07.08 19:46
 */
public class UrlEncoder {
    private UrlEncoder() {
    }

    /**
     * 将url里的中文转Unicode
     *
     * @param url     /
     * @param charset /
     * @return /
     */
    public static String encode(String url, String charset) {
        StringBuilder toUrl = new StringBuilder();
        for (char c : url.toCharArray()) {
            String word = String.valueOf(c);
            if (RegexUtils.isChinese(word)) {
                try {
                    toUrl.append(java.net.URLEncoder.encode(word, charset));
                } catch (UnsupportedEncodingException e) {
                    toUrl.append(c);
                    e.printStackTrace();
                }
            } else {
                toUrl.append(c);
            }
        }
        return toUrl.toString();
    }

    /**
     * Unicode编码
     *
     * @param keyword /
     * @return /
     */
    public static String encode(String keyword) {
        try {
            return java.net.URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return keyword;
    }

    /**
     * {@code &#x}编码转换成汉字
     *
     * @param src 字符集
     * @return 解码后的字符集
     */
    public static String deCodeUnicode(String src) {
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length());
        int lastPos = 0;
        int pos;
        char ch;
        src = src.replace("&#x", "%u").replace(";", StringUtils.EMPTY);
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {

                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src, lastPos, pos);
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
}
