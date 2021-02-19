package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.FileUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 保存为文本文件的pipeline
 * 路径为当前目录的下的downloads
 *
 * @author blog.unclezs.com
 * @date 2020/12/23 10:58 下午
 */
@Slf4j
@Setter
public class MediaFilePipeline extends BaseFilePipeline {
    private static final String DOWNLOAD_FILE_FORMAT = "%s/%s.%s";
    private static final String MEDIA_TYPE = "mp3";

    @Override
    public void process(Chapter chapter) {
        String downloadFile = String.format(DOWNLOAD_FILE_FORMAT, getFilePath(), chapter.getName(), getType(chapter.getContent()));
        try {
            RequestParams requestParams = RequestParams.create(chapter.getContent());
            requestParams.addHeader(RequestParams.REFERER, chapter.getUrl());
            byte[] bytes = Http.bytes(requestParams);
            FileUtils.writeBytes(downloadFile, bytes);
        } catch (IOException e) {
            log.error("保存章节内容到：{} 失败.", downloadFile, e);
            e.printStackTrace();
        }
    }

    /**
     * 获取音频类型
     *
     * @param page URL
     * @return 类型
     */
    private String getType(String page) {
        int typeSplitIndex = page.lastIndexOf(".");
        if (typeSplitIndex < 0) {
            return MEDIA_TYPE;
        } else {
            return page.substring(typeSplitIndex + 1);
        }
    }
}
