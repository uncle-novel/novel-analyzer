package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Setter;

/**
 * 管道处理基类
 *
 * @author blog.unclezs.com
 * @since 2021/02/08 11:02
 */
@Setter
public abstract class BaseFilePipeline extends BasePipeline {
    /**
     * 默认保存路径 非final，可以自己定义
     */
    public static String defaultPath = FileUtils.USER_DIR + "/downloads/";
    /**
     * 默认文件名称
     */
    public static String defaultFileName = "公众号书虫无书荒";
    private String path;

    /**
     * 获取文件保存路径
     *
     * @return 文件路径
     */
    public String getPath() {
        return path == null ? defaultPath : path;
    }

    /**
     * 获取带小说名称的保存路径
     *
     * @return 带小说名称的保存路径
     */
    public String getFilePath() {
        if (getNovel() != null && StringUtils.isNotBlank(getNovel().getTitle())) {
            return getPath() + StringUtils.removeInvalidSymbol(getNovel().getTitle());
        }
        return getPath() + defaultFileName;
    }
}
