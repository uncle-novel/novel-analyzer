package com.unclezs.novel.analyzer.core.rule;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.unclezs.novel.analyzer.common.exception.RuleGrammarException;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用规则
 *
 * <pre>
 *    # 最小规则
 *    rule: "xpath://dic[@class='xx']"
 *    # 完整规则
 *    rule:{
 *      "params": "自定义请求参数"
 *      "type": "xx"
 *      "rule": "xpath://dic[@class='xx']"
 *      "script": "js脚本内容"
 *      "replace": [
 *         {
 *            "from":"正则"
 *            "to":"模板 eg: $1 取第一组"
 *         }
 *      ]
 *   }
 *   # 替换规则支持简写
 *   # 多个替换规则
 *   "replace": [
 *      {
 *         "from":"正则"
 *         "to":"模板 eg: $1 取第一组"
 *      }
 *   ]
 *   # 单个替换规则
 *   "replace":{
 *       "from":"正则"
 *       "to":"模板 eg: $1 取第一组"
 *   }
 *   # 单个替换规则 缩写一行
 *   "replace": "from##to"
 * </pre>
 *
 * @author blog.unclezs.com
 * @date 2021/1/27 21:54
 * @see com.unclezs.novel.analyzer.core.helper.RuleHelper
 */
@Data
public class CommonRule implements Serializable, JsonDeserializer<CommonRule> {
    private static final long serialVersionUID = 6752071448939164168L;
    /**
     * 规则匹配的页面 比如 detail：详情页  search：搜索页
     */
    private String page;
    /**
     * 请求参数
     */
    private RequestParams params;
    /**
     * 匹配器类型 可以手动填写这个，如果不填写则从规则进行解析
     */
    private String type;
    /**
     * 必填项，规则，支持带类型 如 xpath://dic[@class='xx']，不带类型则需要填type
     */
    private String rule;
    /**
     * 匹配到之后的后置处理脚本
     */
    private String script;
    /**
     * 净化规则
     */
    private List<ReplaceRule> replace;

    /**
     * 是否为有效的规则
     */
    public boolean isEffective() {
        return StringUtils.isNotBlank(type) && StringUtils.isNotBlank(rule);
    }

    /**
     * 是否为有效的规则
     */
    public static boolean isEffective(CommonRule commonRule) {
        return commonRule != null && (commonRule.isEffective() || StringUtils.isNotBlank(commonRule.getScript()));
    }

