package org.ytor.sql4j.sql;

import org.ytor.sql4j.util.StrUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表别名注册器
 */
public class AliasRegister {

    /**
     * 映射：实体类 -> 别名
     */
    private final Map<Class<?>, String> classAliasMapper = new HashMap<>();

    /**
     * 映射：实体类全类名 -> 别名
     */
    private final Map<String, String> stringAliasMapper = new HashMap<>();

    public Boolean single() {
        return classAliasMapper.size() <= 1;
    }

    public String getAlias(Class<?> clazz) {
        return classAliasMapper.get(clazz);
    }

    public String getAlias(String clazzName) {
        return stringAliasMapper.get(clazzName);
    }

    public String addAlias(Class<?> clazz) {
        String tableName = StrUtil.toLowerUnderline(clazz.getSimpleName());
        String alias = Arrays.stream(tableName.split("_")).map(i -> {
            if (!i.isEmpty()) {
                return i.substring(0, 1).toLowerCase();
            } else {
                return "";
            }
        }).collect(Collectors.joining());
        return addAlias(clazz, alias);
    }

    public String addAlias(Class<?> clazz, String alise) {
        // 校验别名是否重复
        Collection<String> values = classAliasMapper.values();
        boolean exist = values.contains(alise);
        String _alise = alise;
        for (int i = 1; exist; i++) {
            alise = _alise + i;
            exist = values.contains(alise);
        }

        classAliasMapper.put(clazz, alise);
        stringAliasMapper.put(clazz.getName(), alise);
        return alise;
    }

}
