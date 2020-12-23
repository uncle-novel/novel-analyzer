package com.unclezs.novel.analyzer.spider.pipline;

/**
 * 数据处理管道 用户爬取完成之后的数据处理
 *
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/23 15:51
 */
public interface Pipeline<T> {
    /**
     * 处理数据 默认直接打印
     *
     * @param data 数据
     */
    void process(T data);
}
