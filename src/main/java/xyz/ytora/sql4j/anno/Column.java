package xyz.ytora.sql4j.anno;

import xyz.ytora.sql4j.enums.ColumnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public @interface Column {

    /**
     * 字段名称
     */
    String value() default "";

    /**
     * 数据库字段类型
     */
    ColumnType type() default ColumnType.NONE;

    /**
     * 字段是否非空
     */
    boolean notNull() default false;

    /**
     * 字段注释
     */
    String comment() default "";

    /**
     * 字段是否存在
     */
    boolean exist() default true;
}
