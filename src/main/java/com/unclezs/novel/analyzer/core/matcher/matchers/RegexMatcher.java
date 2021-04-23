package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;

import java.util.List;

/**
 * 正则匹配器
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 11:39
 */
public class RegexMatcher extends Matcher {
    /**
     * 分组模板分隔符
     */
    public static final String REGEX_TEMPLATE_DELIMITER = "##";
    private static final RegexMatcher ME = new RegexMatcher();
    /**
     * HTML中的title标签内容
     */
    private static final String TITLE_REGEX = "<title>([\\s\\S]+?)</title>##$1";

    private RegexMatcher() {
    }

    /**
     * 获取单例
     *
     * @return 实例
     */
    public static RegexMatcher me() {
        return ME;
    }

    /**
     * 获取正则模板 left：正则 right：模板 $1:$2
     * 格式：正则##模板
     *
     * @param regex 正则
     * @return /
     */
    public static Pair<String, String> getTemplate(String regex) {
        String[] ret = regex.split(REGEX_TEMPLATE_DELIMITER);
        Pair<String, String> pair = Pair.of(ret[0], "$0");
        if (ret.length > 1) {
            pair.setRight(ret[1]);
        }
        return pair;
    }

    /**
     * 别名列表
     *
     * @return 别名列表
     */
    @Override
    public MatcherAlias[] aliases() {
        return new MatcherAlias[]{MatcherAlias.alias("regex:"), MatcherAlias.alias("regex")};
    }

    /**
     * 匹配列表
     *
     * @param src      源
     * @param listRule 列表规则
     * @param <E>      类型
     * @return 列表结果
     */
    @Override
    @SuppressWarnings("unchecked")
    protected <E> List<E> list(String src, CommonRule listRule) {
        Pair<String, String> rulePair = getTemplate(listRule.getRule());
        // 通过模板匹配列表
        return (List<E>) RegexUtils.findAllByTemplate(rulePair.getLeft(), src, rulePair.getRight());
    }

    /**
     * 正则匹配
     * <p>
     * 从src中匹配出多个值并根据regex生成新的字符串
     * 匹配结束后会删除匹配内容之前的内容（包括匹配内容）
     * 例如：<br>
     * src 2013年5月 regex (.*?)年(.*?)月##$1-$2 得到 2013-5
     *
     * @param element 源节点
     * @param rule    正则
     * @return /
     */
    @Override
    protected <E> String one(E element, String rule) {
        String source = StringUtils.toStringNullToEmpty(element);
        return match(source, rule);
    }

    /**
     * 正则匹配
     *
     * @param src   源
     * @param regex 正则
     * @param index 组
     * @return /
     */
    public String match(String src, String regex, int index) {
        String ret = RegexUtils.get(regex, src, index);
        return ret == null ? StringUtils.EMPTY : ret;
    }

    /**
     * 正则匹配
     * <p>
     * 从src中匹配出多个值并根据regex生成新的字符串
     * 匹配结束后会删除匹配内容之前的内容（包括匹配内容）
     * 例如：<br>
     * src 2013年5月 regex (.*?)年(.*?)月##$1-$2 得到 2013-5
     *
     * @param src  源
     * @param rule 正则
     * @return /
     */
    public String match(String src, String rule) {
        Pair<String, String> pair = getTemplate(rule);
        String ret = RegexUtils.findByTemplate(pair.getLeft(), src, pair.getRight());
        return ret == null ? StringUtils.EMPTY : ret;
    }

    /**
     * 获取Html中的标题
     *
     * @param content /
     * @return /
     */
    public String title(String content) {
        return this.one(content, TITLE_REGEX);
    }

    /**
     * 获取标题没有 零到十的只包含中文的
     *
     * @param content /
     * @return /
     */
    public String titleWithoutNumber(String content) {
        return RegexUtils.removeNotChineseAndNotNumber(title(content));
    }
}
