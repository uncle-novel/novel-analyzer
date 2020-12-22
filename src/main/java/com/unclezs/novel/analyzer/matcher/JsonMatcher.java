package com.unclezs.novel.analyzer.matcher;

import com.jayway.jsonpath.JsonPath;
import lombok.experimental.UtilityClass;

/**
 * @author zhanghongguo@sensorsdata.cn
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
        return "";
    }
}
