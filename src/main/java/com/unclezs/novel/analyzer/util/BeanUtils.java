package com.unclezs.novel.analyzer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean工具
 *
 * @author blog.unclezs.com
 * @since 2021/1/30 21:08
 */
@Slf4j
@UtilityClass
public class BeanUtils {
  /**
   * 拷贝不为null的字段
   *
   * @param src    源
   * @param target 目
   * @param <T>    类型
   */
  public static <T> void copy(T src, T target) {
    if (src == null || target == null) {
      return;
    }
    Class<?> clazz = src.getClass();
    for (Field field : getFields(clazz)) {
      try {
        Object srcValue = field.get(src);
        if (srcValue != null) {
          field.set(target, srcValue);
        }
      } catch (IllegalAccessException e) {
        log.error("BeanUtils拷贝属性失败: {}", field.getName(), e);
      }
    }
  }

  /**
   * map变为bean
   *
   * @param map   map
   * @param clazz 类型
   * @param <T>   类型
   */
  public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
    try {
      T instance = clazz.newInstance();
      for (Map.Entry<String, ?> entry : map.entrySet()) {
        try {
          Field field = clazz.getDeclaredField(entry.getKey());
          field.setAccessible(true);
          field.set(instance, entry.getValue());
        } catch (NoSuchFieldException e) {
          log.warn("没有找到字段：{} - {}", entry.getKey(), e.getMessage());
        }
      }
      return instance;
    } catch (Exception e) {
      log.error("创建对象失败：{}", clazz);
    }
    return null;
  }

  /**
   * 获取类的字段 非常量
   *
   * @param clazz 类
   * @return 字段列表
   */
  public static List<Field> getFields(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();
    List<Field> fieldList = new ArrayList<>();
    for (Field field : fields) {
      int mod = field.getModifiers();
      if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
        continue;
      }
      field.setAccessible(true);
      fieldList.add(field);
    }
    return fieldList;
  }
}
