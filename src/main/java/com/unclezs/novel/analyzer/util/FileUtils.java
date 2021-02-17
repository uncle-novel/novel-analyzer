package com.unclezs.novel.analyzer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
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
    public static final String USER_DIR = System.getProperty("user.dir");

    static {
        log.debug("当前工作目录：{}", USER_DIR);
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @throws IOException IO异常
     */
    public static void deleteFile(File file) throws IOException {
        Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @throws IOException IO异常
     */
    public static void deleteFile(String file) throws IOException {
        Files.deleteIfExists(Paths.get(file));
    }

    /**
     * 文件是否存在
     *
     * @param path /
     * @return /
     */
    public static boolean exist(String path) {
        return new File(path).exists();
    }

    /**
     * 获取文件 不存在则创建
     *
     * @param path 文件路径
     * @return 文件
     * @throws IOException /
     */
    public static File touch(String path) throws IOException {
        return touch(path, StringUtils.EMPTY);
    }

    /**
     * 获取文件 不存在则创建
     *
     * @param parent 父文件路径
     * @param child  子文件路径
     * @return 文件
     * @throws IOException /
     */
    public static File touch(String parent, String child) throws IOException {
        File file = new File(parent, child);
        if (!file.exists()) {
            // 创建父目录
            if (!file.getParentFile().exists()) {
                mkdirs(file.getParentFile().getAbsolutePath());
            }
            Files.createFile(Paths.get(parent, child));
        }
        return file;
    }

    /**
     * 递归创建文件夹
     *
     * @param path 文件路径
     * @throws IOException /
     */
    public static void mkdirs(String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }

    /**
     * 从文件中读取每一行数据
     *
     * @param file    文件
     * @param charset 编码
     * @return 文件中的每行内容的集合
     * @throws IOException /
     */
    public static List<String> readLines(File file, String charset) throws IOException {
        return Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.forName(charset));
    }

    /**
     * 从文件中全部字符串
     *
     * @param file 文件
     * @return 文件中的每行内容的集合
     * @throws IOException /
     */
    public static String readUtf8String(File file) throws IOException {
        List<String> lines = readLines(file, StandardCharsets.UTF_8.name());
        StringBuilder allLine = new StringBuilder();
        for (String line : lines) {
            allLine.append(line).append(StringUtils.NEW_LINE);
        }
        return allLine.toString();
    }

    /**
     * 从文件中全部字符串
     *
     * @param resourcePathFile 资源文件
     * @return 文件中的每行内容的集合
     * @throws IOException /
     */
    public static String readResource(String resourcePathFile) throws IOException {
        return readUtf8String(new File(FileUtils.class.getResource(resourcePathFile).getFile()));
    }

    /**
     * 从文件路径中全部字符串
     *
     * @param filePath 文件路径
     * @return 文件中的每行内容的集合
     * @throws IOException /
     */
    public static String readUtf8String(String filePath) throws IOException {
        return readUtf8String(new File(filePath));
    }

    /**
     * 写出字符串到文件
     *
     * @param file    文件
     * @param content 字符串
     * @param charset 编码
     * @throws IOException /
     */
    public static void writeString(File file, String content, String charset) throws IOException {
        writeString(file.getAbsolutePath(), content, charset);
    }

    /**
     * 写出字符串到文件
     *
     * @param filePath 文件
     * @param content  字符串
     * @throws IOException /
     */
    public static void writeUtf8String(String filePath, String content) throws IOException {
        writeString(filePath, content, StandardCharsets.UTF_8.name());
    }

    /**
     * 追加模式 写出字符串到文件
     *
     * @param filePath 文件
     * @param content  字符串
     * @throws IOException /
     */
    public static void appendUtf8String(String filePath, String content) throws IOException {
        writeString(filePath, content, StandardCharsets.UTF_8.name(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    /**
     * 写出字符串到文件
     *
     * @param file    文件完整路径
     * @param content 字符串
     * @param charset 编码
     * @throws IOException /
     */
    public static void writeString(String file, String content, String charset) throws IOException {
        writeString(file, content, charset, StandardOpenOption.CREATE);
    }

    /**
     * 写出字符串到文件
     *
     * @param file    文件完整路径
     * @param content 字符串
     * @param charset 编码
     * @throws IOException /
     */
    public static void writeString(String file, String content, String charset, OpenOption... openOptions) throws IOException {
        touch(file);
        Files.write(Paths.get(file), Collections.singletonList(content), Charset.forName(charset), openOptions);
    }


    /**
     * 写出字节到文件
     *
     * @param file    字节
     * @param content 字符串
     * @throws IOException /
     */
    public static void writeBytes(String file, byte[] content) throws IOException {
        touch(file);
        Files.write(Paths.get(file), content, StandardOpenOption.CREATE);
    }
}
