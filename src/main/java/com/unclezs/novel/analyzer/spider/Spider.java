package com.unclezs.novel.analyzer.spider;

import ch.qos.logback.core.util.FileUtil;
import com.unclezs.novel.analyzer.AnalyzerManager;
import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.common.exception.SpiderRuntimeException;
import com.unclezs.novel.analyzer.common.exception.TaskCanceledException;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.ChapterState;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.helper.SpiderHelper;
import com.unclezs.novel.analyzer.spider.pipline.BaseFilePipeline;
import com.unclezs.novel.analyzer.spider.pipline.ConsolePipeline;
import com.unclezs.novel.analyzer.spider.pipline.Pipeline;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.RandomUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

/**
 * 小说爬虫，提供了爬虫的一些能力
 * 提供管道处理爬虫爬取下来的章节数据
 * 支持多线程配置，默认懒加载，不执行run方法不会创建线程池
 * 配置都为懒加载，创建爬虫及创建线程池，都是在run方法执行时候初始化。
 * 可以对爬虫进行备份
 *
 * @author blog.unclezs.com
 * @date 2021/01/11 18:50
 */
@Slf4j
@Getter
@Setter
public final class Spider implements Serializable {
  /**
   * 初始化完成，随时可以启动的状态
   */
  public static final int READY = 1;
  public static final int RUNNING = 2;
  public static final int PAUSED = 3;
  /**
   * 多次重试完成了
   */
  public static final int COMPLETE = 4;
  /**
   * 正在处理管道
   */
  public static final int PIPELINE = 5;
  public static final int STOPPED = 6;
  public static final int SUCCESS = 7;
  /**
   * 爬虫计数器，统计本次启动了多少个爬虫
   */
  private static final AtomicInteger COUNTER = new AtomicInteger(1);
  /**
   * 爬虫状态 0：未开始  1：运行中  2：暂停中 3：已暂停  4：停止中 5：已停止
   * 可能3->1， 不可能4->1
   */
  private final transient AtomicInteger state = new AtomicInteger();
  /**
   * 爬虫启动所
   */
  private final transient ReentrantLock runLock = new ReentrantLock();
  /**
   * 剩余未下载成功的章节
   */
  private final AtomicInteger successCount;
  /**
   * 失败的章节
   */
  private final AtomicInteger errorCount;
  /**
   * 实际的爬虫
   */
  @Getter
  private transient NovelSpider novelSpider;
  /**
   * 线程池
   */
  private transient ThreadPoolExecutor threadPool;
  /**
   * 进度改变处理器，用户可以通过此得知爬虫进度
   */
  private transient BiConsumer<Double, String> progressChangeHandler;
  /**
   * 数据处理管道
   */
  private transient List<Pipeline> pipelines;
  /**
   * 正在执行的任务集合，用户监控任务状态
   *
   * @see com.unclezs.novel.analyzer.spider.Spider.Task
   */
  private transient CopyOnWriteArraySet<Task> tasks;
  /**
   * 线程数量
   */
  private int threadNum;
  /**
   * 解析配置
   */
  private AnalyzerRule analyzerRule;
  /**
   * 重新下载 下载失败的章节 次数 0则不重试
   */
  private int currentTimes;
  private int retryTimes;
  /**
   * 忽略错误
   */
  private boolean ignoreError;
  /**
   * 小说目录
   */
  private List<Chapter> toc;
  /**
   * 小说信息
   */
  private Novel novel;
  /**
   * 小说目录地址
   */
  private String url;
  /**
   * 总章节数
   */
  private int totalCount;
  /**
   * 文件下载路径（如果有，用于dump时保留）
   */
  private String savePath;
  /**
   * 当状态改变时回调
   */
  private transient IntConsumer onStateChange;

  public Spider() {
    this.threadNum = 1;
    this.currentTimes = 0;
    this.retryTimes = 0;
    this.totalCount = 0;
    this.successCount = new AtomicInteger(0);
    this.errorCount = new AtomicInteger(0);
    this.pipelines = new ArrayList<>();
    this.tasks = new CopyOnWriteArraySet<>();
  }

  /**
   * 设置小说信息
   *
   * @param novel 小说
   * @return this
   */
  public Spider setNovel(Novel novel) {
    this.novel = novel;
    if (CollectionUtils.isNotEmpty(novel.getChapters())) {
      this.toc = novel.getChapters();
    }
    return this;
  }

