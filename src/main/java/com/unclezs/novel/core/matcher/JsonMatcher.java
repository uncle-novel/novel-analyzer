package com.unclezs.novel.core.matcher;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.unclezs.novel.core.util.StringUtils;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * 开源地址：https://github.com/json-path/JsonPath
 * 在线测试：http://jsonpath.herokuapp.com/
 *
 * @author blog.unclezs.com
 * @since 2020/12/21 16:20
 */
@UtilityClass
public class JsonMatcher {
    /**
     * JsonPath匹配
     *
     * @param src  /
     * @param rule /
     * @return /
     */
    public String matching(String src, String rule) {
        Object ret = JsonPath.read(src, rule);
        if (ret != null) {
            return ret.toString();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 匹配一个列表
     *
     * @param src  /
     * @param rule /
     * @return /
     */
    public List<String> macheList(String src, String rule) {
        DocumentContext context = JsonPath.parse(src);
        return null;
    }
}
