package org.ytor.sql4j.enums;

/**
 * 主键类型
 */
public enum IdType {
    /**
     * 数据库自增主键，依赖数据库自带的自增机制
     */
    AUTO_INCREMENT(),

    /**
     * UUID，通常是36位字符串形式，分布式环境使用较多
     */
    UUID(),

    /**
     * UCID
     */
    UCID(),

    /**
     * 雪花算法生成的64位长整型主键，分布式环境下高效且有序
     */
    SNOWFLAKE(),

    /**
     * 无规则，手动赋值
     */
    NONE();
}
