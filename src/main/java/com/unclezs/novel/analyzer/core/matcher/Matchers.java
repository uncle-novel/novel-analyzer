package com.unclezs.novel.analyzer.core.matcher;

import com.unclezs.novel.analyzer.common.annotation.Alias;
import com.unclezs.novel.analyzer.core.matcher.matchers.Matcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.util.BeanUtils;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 匹配器  正则,xpath,jsonpath,css
 * <p>
 * 格式： 解析器类型：解析规则[##$正则组号(仅regex可选)]
 * <p>
 * eg: css:div -&gt; .author -&gt; a@href
 * <p>
 * regex:&lt;title&gt;(.+?)&lt;/title&gt;##$1
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 11:24
 */
@Slf4j
@UtilityClass
public class Matchers {
    /**
     * 匹配 规则带有选择器标识 css:xxx
     *
     * @param src  源文档
     * @param rule 规则
     * @return /
     */
    public static String match(Object src, String rule) {
        CommonRule commonRule = CommonRule.create(rule);
        return match(src, commonRule);
    }

    /**
     * 匹配 规则带有选择器标识 css:xxx
     *
     * @param src  源文档
     * @param rule 通用规则
     * @return /
     */
    public static String match(Object src, CommonRule rule) {
        Matcher matcher = MatcherManager.getMatcher(rule.getType(), RegexMatcher.me());
        return matcher.match(src, rule);
    }

    /**
     * 匹配一页里面的多个结果，并且封装到Bean
     *
     * @param source  源节点（节点、字符串）
     * @param ruleMap 规则集合
     * @param type    类型
     * @return 匹配结果
     */
    public <T> T matchMultiple(Object source, Map<String, CommonRule> ruleMap, Class<T> type) {
        Optional<Map.Entry<String, CommonRule>> ruleEntry = ruleMap.entrySet().stream().findFirst();
        if (ruleEntry.isPresent()) {
            Matcher matcher = MatcherManager.getMatcher(ruleEntry.get().getValue().getType(), RegexMatcher.me());
            return matcher.matchMultiple(source, ruleMap, type);
        }
        return null;
    }

    /**
     * 匹配列表
     *
     * @param src         源
     * @param rule        列表规则
     * @param itemHandler 列表项目匹配结果处理器
     */
    public static void matchList(String src, CommonRule rule, Consumer<Object> itemHandler) {
        Matcher matcher = MatcherManager.getMatcher(rule.getType(), RegexMatcher.me());
        matcher.matchList(src, rule, itemHandler);
    }

    /**
     * 匹配列表
     *
     * @param src        源
     * @param rule       列表规则
     * @param type       列表元素类型
     * @param childRules 列表元素匹配规则
     * @param <E>        类型
     * @return 列表结果
     */
    public static <E> List<E> matchList(String src, CommonRule rule, Map<String, CommonRule> childRules, Class<E> type) {
        Matcher matcher = MatcherManager.getMatcher(rule.getType(), RegexMatcher.me());
        return matcher.matchList(src, rule, childRules, type);
    }

    /**
     * 转化为Map,可以通过alias指定map的key
     *
     * @param obj        规则对象
     * @param ignore     true 忽略properties,false 包含properties，如果properties为空则此字段无效
     * @param properties 属性
     * @param type       规则类型 为null时不设置
     * @return 规则Map
     * @see com.unclezs.novel.analyzer.common.annotation.Alias
     */
    public static Map<String, CommonRule> getChildMap(String type, Object obj, boolean ignore, String... properties) {
        Class<?> clazz = obj.getClass();
        Map<String, CommonRule> map = new HashMap<>(16);
        Set<String> ignoreFieldSet = CollectionUtils.set(false, properties);
        BeanUtils.getFields(clazz).stream()
            .filter(f -> properties.length == 0 || ignore != ignoreFieldSet.contains(f.getName()))
            .forEach(field -> {
                try {
                    // 选出类型为CommonRule且不为空的
                    Object srcValue = field.get(obj);
                    if (srcValue instanceof CommonRule) {
                        Alias alias = field.getAnnotation(Alias.class);
                        CommonRule value = (CommonRule) srcValue;
                        if (type != null) {
                            // 设置默认类型 此处应该与list规则类型一样
                            value.setType(type);
                        }
                        if (alias != null) {
                            map.put(alias.value(), (CommonRule) srcValue);
                        } else {
                            map.put(field.getName(), (CommonRule) srcValue);
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.error("BeanUtils获取属性失败: {}", field.getName(), e);
                }
            });
        return map;
    }


    /**
     * 转化为Map,可以通过alias指定map的key
     *
     * @param obj  规则对象
     * @param type 规则类型
     * @return 规则Map
     * @see com.unclezs.novel.analyzer.common.annotation.Alias
     */
    public static Map<String, CommonRule> getChildMap(String type, Object obj) {
        return getChildMap(type, obj, true);
    }

    /**
     * 转化为Map,可以通过alias指定map的key
     *
     * @param obj 规则对象
     * @return 规则Map
     * @see com.unclezs.novel.analyzer.common.annotation.Alias
     */
    public static Map<String, CommonRule> getChildMap(Object obj) {
        return getChildMap(null, obj, true);
    }
}