  /**
   * 获取爬虫状态
   *
   * @return /
   */
  public int state() {
    return this.state.get();
  }

  /**
   * 线程数量
   *
   * @param threadNum 线程数量
   * @return this
   */
  public Spider setThreadNum(int threadNum) {
    if (threadNum < 1) {
      threadNum = 1;
    }
    this.threadNum = threadNum;
    if (threadPool != null) {
      threadPool.setCorePoolSize(threadNum);
      threadPool.setMaximumPoolSize(threadNum);
    }
    return this;
  }

  /**
   * 数据处理管道
   *
   * @param pipeline 管道
   * @return this
   */
  public Spider addPipeline(Pipeline pipeline) {
    this.pipelines.add(pipeline);
    return this;
  }

  private void validate() {
    // 目录地址校验
    if (StringUtils.isBlank(url)) {
      throw new SpiderRuntimeException("目录地址不能为空");
    }
    // 设置解析配置
    if (this.analyzerRule == null) {
      throw new SpiderRuntimeException("解析规则不能为空");
    }
  }

  /**
   * 确定novel序号
   *
   * @param pipeline 管道:获取novel地址
   */

  private void setChapterOrder(Pipeline pipeline) {
    String novelSavePath = ((BaseFilePipeline) pipeline).getFilePath();
    // 编序号
    int order = 1;
    if (FileUtils.exist(novelSavePath)) {
      File novelDir = new File(novelSavePath);
      if (novelDir.isDirectory()) {
        File[] files = novelDir.listFiles((dir1, name) -> name.endsWith(".txt"));
        order = files.length + 1;
      }
    }
    for (Chapter chapter : toc) {
      chapter.setOrder(order++);
    }
    // 兼容反序列化
    novel.setChapters(toc);
    // 记录总数
    this.totalCount = toc.size();
  }

  /**
   * 初始化爬虫，兼容从备份导入方式
   */
  private void init() throws IOException {
    validate();
    novelSpider = new NovelSpider();
    novelSpider.setRule(this.analyzerRule);
    // 小说详情
    if (novel == null) {
      novel = novelSpider.details(url);
      novel.setUrl(url);
      // 获取小说详情信息
      log.trace("抓取到小说详情信息：{}", novel);
    }
    if (StringUtils.isBlank(novel.getTitle())) {
      novel.setTitle("未知标题" + RandomUtils.randomInt(1000));
    }
    // 章节抓取 兼容反序列化情况
    if (CollectionUtils.isEmpty(this.toc)) {
      toc = novelSpider.toc(url);
      if (CollectionUtils.isEmpty(this.toc)) {
        log.warn("章节数据抓取失败或未获取到章节：{}", url);
        throw new SpiderRuntimeException("章节数据抓取失败或未获取到章节:" + url);
      }
    }

    // 没有提供管道则使用控制台打印
    if (pipelines.isEmpty()) {
      pipelines.add(new ConsolePipeline());
    }
    // 设置文件保存路径
    pipelines.forEach(pipeline -> {
      if (pipeline instanceof BaseFilePipeline && StringUtils.isNotBlank(savePath)) {
        ((BaseFilePipeline) pipeline).setPath(savePath);
      }
      // 小说详情注入到管道
      pipeline.injectNovel(novel);
      setChapterOrder(pipeline);
    });
    // 初始化任务监控集合
    tasks = new CopyOnWriteArraySet<>();
    // 初始化线程
    threadPool = ThreadUtils.newFixedThreadPoolExecutor(this.threadNum, String.format("spider-%d", COUNTER.getAndIncrement()));
    // 更新初始进度
    progressChangeHandler.accept(progress(), progressText());
    setState(READY);
  }

  /**
   * 爬取一本小说
   */
  private void crawling() {
    // 全部已经下载成功
    if (isSucceed()) {
      setState(SUCCESS);
      return;
    }
    log.debug("开始爬取小说[{}]：已下载{}/{}章 开启{}个线程 是否启用自动代理：{}", novel.getTitle(), successCount, toc.size(), threadNum, AnalyzerManager.me().isAutoProxy());
    tasks.clear();
    // 如果是暂停，则只执行INIT状态的任务,不是暂停状态则执行所有非DOWNLOADED状态的任务
    boolean isPaused = isState(PAUSED);
    setState(RUNNING);
    toc.stream()
      .filter(chapter -> isPaused ? chapter.getState() == ChapterState.INIT : !chapter.downloaded())
      .forEach(chapter -> threadPool.execute(new Task(chapter)));
    // 全部任务已完成（不一定都下载成功）
    while (!tasks.isEmpty()) {
      // 被取消执行
      if (isState(PAUSED, STOPPED)) {
        break;
      }
    }
    // 全部章节下载成功
    if (isSucceed()) {
      log.debug("小说【{}】已经全部抓取成功", novel.getTitle());
      doLast();
    }
  }

