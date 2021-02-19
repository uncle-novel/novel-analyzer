package com.unclezs.novel.analyzer.script.variables;

/**
 * 脚本全局变量
 *
 * @author blog.unclezs.com
 * @since 2021/2/2 23:22
 */
public interface ScriptGlobalVariables<T> {
    /**
     * 变量名
     *
     * @return 变量名
     */
    String getVariableName();
}