    /**
     * 是否为有效的规则
     */
    public static boolean isEffective(CommonRule... commonRules) {
        if (commonRules == null) {
            return false;
        }
        for (CommonRule commonRule : commonRules) {
            if (!isEffective(commonRule)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为有效的规则 只有脚本存在
     */
    public static boolean isEffectiveOnlyScript(CommonRule commonRule) {
        return commonRule != null && !commonRule.isEffective() && StringUtils.isNotBlank(commonRule.getScript());
    }

    /**
     * 是否包含Rule规则
     */
    public static boolean hasRule(CommonRule commonRule) {
        return commonRule != null && StringUtils.isNotBlank(commonRule.getRule());
    }

    /**
     * 创建一个规则
     *
     * @return 只包含规则类型与规则的
     */
    public static CommonRule create(String type, String rule) {
        CommonRule commonRule = new CommonRule();
        commonRule.setRule(rule);
        commonRule.setType(type);
        return commonRule;
    }

    /**
     * 创建一个规则
     *
     * @param withTypeRule 带类型的规则
     * @return 只包含规则类型与规则的
     */
    public static CommonRule create(String withTypeRule) {
        Pair<String, String> ruleType = RuleHelper.parseRuleType(withTypeRule);
        CommonRule commonRule = new CommonRule();
        commonRule.setType(ruleType.getLeft());
        commonRule.setRule(ruleType.getRight());
        return commonRule;
    }

    /**
     * 反序列化规则
     *
     * @param json    json节点
     * @param typeOfT 类型
     * @param context 上下文
     * @return 解析结果CommonRule
     * @throws JsonParseException /
     */
    @Override
    public CommonRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // 直接跳过
        if (typeOfT != CommonRule.class) {
            return null;
        }
        CommonRule commonRule = new CommonRule();
        // 规则是个对象
        if (json.isJsonObject()) {
            JsonObject ruleJson = json.getAsJsonObject();
            JsonElement params = ruleJson.get("params");
            if (params != null) {
                commonRule.setParams(context.deserialize(params, RequestParams.class));
            }
            // 规则类型
            commonRule.setPage(GsonUtils.getOrDefault(ruleJson, "page", null));
            // 规则类型
            commonRule.setType(GsonUtils.getOrDefault(ruleJson, "type", null));
            // 实际规则
            commonRule.setRule(GsonUtils.getOrDefault(ruleJson, "rule", null));
            // 处理脚本
            commonRule.setScript(GsonUtils.getOrDefault(ruleJson, "script", null));
            // 净化
            JsonElement purifyRulesJsonElement = ruleJson.get("replace");
            if (purifyRulesJsonElement != null) {
                commonRule.setReplace(handlePurifyRule(purifyRulesJsonElement, context));
            }
        } else {
            // 字符串 则直接是规则
            String rule = json.getAsString();
            if (StringUtils.isNotEmpty(rule)) {
                commonRule.setRule(rule);
            }
        }
        // 规则预处理  xpath://xx  分离
        if (StringUtils.isNotEmpty(commonRule.getRule())) {
            Pair<String, String> rulePair = RuleHelper.parseRuleType(commonRule.getRule());
            if (rulePair != null) {
                // 类型
                if (StringUtils.isNotEmpty(rulePair.getLeft()) && StringUtils.isEmpty(commonRule.getType())) {
                    commonRule.setType(rulePair.getLeft());
                }
                // 实际规则
                if (StringUtils.isNotEmpty(rulePair.getRight())) {
                    commonRule.setRule(rulePair.getRight());
                }
            }
        } else if(StringUtils.isEmpty(commonRule.getScript())){
            throw new RuleGrammarException("解析规则不能为空：rule 必填.");
        }
        return commonRule;
    }

    /**
     * 处理净化规则
     *
     * @param purifyRulesJsonElement 净化规则JSON
     * @param context                上下文
     * @return 净化规则列表
     */
    private List<ReplaceRule> handlePurifyRule(JsonElement purifyRulesJsonElement, JsonDeserializationContext context) {
        List<ReplaceRule> replaceRules = new ArrayList<>();
        // 只有一个 可以为json对象
        if (purifyRulesJsonElement.isJsonObject()) {
            ReplaceRule replaceRule = context.deserialize(purifyRulesJsonElement, ReplaceRule.class);
            if (replaceRule != null) {
                replaceRules.add(replaceRule);
            }
            // 多个净化可以为数组
        } else if (purifyRulesJsonElement.isJsonArray()) {
            JsonArray purifyRulesJsonElementAsJsonArray = purifyRulesJsonElement.getAsJsonArray();
            for (JsonElement element : purifyRulesJsonElementAsJsonArray) {
                // 是JSON对象的方式
                if (element.isJsonObject()) {
                    ReplaceRule replaceRule = context.deserialize(element, ReplaceRule.class);
                    if (replaceRule != null) {
                        replaceRules.add(replaceRule);
                    }
                } else {
                    // 直接为字符串 广告##模板
                    String purifyRuleStr = element.getAsString();
                    if (StringUtils.isNotEmpty(purifyRuleStr)) {
                        replaceRules.add(ReplaceRule.parseRule(purifyRuleStr));
                    }
                }
            }
        } else {
            // 直接为字符串 广告##模板
            String purifyRuleStr = purifyRulesJsonElement.getAsString();
            if (StringUtils.isNotEmpty(purifyRuleStr)) {
                replaceRules.add(ReplaceRule.parseRule(purifyRuleStr));
            }
        }
        return replaceRules;
    }
}