  /**
   * 启动爬虫
   */
  public void run() {
    if (runLock.tryLock()) {
      try {
        // 忽略错误保存
        if (ignoreError && isState(COMPLETE)) {
          doLast();
          return;
        }
        // 未初始化则进行初始化
        if (!isExceed(READY)) {
          init();
        }
        // 到了完成阶段，已经不能再操作了
        if (isExceed(PIPELINE)) {
          return;
        }
        while (currentTimes <= retryTimes) {
          this.crawling();
          if (isState(PAUSED, STOPPED, SUCCESS)) {
            break;
          }
          currentTimes++;
        }
        // 不是被中断的,且没有全部成功
        if (currentTimes > retryTimes && !isState(PAUSED, STOPPED, SUCCESS)) {
          setState(COMPLETE);
        }
      } catch (Exception e) {
        log.error("小说{}抓取失败", url, e);
      } finally {
        runLock.unlock();
      }
    }
  }

  /**
   * 异步启动爬虫,幂等
   */
  public void runAsync() {
    ThreadUtils.execute(this::run);
  }

  /**
   * 设置状态
   *
   * @param state 状态
   */
  private void setState(int state) {
    // 到了管道阶段，不可再逆转状态了
    if (isExceed(PIPELINE) && state != SUCCESS) {
      return;
    }
    this.state.set(state);
    // 状态改变回调
    if (onStateChange != null) {
      onStateChange.accept(state);
    }
    handleChangeState(state);
  }

  /**
   * 全部都已经下载成功
   *
   * @return true 全部下载成功
   */
  private boolean isSucceed() {
    return toc.stream().allMatch(Chapter::downloaded);
  }

  /**
   * 根据状态采取一定行为
   *
   * @param state 状态
   */
  private void handleChangeState(int state) {
    switch (state) {
      case PAUSED:
        cancelRunningTasks();
        log.trace("小说[{}]抓取已经暂停：已下载{}/{}章", novel.getTitle(), successCount.get(), toc.size());
        break;
      case COMPLETE:
        log.trace("小说[{}]抓取完成：已下载{}/{}章，错误章节：{}", novel.getTitle(), successCount.get(), toc.size(), errorCount());
        break;
      case PIPELINE:
        // 回调管道处理完成
        pipelines.forEach(Pipeline::onComplete);
        log.trace("小说[{}]抓取完成，等待管道处理完成", novel.getTitle());
        break;
      // 停止、完成 关闭线程池
      case STOPPED:
        log.trace("小说[{}]抓取已停止 - 任务丢弃", novel.getTitle());
        shutdown();
        break;
      case SUCCESS:
        log.debug("小说[{}]抓取成功：共{}章", novel.getTitle(), toc.size());
        shutdown();
        break;
      default:
    }
  }

  /**
   * 判断是否为某个状态
   *
   * @param states 目标状态
   * @return true 满足状态条件
   */
  public boolean isState(int... states) {
    return Arrays.stream(states).anyMatch(value -> state() == value);
  }

  /**
   * 判断是否过了某个状态
   *
   * @param state 目标状态
   * @return true 已经过了这个状态
   */
  public boolean isExceed(int state) {
    return state() >= state;
  }

  /**
   * 取消正在执行的任务
   */
  public void cancelRunningTasks() {
    if (tasks != null) {
      tasks.forEach(Task::cancel);
    }
    if (threadPool != null) {
      threadPool.getQueue().clear();
    }
  }

  /**
   * 关闭爬虫，释放一些重资源
   */
  private void shutdown() {
    this.cancelRunningTasks();
    if (this.threadPool != null) {
      this.threadPool.shutdown();
    }
    this.toc = null;
    this.novel = null;
    this.threadPool = null;
    this.pipelines = null;
    this.progressChangeHandler = null;
  }

