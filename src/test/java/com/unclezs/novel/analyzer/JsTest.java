package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.core.matcher.MatchersTest;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.FileUtils;
import org.junit.Test;

import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @since  2021/01/27 18:46
 */
public class JsTest {
    @Test
    public void testJs() throws ScriptException, IOException {
        String js = FileUtils.readUtf8String(MatchersTest.class.getResource("/script/matcher.js").getFile());
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("target", "javascript:Chapter(5285087,39397);");
        bindings.put("result", "xxx");
        System.out.println(ScriptUtils.execute(js, bindings));
    }

    @Test
    public void testCharset() {
        // gbk
        //System.out.println(Http.get("http://www.335xs.info/top/allvisit_1.html"));
        // gb2312
        System.out.println(Http.get("http://www.ting89.com/playbook/?14416-0-0.html"));
    }

    @Test
    public void testCharsetPhantomJs() throws IOException {
        // gbk
        String gbk = "http://www.335xs.info/top/allvisit_1.html";
        // gb2312
        String gb2312 = "http://www.ting89.com/playbook/?14416-0-0.html";
        RequestParams params = RequestParams.create(gbk);
        params.setDynamic(true);
        System.out.println(Http.content(params));
    }
}
