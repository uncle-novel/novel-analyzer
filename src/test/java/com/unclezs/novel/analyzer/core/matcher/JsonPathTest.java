package com.unclezs.novel.analyzer.core.matcher;

import com.jayway.jsonpath.JsonPath;
import com.unclezs.novel.analyzer.model.Chapter;
import org.junit.Test;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2020/12/28 10:43 下午
 */
public class JsonPathTest {
    private final String json = "{\n" +
        "    \"code\":0,\n" +
        "    \"msg\":\"成功\",\n" +
        "    \"data\":{\n" +
        "        \"current_page\":1,\n" +
        "        \"data\":[\n" +
        "            {\n" +
        "                \"unique_id\":\"dd2aa4a97ab900ad5c7b679e445d9cde\",\n" +
        "                \"ip\":\"119.167.153.50\",\n" +
        "                \"port\":\"8118\",\n" +
        "                \"ip_address\":\"山东省 青岛市\",\n" +
        "                \"anonymity\":0,\n" +
        "                \"protocol\":\"http\",\n" +
        "                \"isp\":\"联通\",\n" +
        "                \"speed\":46,\n" +
        "                \"validated_at\":\"2017-12-25 15:11:05\",\n" +
        "                \"created_at\":\"2017-12-25 15:11:05\",\n" +
        "                \"updated_at\":\"2017-12-25 15:11:05\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"unique_id\":\"7468e4ee73bf2be35b36221231ab02d5\",\n" +
        "                \"ip\":\"119.5.0.42\",\n" +
        "                \"port\":\"22\",\n" +
        "                \"ip_address\":\"四川省 南充市\",\n" +
        "                \"anonymity\":0,\n" +
        "                \"protocol\":\"http\",\n" +
        "                \"isp\":\"联通\",\n" +
        "                \"speed\":127,\n" +
        "                \"validated_at\":\"2017-12-25 15:10:04\",\n" +
        "                \"created_at\":\"2017-12-25 14:38:14\",\n" +
        "                \"updated_at\":\"2017-12-25 15:10:04\"\n" +
        "            }\n" +
        "        ],\n" +
        "        \"last_page\":1,\n" +
        "        \"per_page\":15,\n" +
        "        \"to\":8,\n" +
        "        \"total\":8\n" +
        "    }\n" +
        "}";

    @Test
    public void testJsonPathList() {
        List<Object> ips = JsonPath.read(json, "$.data.data");
        String string = ips.get(0).toString();
        System.out.println(string);
        Object o = JsonPath.read(string, "$.ip");
        System.out.println(o);
    }

    @Test
    public void testJsonMatcher(){
        Chapter chapter = new Chapter();
        chapter.setUrl("$.ip");
        chapter.setName("ip_address");
        String listRule = "json:$.data.data";
//        List<Chapter> chapters = Matchers.matchList(json, listRule,  chapter);
//        chapters.forEach(System.out::println);
    }
}
