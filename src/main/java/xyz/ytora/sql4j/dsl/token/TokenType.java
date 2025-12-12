package xyz.ytora.sql4j.dsl.token;

/**
 * TOKEN类型
 */
public enum TokenType {
    IDENT,      // 变量名、函数名
    STRING,     // 字符串：'abc' or "abc"
    NUMBER,     // 数值：123, 3.14
    LPAREN,     // 左括号：(
    RPAREN,     // 右括号：)
    COMMA,      // 逗号：,

    // 逻辑一元操作符
    BANG,       // 取反

    // 逻辑二元操作符：==, !=, <, <=, >, >=等，使整个表达式返回bool值
    IS, IS_NOT, NOT_IS, IN, NOT_IN, LIKE, NOT_LIKE, BETWEEN, NOT_BETWEEN,
    EQ, NE, LT, LE, GT, GE, REGEX_MATCH, STARTS_WITH, ENDS_WITH, CONTAINS,

    // 算术二元操作符：加减乘除
    PLUS,            // +，可用于数字和字符串
    MINUS,           // -
    STAR,            // *
    SLASH,           // /
    PERCENT,         // %

    // 空合并（右结合）
    COALESCE,        // ??

    KEYWORD,    // 关键字：null, empty, true, false

    AND, OR,    // 逻辑操作符
    EOF         // 结束标记
}
