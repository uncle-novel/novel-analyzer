package com.unclezs.novel.analyzer.spider.helper;

import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.util.BeanUtils;
import com.unclezs.novel.analyzer.util.FileUtils;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author blog.unclezs.com
 * @date 2021/02/10 16:53
 */
@UtilityClass
public class SpiderHelper {
    /**
     * 请求参数预处理
     * 1. 配置自定义的请求的参数
     * 2. 脚本变量初始化
     *
     * @param rule   执行的规则
     * @param params 要处理的请求参数
     */
    public static String request(CommonRule rule, RequestParams params) throws IOException {
        if (rule != null && rule.getParams() != null) {
            BeanUtils.copy(rule.getParams(), params);
        }
        String content;
        try {
            // 脚本初始变量添加 当前页面URL
            ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_URL, params.getUrl());
            content = Http.content(params);
        } catch (Exception e) {
            // 请求失败则移除脚本上下文
            ScriptContext.remove();
            throw new IOException(e);
        }
        return content;
    }

    /**
     * 请求参数预处理
     * 1. 配置自定义的请求的参数
     * 2. 脚本变量初始化
     *
     * @param params 要处理的请求参数
     */
    public static String request(RequestParams params) throws IOException {
        return request(null, params);
    }

    /**
     * 合并文件
     *
     * @param dir      小说目录
     * @param filename 文件
     */
    public static void mergeNovel(File dir, String filename) throws IOException {
        FileUtils.deleteFile(filename);
        File[] txtFiles = dir.listFiles((dir1, name) -> name.endsWith(".txt"));
        if (txtFiles != null) {
            Arrays.stream(txtFiles).sorted((o1, o2) -> {
                Integer a = Integer.valueOf(o1.getName().split("\\.")[0]);
                Integer b = Integer.valueOf(o2.getName().split("\\.")[0]);
                return a - b;
            }).forEach(file -> {
                try {
                    String s = FileUtils.readUtf8String(file.getAbsoluteFile());
                    FileUtils.appendUtf8String(filename, s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
