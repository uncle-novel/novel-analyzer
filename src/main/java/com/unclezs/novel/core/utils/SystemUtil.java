package com.unclezs.novel.core.utils;

import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @since 2020/12/24 18:42
 */
@UtilityClass
public class SystemUtil {
    private static final String OS_NAME = System.getProperty("os.name", "Windows");
    private static final boolean IS_OS_MAC = getOsMatches("Mac");
    private static final boolean IS_OS_WINDOWS = getOsMatches("Windows");
    private static final boolean IS_OS_LINUX = getOsMatches("Linux") || getOsMatches("LINUX");

    /**
     * 判断当前OS的类型。
     *
     * <p>
     * 如果不能取得系统属性<code>os.name</code>（因为Java安全限制），则总是返回<code>false</code>
     * </p>
     *
     * @return 如果当前OS类型为Mac，则返回<code>true</code>
     */
    public boolean isMac() {
        return IS_OS_MAC;
    }

    /**
     * 匹配OS名称。
     *
     * @param osNamePrefix OS名称前缀
     * @return 如果匹配，则返回<code>true</code>
     */
    private boolean getOsMatches(String osNamePrefix) {
        if (OS_NAME == null) {
            return false;
        }

        return OS_NAME.startsWith(osNamePrefix);
    }

    /**
     * 判断当前OS的类型。
     *
     * <p>
     * 如果不能取得系统属性<code>os.name</code>（因为Java安全限制），则总是返回<code>false</code>
     * </p>
     *
     * @return 如果当前OS类型为Linux，则返回<code>true</code>
     */
    public boolean isLinux() {
        return IS_OS_LINUX;
    }

    /**
     * 判断当前OS的类型。
     *
     * <p>
     * 如果不能取得系统属性<code>os.name</code>（因为Java安全限制），则总是返回<code>false</code>
     * </p>
     *
     * @return 如果当前OS类型为Windows，则返回<code>true</code>
     */
    public boolean isWindows() {
        return IS_OS_WINDOWS;
    }

    /**
     * 获取可执行文件后缀名
     *
     * @return
     */
    public String getExecuteSuffix() {
        if (isWindows()) {
            return ".exe";
        }
        return StringUtil.EMPTY;
    }
}
