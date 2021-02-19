package com.unclezs.novel.analyzer.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @author blog.unclezs.com
 * @since 2021/1/17 8:23
 */
public class GsonUtils {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    }

    /**
     * 获取单例gson
     *
     * @return /
     */
    public static Gson me() {
        return GSON;
    }

    /**
     * 将对象转为json字符串
     *
     * @param object /
     * @return /
     */
    public static String toJson(Object object) {
        String json = null;
        if (GSON != null) {
            json = GSON.toJson(object);
        }
        return json;
    }

    /**
     * 将json字符串转为指定类型的实例
     *
     * @param json /
     * @param cls  /
     * @param <T>  /
     * @return /
     */
    public static <T> T parse(String json, Class<T> cls) {
        T t = null;
        if (GSON != null) {
            t = GSON.fromJson(json, cls);
        }
        return t;
    }

    /**
     * 将json转为Map
     *
     * @param json /
     * @param <T>  /
     * @return /
     */
    public static <T> Map<String, T> toMap(String json) {
        Map<String, T> map = null;
        if (GSON != null) {
            map = GSON.fromJson(json, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 将json转为指定类型的List
     *
     * @param json /
     * @param <T> /
     * @return /
     */
    public static <T> List<T> toList(String json) {
        List<T> list = null;
        if (GSON != null) {
            // 根据泛型返回解析指定的类型,TypeToken<List<T>>{}.getType()获取返回类型
            list = GSON.fromJson(json, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * 获取json的字符串
     *
     * @param jsonObject   /
     * @param key          key
     * @param defaultValue 默认值
     * @return /
     */
    public static String getOrDefault(JsonObject jsonObject, String key, String defaultValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defaultValue;
        }
        return jsonElement.getAsString();
    }

    /**
     * 获取json的字符串
     *
     * @param jsonObject /
     * @param key        key
     * @return /
     */
    public static String get(JsonObject jsonObject, String key) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return null;
        }
        return jsonElement.getAsString();
    }
}
