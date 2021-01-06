package com.unclezs.novel.core.spider.pipline;

import com.unclezs.novel.core.model.Chapter;
import com.unclezs.novel.core.util.FileUtils;
import com.unclezs.novel.core.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 保存为文本文件的pipeline
 *
 * @author blog.unclezs.com
 * @date 2020/12/23 10:58 下午
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class StoreTextFilePipeline implements Pipeline<Chapter> {
    private static final String DOWNLOAD_FILE_FORMAT = "%s/downloads/%s/%d.txt";
    private String bookName = "公众号书虫无书荒";
    private String charset = StandardCharsets.UTF_8.name();

    public StoreTextFilePipeline(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public void process(Chapter chapter) {
        String downloadFile = String.format(DOWNLOAD_FILE_FORMAT, FileUtils.USER_DIR, StringUtils.removeInvalidSymbol(bookName), chapter.getOrder());
        try {
            // 创建文件
            File file = FileUtils.touch(downloadFile);
            // 格式化内容 移除标题
            String text = chapter.getName() + StringUtils.NEW_LINE + StringUtils.removeTitle(chapter.getContent(), chapter.getName());
            // 写入文件
            FileUtils.writeString(file, text, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