  /**
   * 暂停爬虫，可以重新启动，不会立即暂停，进行的任务还是会进行
   */
  public void pause() {
    if (isExceed(READY) && !isSucceed()) {
      setState(PAUSED);
    }
  }

  /**
   * 停止爬虫 将不会接收新的任务
   * 不会立即停止，进行的任务还是会进行
   */
  public void stop() {
    setState(STOPPED);
  }

  /**
   * 忽略错误直接后续操作
   */
  private void doLast() {
    setState(PIPELINE);
    setState(SUCCESS);
  }

  /**
   * 重置重试次数
   */
  public void resetRetryTimes() {
    if (isState(COMPLETE)) {
      this.currentTimes = 0;
      log.trace("重置重试次数为：0/{}", retryTimes);
    }
  }

  /**
   * 任务进度
   *
   * @return 任务进度百分比
   */
  public double progress() {
    return (double) successCount.get() / totalCount;
  }

  /**
   * 任务进度
   *
   * @return 任务进度百分比
   */
  public String progressText() {
    return String.format("%d/%d", successCount.get(), this.totalCount);
  }

  /**
   * 错误章节数量
   *
   * @return 数量
   */
  public int errorCount() {
    return errorCount.get();
  }

  /**
   * 章节抓取任务
   */
  class Task implements Runnable {
    private final Chapter chapter;
    private boolean canceled;

    public Task(Chapter chapter) {
      this.chapter = chapter;
      tasks.add(this);
    }

    /**
     * 执行章节抓取任务
     */
    @Override
    public void run() {
      try {
        assertNotCanceled();
        String content = novelSpider.content(chapter.getUrl());
        // 内容是空白也当做错误处理
        if (StringUtils.isBlank(content.trim()) && !StringUtils.NULL.equals(content.trim())) {
          throw new SpiderRuntimeException("未知的，未抓取的章节内容");
        }
        // 移除标题
        if (Boolean.TRUE.equals(analyzerRule.getContent().getRemoveTitle())) {
          content = SpiderHelper.removeTitle(content, chapter.getName());
        }
        chapter.setContent(content);
        assertNotCanceled();
        // 管道处理章节数据
        if (CollectionUtils.isNotEmpty(pipelines)) {
          pipelines.forEach(pipeline -> pipeline.process(chapter));
        }
        // 管道处理完成之后释放章节内容
        chapter.setContent(null);
        assertNotCanceled();
        // 计数器改变
        if (chapter.getState() == ChapterState.FAILED) {
          errorCount.decrementAndGet();
        }
        if (chapter.getState() != ChapterState.DOWNLOADED) {
          successCount.incrementAndGet();
          // 下完完成标记
          chapter.setState(ChapterState.DOWNLOADED);
        }
      } catch (Exception e) {
        if (e instanceof TaskCanceledException) {
          return;
        }
        if (!isCanceled()) {
          // 错误章节计数器
          if (chapter.getState() != ChapterState.FAILED) {
            chapter.setState(ChapterState.FAILED);
            errorCount.incrementAndGet();
          }
          log.warn("小说章节内容爬取失败：order:{} - {} - {}", chapter.getOrder(), chapter.getName(), chapter.getUrl(), e);
        }
      } finally {
        tasks.remove(this);
        // 通知完成一个章节的抓取（无论成功、失败、取消）
        if (progressChangeHandler != null) {
          progressChangeHandler.accept(progress(), progressText());
        }
        // 下载延迟
        Long delayTime = analyzerRule.getContent().getDelayTime();
        if (!canceled && delayTime != null && delayTime > 0) {
          ThreadUtils.sleep(delayTime);
        }
      }
    }

    /**
     * 尝试取消任务 不一定会有效，在正文解析前取消有效
     */
    public void cancel() {
      this.canceled = true;
    }

    /**
     * 如果被取消或者当前状态不是running则认为任务被中断
     *
     * @return true 被取消
     */
    private boolean isCanceled() {
      return canceled || !isState(RUNNING) || chapter.getState() == ChapterState.DOWNLOADED;
    }

    /**
     * 断言没有任务未被取消
     */
    private void assertNotCanceled() {
      if (isCanceled()) {
        throw new TaskCanceledException();
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Task task = (Task) o;
      return chapter.getOrder() == task.chapter.getOrder();
    }

    @Override
    public int hashCode() {
      return Objects.hash(chapter);
    }
  }
}
