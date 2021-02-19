package com.unclezs.novel.analyzer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 执行CMD命令工具
 *
 * @author blog.unclezs.com
 * @since 2020/12/25 11:09
 */
@Slf4j
@UtilityClass
public class CommandUtils {
    /**
     * 执行CMD命令
     *
     * @param command 命令
     * @return 控制台数据
     */
    public static String execute(String command) throws IOException {
        StringBuilder buffer = new StringBuilder();
        log.trace("执行Command - 命令：{}", command);
        Process process = Runtime.getRuntime().exec(command);
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String tmp;
        while ((tmp = br.readLine()) != null) {
            buffer.append(tmp).append(StringUtils.NEW_LINE);
        }
        return buffer.toString();
    }
}
