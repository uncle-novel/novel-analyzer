package com.unclezs.novel.analyzer.script;

import com.unclezs.novel.analyzer.script.variables.Utils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static javax.script.ScriptContext.GLOBAL_SCOPE;

/**
 * 脚本工具 默认JS
 *
 * @author blog.unclezs.com
 * @date 2021/1/28 22:55
 */
@UtilityClass
public class ScriptUtils {
  /**
   * js引擎
   */
  @Getter
  private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("js");

  static {
    // 通用工具
    Utils utils = new Utils();
    SCRIPT_ENGINE.getContext().setAttribute(utils.getVariableName(), utils, GLOBAL_SCOPE);
  }

  /**
   * 执行脚本获取返回值
   *
   * @param js               js脚本内容
   * @param runtimeVariables 运行时变量
   * @return 结果
   */
  @SneakyThrows
  public static String execute(String js, Bindings runtimeVariables) {
    Object result = SCRIPT_ENGINE.eval(js, runtimeVariables);
    return StringUtils.toStringNullToEmpty(result);
  }

  /**
   * 执行脚本获取返回值
   *
   * @param js js脚本内容
   * @return 结果
   */
  @SneakyThrows
  public static String execute(String js) {
    Object result = SCRIPT_ENGINE.eval(js);
    return StringUtils.toStringNullToEmpty(result);
  }
}
