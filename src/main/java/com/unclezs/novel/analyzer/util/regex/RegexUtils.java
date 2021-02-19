package com.unclezs.novel.analyzer.util.regex;

import com.unclezs.novel.analyzer.common.exception.UtilException;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaoleilu
 * @author blog.unclezs.com
 * @since 2020/12/20 7:24 下午
 */
@UtilityClass
public class RegexUtils {
    /**
     * 数字
     */
    public final Pattern NUMBERS = Pattern.compile("\\d+");
    /**
     * 中文
     */
    public final String CHINESE = "\\u4E00-\\u9FFF";
    public final Pattern CHINESE_REG = Pattern.compile("[\\u4E00-\\u9FFF]");
    public final Pattern HAS_CHINESE_REG = Pattern.compile("[\\u4E00-\\u9FFF]");
    public final Pattern NOT_CHINESE_AND_NOT_NUMBER = Pattern.compile("([^" + CHINESE + "]|[一二三四五六七八九十零])");
    /**
     * unicode符号
     */
    public final String UNICODE_LETTER_NUMBER = "\\uFF41-\\uFF5a\\uFF21-\\uFF3a\\uFF10-\\uFF19";
    /**
     * 中文标点符号
     */
    public final String CHINESE_PUNCTUATION = "~\\u000A\\u0009\\u00A0\\u0020\\u3000\\uFEFF";
    /**
     * 字母
     */
    public final Pattern WORD = Pattern.compile("[a-zA-Z]+");
    /**
     * 包含字母
     */
    public final Pattern CONTAINS_CHAR = Pattern.compile(".*?[a-zA-Z].*?");
    /**
     * 包含数字
     */
    public final Pattern CONTAINS_NUMBER = Pattern.compile(".*?[0-9].*?");
    /**
     * 正则中需要被转义的关键字
     */
    public final Set<Character> RE_KEYS =
        CollectionUtils.newSet('$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|');

    /**
     * 是否为数字
     *
     * @param src /
     * @return /
     */
    public boolean isNumber(String src) {
        return NUMBERS.matcher(src).matches();
    }

    /**
     * 是否为中文
     *
     * @param src /
     * @return /
     */
    public boolean isChinese(CharSequence src) {
        return CHINESE_REG.matcher(src).matches();
    }

    /**
     * 是否为中文
     *
     * @param src /
     * @return /
     */
    public boolean containChinese(CharSequence src) {
        return CHINESE_REG.matcher(src).find();
    }

    /**
     * 是否为单词
     *
     * @param src 源
     * @return /
     */
    public boolean isWord(String src) {
        return WORD.matcher(src).matches();
    }

    /**
     * 移除非中文且非中文数字的字符
     *
     * @param content /
     * @return /
     */
    public String removeNotChineseAndNotNumber(String content) {
        return NOT_CHINESE_AND_NOT_NUMBER.matcher(content).replaceAll(StringUtils.EMPTY);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex      匹配的正则
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, int groupIndex) {
        if (null == content || null == regex) {
            return null;
        }
        final Pattern pattern = PatternPool.get(regex, Pattern.CASE_INSENSITIVE);
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        if (null == content || null == pattern) {
            return null;
        }

        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }

