package com.unclezs.novel.analyzer.common.page;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author blog.unclezs.com
 * @date 2021/2/12 14:28
 */
@Slf4j
public abstract class AbstractPageable<T> implements Pageable {
    /**
     * 已经访问过的链接
     */
    private final Set<String> visited = new HashSet<>(16);
    /**
     * 当前页码
     */
    @Setter
    @Getter
    private int page;
    /**
     * 一个书源搜索完成后的回调
     */
    private Consumer<T> onNewItemAddHandler;
    /**
     * 加载数据锁
     */
    private final Lock loadLock = new ReentrantLock();
    /**
     * 是否取消，防止多个线程时候，一个还在取消中，一个又开始了新的搜索
     */
    private static final ThreadLocal<Boolean> CANCELED = new ThreadLocal<>();
    /**
     * 标记已完成
     */
    private boolean hasMore;
    /**
     * 是否忽略错误 默认true
     */
    @Getter
    @Setter
    private boolean ignoreError;

    public AbstractPageable() {
        this.ignoreError = true;
        this.init();
    }

    /**
     * 当有新的添加时候的回调
     *
     * @param onNewItemAddHandler 回调
     */
    public void setOnNewItemAddHandler(Consumer<T> onNewItemAddHandler) {
        this.onNewItemAddHandler = onNewItemAddHandler;
    }

    /**
     * 获取Item的唯一标识
     *
     * @param item 数据项
     * @return 唯一标识
     */
    protected abstract String getUniqueId(T item);

    /**
     * 加载下一页
     *
     * @param page 下一页页码
     * @return true 则还有更多
     * @throws IOException 下一页加载失败 一般为网络IO异常
     */
    protected abstract boolean loadPage(int page) throws IOException;

    /**
     * 首次加载调用经行初始化
     */
    protected void firstLoad() throws IOException {
        init();
        loadMore();
    }

    /**
     * 加载更多
     */
    @Override
    public void loadMore() throws IOException {
        // 已经加载完成 不再加载更多了
        if (!hasMore()) {
            return;
        }
        if (loadLock.tryLock()) {
            try {
                this.page++;
                try {
                    this.hasMore = loadPage(this.page);
                } catch (IOException e) {
                    // 忽略异常 否则回退一页 下次继续请求
                    if (!ignoreError) {
                        this.page--;
                        throw new IOException(e);
                    }
                }
                // 加载完成
                if (!this.hasMore) {
                    cancel();
                }
            } finally {
                loadLock.unlock();
            }
        } else {
            log.trace("正在加载中，请等待加载结束后再试");
        }
    }

    /**
     * 加载全部
     *
     * @throws IOException IO异常
     */
    public void loadAll() throws IOException {
        while (hasMore()) {
            if (!isCanceled()) {
                loadMore();
            }
        }
    }

    /**
     * 添加下一页数据项， 并且唤醒回调
     *
     * @param item 数据项
     * @return 是否为新的 true新的 false旧的，已经被添加过了
     */
    protected boolean addItem(T item) {
        if (!isCanceled() && visited.add(getUniqueId(item))) {
            if (onNewItemAddHandler != null) {
                onNewItemAddHandler.accept(item);
            }
            return true;
        }
        return false;
    }

    /**
     * 添加下一页数据项列表， 并且唤醒回调
     *
     * @param items 数据项
     * @return 是否有新的 true有新的 false全部是旧的
     */
    protected boolean addItems(List<T> items) {
        boolean hasNew = false;
        for (T item : items) {
            if (addItem(item)) {
                hasNew = true;
            }
        }
        return !isCanceled() && hasNew;
    }

    /**
     * 是否还有更多
     *
     * @return true还有更多
     */
    public boolean hasMore() {
        return this.hasMore;
    }

    /**
     * 页面添加访问
     *
     * @param uniqueId 唯一标识
     */
    protected void addVisited(String uniqueId) {
        visited.add(uniqueId);
    }

    /**
     * 是否已经被访问
     *
     * @param uniqueId 唯一标识
     * @return false则已经被访问过了
     */
    protected boolean isVisited(String uniqueId) {
        return visited.contains(uniqueId);
    }

    /**
     * 取消搜索
     */
    public void cancel() {
        CANCELED.remove();
    }

    /**
     * 是否已经被取消
     *
     * @return true已经被取消
     */
    protected boolean isCanceled() {
        return CANCELED.get() == null;
    }

    /**
     * 重置搜索引擎
     */
    protected void init() {
        this.page = 0;
        this.visited.clear();
        CANCELED.set(false);
        hasMore = true;
        // 初始化是否有更多页数据
        if (loadLock.tryLock()) {
            loadLock.unlock();
        }
    }
}
