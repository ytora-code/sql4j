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
            String tableName = anno.value();
            if (!tableName.isEmpty()) {
                return tableName;
            }
        }
        return Strs.toUnderline(table.getSimpleName());
    }

}
