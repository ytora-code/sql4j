package xyz.ytora.sql4j.anno;

import xyz.ytora.sql4j.enums.DbType;
import xyz.ytora.sql4j.enums.IdType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰表实体类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface Table {

    /**
     * 表名称
     */
    String value() default "";

    /**
     * 主键类型
     */
    IdType idType() default IdType.NONE;

    /**
     * 如果该表不存在，就创建
     */
    boolean createIfNotExist() default false;

    /**
     * 表注释
     */
    String comment() default "";
}
