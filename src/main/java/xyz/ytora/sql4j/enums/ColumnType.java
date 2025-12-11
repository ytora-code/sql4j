package xyz.ytora.sql4j.enums;

import xyz.ytora.ytool.json.JSON;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 数据库表的列类型
 */
public enum ColumnType {
    INT1(Byte.class, byte.class)
    ,INT2(Short.class, short.class)
    ,INT3(Short.class, short.class)
    ,INT4(Integer.class, int.class)
    ,INT8(Long.class, long.class)
    ,FLOAT(Float.class, float.class)
    ,DOUBLE(Double.class, double.class)
    ,BOOLEAN(Boolean.class, boolean.class)
    ,VARCHAR255(String.class)
    ,VARCHAR64(String.class)
    ,VARCHAR16(String.class)
    ,BLOB(byte[].class)
    ,DATE(Date.class, LocalDate.class)
    ,DATE_TIME(LocalDateTime.class)
    ,TEXT(String.class)
    ,JSON(JSON.class)
    ,NONE()
    ;

    private final List<Class<?>> javaTypes;

    ColumnType(Class<?>... javaTypes) {
        this.javaTypes = List.of(javaTypes);
    }

    public static String getColumnTypeName(Class<?> javaType) {
        for (ColumnType columnType : ColumnType.values()) {
            if (columnType.javaTypes.contains(javaType)) {
                return columnType.name();
            }
        }
        // 默认使用 TEXT 类型
        return TEXT.name();
    }
}
