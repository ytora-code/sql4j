package xyz.yangtong.sql4j.anno;

import xyz.yangtong.sql4j.enums.IdType;

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
    String value();

    /**
     * 表名称（语义化字段，比value优先级更高）
     */
    String name() default "";

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

    /**
     * 数据源
     */
    String ds() default "";
}