    /**
     * 获得匹配的字符串匹配到的所有分组
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     * @since 4.0.13
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
        if (null == content || null == pattern) {
            return null;
        }

        ArrayList<String> result = new ArrayList<>();
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            final int startGroup = withGroup0 ? 0 : 1;
            final int groupCount = matcher.groupCount();
            for (int i = startGroup; i <= groupCount; i++) {
                result.add(matcher.group(i));
            }
        }
        return result;
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param regex   正则
     * @param content 内容
     * @return 正则为null或者StringUtil.EMPTY则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, CharSequence content) {
        if (content == null) {
            // 提供null的字符串为不匹配
            return false;
        }

        if (StringUtils.isEmpty(regex)) {
            // 正则不存在则为全匹配
            return true;
        }

        final Pattern pattern = PatternPool.get(regex, Pattern.CASE_INSENSITIVE);
        return isMatch(pattern, content);
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者StringUtil.EMPTY则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param pattern  匹配正则
     * @param content  被匹配的内容
     * @param template 生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 新字符串
     */
    public static String findByTemplate(Pattern pattern, CharSequence content, String template) {
        if (null == content || null == pattern || null == template) {
            return null;
        }
        //提取模板中的编号
        final TreeSet<Integer> varNums = getTemplateGroups(template);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            for (Integer group : varNums) {
                template = template.replace("$" + group, matcher.group(group));
            }
            return template;
        }
        return null;
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param pattern  匹配正则
     * @param content  被匹配的内容
     * @param template 生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 新字符串列表
     */
    public static List<String> findAllByTemplate(Pattern pattern, CharSequence content, String template) {
        if (null == content || null == pattern || null == template) {
            return null;
        }
        // 提取模板中的编号
        final TreeSet<Integer> varNums = getTemplateGroups(template);
        final Matcher matcher = pattern.matcher(content);
        List<String> items = new ArrayList<>();
        while (matcher.find()) {
            for (Integer group : varNums) {
                items.add(template.replace("$" + group, matcher.group(group)));
            }
        }
        return items;
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param regex  匹配正则
     * @param content  被匹配的内容
     * @param template 生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 新字符串列表
     */
    public static List<String> findAllByTemplate(String regex, CharSequence content, String template) {
        if (null == content || null == regex || null == template) {
            return null;
        }
        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return findAllByTemplate(pattern, content, template);
    }

    /**
     * 提取模板中的组号
     *
     * @param template 模板
     * @return 组号集合
     */
    private TreeSet<Integer> getTemplateGroups(String template) {
        // 提取模板中的编号
        final TreeSet<Integer> varNums = new TreeSet<>();
        final Matcher matcherForTemplate = PatternPool.GROUP_VAR.matcher(template);
        while (matcherForTemplate.find()) {
            varNums.add(Integer.parseInt(matcherForTemplate.group(1)));
        }
        return varNums;
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 匹配结束后会删除匹配内容之前的内容（包括匹配内容）<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param regex    匹配正则字符串
     * @param content  被匹配的内容
     * @param template 生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 按照template拼接后的字符串
     */
    public static String findByTemplate(String regex, CharSequence content, String template) {
        if (null == content || null == regex || null == template) {
            return null;
        }

        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return findByTemplate(pattern, content, template);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param pattern    编译后的正则模式
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(Pattern pattern, CharSequence content, int group,
                                                           T collection) {
        if (null == pattern || null == content) {
            return null;
        }

        if (null == collection) {
            throw new NullPointerException("Null collection param provided!");
        }

        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            collection.add(matcher.group(group));
        }
        return collection;
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * <p>
     * 例如：原字符串是：中文1234，我想把1234换成(1234)，则可以：
     *
     * <pre>
     * ReUtil.replaceAll("中文1234", "(\\d+)", "($1)"))
     *
     * 结果：中文(1234)
     * </pre>
     *
     * @param content             文本
     * @param regex               正则
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     */
    public static String replaceAll(CharSequence content, String regex, String replacementTemplate) {
        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return replaceAll(content, pattern, replacementTemplate);
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * @param content             文本
     * @param pattern             {@link java.util.regex.Pattern}
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     * @since 3.0.4
     */
    public static String replaceAll(CharSequence content, Pattern pattern, String replacementTemplate) {
        if (StringUtils.isEmpty(content)) {
            return StringUtils.str(content);
        }
        final Matcher matcher = pattern.matcher(content);
        if (replacementTemplate == null) {
            replacementTemplate = StringUtils.EMPTY;
        }
        boolean result = matcher.find();
        if (result) {
            final Set<String> varNums = findAll(PatternPool.GROUP_VAR, replacementTemplate, 1, new HashSet<>());
            final StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacementTemplate;
                for (String var : varNums) {
                    int group = Integer.parseInt(var);
                    replacement = replacement.replace("$" + var, matcher.group(group));
                }
                matcher.appendReplacement(sb, escape(replacement));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return StringUtils.str(content);
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换
     *
     * @param str        要替换的字符串
     * @param regex      用于匹配的正则式
     * @param replaceFun 决定如何替换的函数
     * @return 替换后的文本
     * @since 4.2.2
     */
    public static String replaceAll(CharSequence str, String regex, Function<Matcher, String> replaceFun) {
        return replaceAll(str, Pattern.compile(regex), replaceFun);
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换
     *
     * @param str        要替换的字符串
     * @param pattern    用于匹配的正则式
     * @param replaceFun 决定如何替换的函数,可能被多次调用（当有多个匹配时）
     * @return 替换后的字符串
     * @since 4.2.2
     */
    public static String replaceAll(CharSequence str, Pattern pattern, Function<Matcher, String> replaceFun) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.str(str);
        }

        final Matcher matcher = pattern.matcher(str);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(buffer, replaceFun.apply(matcher));
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 转义字符，将正则的关键字转义
     *
     * @param c 字符
     * @return 转义后的文本
     */
    public static String escape(char c) {
        final StringBuilder builder = new StringBuilder();
        if (RE_KEYS.contains(c)) {
            builder.append('\\');
        }
        builder.append(c);
        return builder.toString();
    }

    /**
     * 转义字符串，将正则的关键字转义
     *
     * @param content 文本
     * @return 转义后的文本
     */
    public static String escape(CharSequence content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.str(content);
        }

        final StringBuilder builder = new StringBuilder();
        int len = content.length();
        char current;
        for (int i = 0; i < len; i++) {
            current = content.charAt(i);
            if (RE_KEYS.contains(current)) {
                builder.append('\\');
            }
            builder.append(current);
        }
        return builder.toString();
    }

    /**
     * 匹配HTML的编码
     *
     * @param html 网页HTML
     * @return 编码 没有找到返回null
     */
    public static String matchHtmlCharset(String html) {
        Matcher matcher = PatternPool.HTML_CHARSET.matcher(html);
        if (matcher.find()) {
            // 先选择几个常规的
            String charset = matcher.group("default");
            // 没有再去匹配其他比较特殊的
            if (StringUtils.isEmpty(charset)) {
                charset = matcher.group("match");
            }
            if (charset != null) {
                try {
                    Charset.forName(charset);
                } catch (UnsupportedCharsetException e) {
                    // 都没有匹配到则使用GBK编码
                    charset = null;
                }
            }
            return charset;
        }
        return null;
    }

    /**
     * 获取字符串的组成类型
     *
     * @return 0：字符数字其他 1:数字 2:字符 3:字符数字
     */
    public int getStringType(String source) {
        int type = 0;
        if (CONTAINS_NUMBER.matcher(source).matches()) {
            type = 1;
        }
        if (CONTAINS_CHAR.matcher(source).matches()){
            type += 2;
        }
        return type;
    }
}
