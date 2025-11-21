package xyz.yangtong.sql4j.util;

import xyz.yangtong.sql4j.anno.Table;

/**
 * 表工具栏
 */
public class TableUtil {

    /**
     * 从 CLASS 对象中解析出表名称
     */
    public static String parseTableNameFromClass(Class<?> table) {
        Table anno = table.getAnnotation(Table.class);
        if (anno != null) {
            String name = anno.name();
            if (!name.isEmpty()) {
                return name;
            }
            String value = anno.value();
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return StrUtil.toLowerUnderline(table.getSimpleName());
    }

}
