package xyz.ytora.sql4j.util;

import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.ytool.str.Strs;

/**
 * 表工具类
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
        return Strs.toUnderline(table.getSimpleName());
    }

}
