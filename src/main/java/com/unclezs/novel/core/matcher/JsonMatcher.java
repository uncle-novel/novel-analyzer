package com.unclezs.novel.core.matcher;

import com.jayway.jsonpath.JsonPath;
import lombok.experimental.UtilityClass;

/**
 * 开源地址：https://github.com/json-path/JsonPath
 * 在线测试：http://jsonpath.herokuapp.com/
 *
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
