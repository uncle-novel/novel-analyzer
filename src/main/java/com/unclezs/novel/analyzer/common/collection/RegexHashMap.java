package com.unclezs.novel.analyzer.common.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author blog.unclezs.com
 * @since 2021/07/12 19:28
 */
public class RegexHashMap<V> implements Map<String, V> {
    private final Map<String, V> values = new HashMap<>(16);

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (values.containsKey(key)) {
            return true;
        }
        String keyStr = key.toString();
        return values.keySet().stream().anyMatch(ikey -> ikey.matches(keyStr));
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public V get(Object key) {
        V value = values.get(key);
        if (value == null) {
            String keyStr = key.toString();
            value = values.keySet().stream()
                    .filter(ikey -> ikey.matches(keyStr))
                    .map(values::get)
                    .findFirst()
                    .orElse(null);
        }
        return value;
    }

    @Override
    public V put(String key, V value) {
        return values.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        values.putAll(map);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<V> values() {
        return values.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return values.entrySet();
    }
}
