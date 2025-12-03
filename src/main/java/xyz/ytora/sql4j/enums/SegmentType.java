package xyz.ytora.sql4j.enums;

/**
 * 条件表达式的片段类型
 */
public enum SegmentType {
    START,        // 表达式开头
    PREDICATE,    // 已经追加过一个完整条件（a = b）
    LOGICAL,      // 刚刚追加过 AND / OR
    OPEN_PAREN    // 刚刚追加过 "("
}
