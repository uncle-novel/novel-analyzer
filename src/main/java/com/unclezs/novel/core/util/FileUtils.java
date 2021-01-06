package com.unclezs.novel.core.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 3:02 下午
 */
@Slf4j
@UtilityClass
public class FileUtils {
    public final String USER_DIR = System.getProperty("user.dir");

    static {
        log.debug("当前工作目录：{}", USER_DIR);
    }

    /**
     * 文件是否存在
     *
     * @param path /
     * @return /
     */
    public boolean exist(String path) {
        return new File(path).exists();
    }

    /**
     * 从文件中读取每一行数据
     *
     * @param file    文件
     * @param charset 编码
     * @return 文件中的每行内容的集合
     * @throws IOException /
     */
    public List<String> readLines(File file, String charset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    /**
     * 写出字符串到文件
     *
     * @param file    文件
     * @param content 字符串
     * @param charset 编码
     * @throws IOException /
     */
    public void writeString(File file, String content, String charset) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))) {
            writer.write(content);
        }
    }

    public static void main(String[] args) throws IOException {
        writeString(new File(USER_DIR + "/1.txt"), "123", StandardCharsets.UTF_8.name());
    }
}
