package xyz.ytora.sql4j.enums;

/**
 * 数据库对象类型
 */
public enum DbObjType {

    TABLE,
    VIEW,
    MATERIALIZED_VIEW,

    SEQUENCE,

    FUNCTION,
    PROCEDURE,

    TYPE,

    // 依附对象（一般不作为 schema 直系返回）
    COLUMN,
    INDEX,
    CONSTRAINT,
    TRIGGER
}
