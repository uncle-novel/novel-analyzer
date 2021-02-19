package com.unclezs.novel.analyzer.util.io;

import lombok.experimental.UtilityClass;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO工具
 *
 * @author blog.unclezs.com
 * @since 2021/2/3 21:48
 */
@UtilityClass
public class IoUtils {
    /**
     * 默认缓存大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;
    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

    /**
     * 拷贝流 自动关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 总长度
     * @throws IOException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long size = 0;
        for (int readSize; (readSize = in.read(buffer)) != EOF; ) {
            out.write(buffer, 0, readSize);
            size += readSize;
            out.flush();
        }
        close(in);
        close(out);
        return size;
    }

    /**
     * 从流中读取内容，读到输出流中，读取完毕后并不关闭流
     *
     * @param in      输入流
     * @param isClose 读取完毕后是否关闭流
     * @return 输出流
     * @throws IOException IO异常
     */
    public static FastByteArrayOutputStream read(InputStream in, boolean isClose) throws IOException {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        try {
            copy(in, out);
        } finally {
            if (isClose) {
                close(in);
            }
        }
        return out;
    }

    /**
     * 从流中读取字节数据
     *
     * @param in 输入流
     * @return 字节数据
     * @throws IOException IO异常
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        return read(in, true).toByteArray();
    }

    /**
     * 文件转为流
     *
     * @param file 文件
     * @return {@link FileInputStream}
     */
    public static FileInputStream toStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    /**
     * 文件转为流
     *
     * @param file 文件
     * @return {@link FileInputStream}
     */
    public static FileInputStream toStream(String file) throws FileNotFoundException {
        return toStream(new File(file));
    }

    /**
     * 文件转为流
     *
     * @param file 文件
     * @return {@link FileOutputStream}
     */
    public static FileOutputStream toOutStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    /**
     * 文件转为流
     *
     * @param file 文件
     * @return {@link FileOutputStream}
     */
    public static FileOutputStream toOutStream(String file) throws FileNotFoundException {
        return toOutStream(new File(file));
    }

    /**
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }
}
