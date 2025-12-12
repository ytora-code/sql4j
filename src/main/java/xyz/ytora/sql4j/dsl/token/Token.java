package xyz.ytora.sql4j.dsl.token;

/**
 * TOKEN
 */
public class Token {
    public TokenType type;
    public String literal;
    // 在源字符串中的起始位置（含）
    public int startIndex;
    // 结束位置（不含）
    public int endIndex;

    public Token(TokenType type, String literal) {
        this(type, literal, -1, -1);
    }

    public Token(TokenType type, String literal, int startIndex, int endIndex) {
        this.type = type;
        this.literal = literal;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public String toString() {
        return type + "(" + literal + ")";
    }
}
