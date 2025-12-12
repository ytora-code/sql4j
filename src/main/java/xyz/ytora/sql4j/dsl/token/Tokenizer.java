package xyz.ytora.sql4j.dsl.token;

import java.util.ArrayList;
import java.util.List;

/**
 * TOKEN词法分析器
 */
public class Tokenizer {

    private final String input;
    private int pos = 0;
    private final int length;

    public Tokenizer(String input) {
        this.input = input;
        this.length = input.length();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < length) {
            char c = peek();

            if (Character.isWhitespace(c)) {
                // 跳过空白
                advance();
                continue;
            }

            if (c == '(') {
                int start = pos;
                advance();
                tokens.add(new Token(TokenType.LPAREN, "(", start, pos));
                continue;
            }

            if (c == ')') {
                int start = pos;
                advance();
                tokens.add(new Token(TokenType.RPAREN, ")", start, pos));
                continue;
            }

            if (c == ',') {
                int start = pos;
                advance();
                tokens.add(new Token(TokenType.COMMA, ",", start, pos));
                continue;
            }

            if (c == '\'' || c == '"') {
                tokens.add(readString());
                continue;
            }

            if (Character.isDigit(c)) {
                tokens.add(readNumber());
                continue;
            }

            if (isIdentifierStart(c)) {
                tokens.add(readWord());
                continue;
            }

            if (isOperatorChar(c)) {
                tokens.add(readOperator());
                continue;
            }

            throw new RuntimeException("无法识别的字符：'" + c + "'，位置：" + pos);
        }

        tokens.add(new Token(TokenType.EOF, "", pos, pos));
        return tokens;
    }

    // ===== 基础游标操作 =====

    private char peek() {
        if (pos >= length) return '\0';
        return input.charAt(pos);
    }

    private void advance() {
        pos++;
    }

    // ===== 读取各类 Token =====

    /**
     * 读取字符串字面量（当前未实现转义）
     */
    private Token readString() {
        int start = pos;
        char quote = peek();
        advance(); // 跳过起始引号
        StringBuilder sb = new StringBuilder();

        while (pos < length && peek() != quote) {
            sb.append(peek());
            advance();
        }

        if (pos == length) {
            throw new RuntimeException("字符串未闭合，从索引 " + start + " 开始。");
        }

        advance(); // 跳过结束引号
        return new Token(TokenType.STRING, sb.toString(), start, pos);
    }

    /**
     * 读取数字（允许一个小数点）
     */
    private Token readNumber() {
        int start = pos;
        StringBuilder sb = new StringBuilder();
        boolean hasDot = false;

        while (pos < length) {
            char ch = peek();
            if (Character.isDigit(ch)) {
                sb.append(ch);
                advance();
            } else if (ch == '.' && !hasDot) {
                hasDot = true;
                sb.append(ch);
                advance();
            } else {
                break;
            }
        }

        return new Token(TokenType.NUMBER, sb.toString(), start, pos);
    }

    /**
     * 读取以字母/下划线开头的单词（标识符/关键字/复合关键字前项）
     * 关键字、操作符判断用小写；IDENT 的字面量保留原样。
     */
    private Token readWord() {
        int start = pos;
        StringBuilder sb = new StringBuilder();
        while (pos < length && (Character.isLetterOrDigit(peek()) || peek() == '-' || peek() == '.' || peek() == '_')) {
            sb.append(peek());
            advance();
        }
        String raw = sb.toString();           // 原样（保留大小写）
        String word = raw.toLowerCase();      // 用于判断关键字/操作符

        // 复合操作符（携带跨两词位置）
        Token compound = handleCompoundOperators(word, start);
        if (compound != null) {
            return compound;
        }

        // 单词/关键字/基本操作符
        return switch (word) {
            case "and" -> new Token(TokenType.AND, raw, start, pos);
            case "or" -> new Token(TokenType.OR, raw, start, pos);
            case "null", "empty", "true", "false" -> new Token(TokenType.KEYWORD, raw, start, pos);
            case "is" -> new Token(TokenType.IS, raw, start, pos);
            case "in" -> new Token(TokenType.IN, raw, start, pos);
            case "like" -> new Token(TokenType.LIKE, raw, start, pos);
            case "between" -> new Token(TokenType.BETWEEN, raw, start, pos);
            default -> new Token(TokenType.IDENT, raw, start, pos);
        };
    }

    /**
     * 处理各类操作符：
     * - 双字符优先：==, !=, >=, <=, ??, ~=, ^=, $=, *=
     * - 单字符：>, <, +, -, *, /, %, !
     * 规则：'!' 优先尝试 '!='，否则作为一元取反 BANG，并强制“紧贴后项”。
     */
    private Token readOperator() {
        int start = pos;
        char first = peek();
        advance();

        if (pos < length) {
            char second = peek();
            String two = "" + first + second;
            switch (two) {
                case "==" -> {
                    advance();
                    return new Token(TokenType.EQ, "==", start, pos);
                }
                case "!=" -> {
                    advance();
                    return new Token(TokenType.NE, "!=", start, pos);
                }
                case ">=" -> {
                    advance();
                    return new Token(TokenType.GE, ">=", start, pos);
                }
                case "<=" -> {
                    advance();
                    return new Token(TokenType.LE, "<=", start, pos);
                }
                case "??" -> {
                    advance();
                    return new Token(TokenType.COALESCE, "??", start, pos);
                }
                case "~=" -> {
                    advance();
                    return new Token(TokenType.REGEX_MATCH, "~=", start, pos);
                }
                case "^=" -> {
                    advance();
                    return new Token(TokenType.STARTS_WITH, "^=", start, pos);
                }
                case "$=" -> {
                    advance();
                    return new Token(TokenType.ENDS_WITH, "$=", start, pos);
                }
                case "*=" -> {
                    advance();
                    return new Token(TokenType.CONTAINS, "*=", start, pos);
                }
            }
        }

        // 单字符操作符
        switch (first) {
            case '>' -> {
                return new Token(TokenType.GT, ">", start, pos);
            }
            case '<' -> {
                return new Token(TokenType.LT, "<", start, pos);
            }
            case '+' -> {
                return new Token(TokenType.PLUS, "+", start, pos);
            }
            case '-' -> {
                return new Token(TokenType.MINUS, "-", start, pos);
            }
            case '*' -> {
                return new Token(TokenType.STAR, "*", start, pos);
            }
            case '/' -> {
                return new Token(TokenType.SLASH, "/", start, pos);
            }
            case '%' -> {
                return new Token(TokenType.PERCENT, "%", start, pos);
            }
            case '!' -> {
                char next = (pos < length) ? peek() : '\0';

                // 兜底再次识别 '!='（通常上面已处理，此处仅为稳妥）
                if (next == '=') {
                    advance();
                    return new Token(TokenType.NE, "!=", start, pos);
                }

                // “紧贴”规则：'!' 后不能是空白或输入结束
                if (next == '\0' || Character.isWhitespace(next)) {
                    throw new RuntimeException("一元取反 '!' 后必须紧贴表达式（不能有空格），错误位置：" + (pos - 1));
                }

                // 允许：另一个 '!'（支持 !!/!!!）、标识符起始、数字、左括号、引号
                if (next == '!' || isIdentifierStart(next) || Character.isDigit(next) || next == '(' || next == '\'' || next == '"') {
                    return new Token(TokenType.BANG, "!", start, pos);
                }

                // 其它（如 '!,', '!<', '!>' 等）不支持
                throw new RuntimeException("一元取反 '!' 后跟随了不支持的字符 '" + next + "'，位置：" + pos + "。");
            }
            case '=' -> {
                // 没有单独 '='
                throw new RuntimeException("不支持单独的 '='，位置：" + (pos - 1) + "。是否要写 '=='？");
            }
            case '~', '^', '$', '?' -> {
                // 单独出现 ~ ^ $ ? 在语法里无意义
                throw new RuntimeException("不支持的操作符 '" + first + "'，位置：" + (pos - 1) + "。");
            }
            default -> throw new RuntimeException("未知的操作符 '" + first + "'，位置：" + (pos - 1) + "。");
        }
    }

    // ===== 复合关键字处理 =====

    /**
     * 处理复合操作符：is not、not is、not in、not like、not between
     * 返回跨两词的 Token（带 startIndex/endIndex），否则返回 null。
     */
    private Token handleCompoundOperators(String currentWord, int startIndexOfCurrentWord) {
        switch (currentWord) {
            case "is" -> {
                String nextWord = peekNextWord();
                if ("not".equals(nextWord)) {
                    consumeNextWord(); // 把 not 消费掉（包含前导空白）
                    return new Token(TokenType.IS_NOT, "is not", startIndexOfCurrentWord, pos);
                }
                return null;
            }
            case "not" -> {
                String nextWord = peekNextWord();
                if (nextWord == null) {
                    throw new RuntimeException("关键字 'not' 后缺少后续单词，位置：" + pos + "。");
                }
                switch (nextWord) {
                    case "is":
                        consumeNextWord();
                        return new Token(TokenType.NOT_IS, "not is", startIndexOfCurrentWord, pos);
                    case "in":
                        consumeNextWord();
                        return new Token(TokenType.NOT_IN, "not in", startIndexOfCurrentWord, pos);
                    case "like":
                        consumeNextWord();
                        return new Token(TokenType.NOT_LIKE, "not like", startIndexOfCurrentWord, pos);
                    case "between":
                        consumeNextWord();
                        return new Token(TokenType.NOT_BETWEEN, "not between", startIndexOfCurrentWord, pos);
                    default:
                        return null; // 非复合关键字，交回上层按 IDENT/KEYWORD 处理（当前未作为关键字使用）
                }
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * 向前看下一个单词（不移动 pos）；若不存在则返回 null
     */
    private String peekNextWord() {
        int saved = pos;

        // 跳过空白
        while (pos < length && Character.isWhitespace(peek())) {
            advance();
        }

        if (pos >= length || !isIdentifierStart(peek())) {
            pos = saved;
            return null;
        }

        // 读取单词
        StringBuilder sb = new StringBuilder();
        while (pos < length && (Character.isLetterOrDigit(peek()) || peek() == '-' || peek() == '.' || peek() == '_')) {
            sb.append(peek());
            advance();
        }

        String word = sb.toString().toLowerCase();
        pos = saved; // 还原
        return word;
    }

    /**
     * 消费（跳过）紧随其后的一个单词（包含前导空白）
     */
    private void consumeNextWord() {
        // 跳过空白
        while (pos < length && Character.isWhitespace(peek())) {
            advance();
        }
        // 跳过单词
        while (pos < length && (Character.isLetterOrDigit(peek()) || peek() == '-' || peek() == '.' || peek() == '_')) {
            advance();
        }
    }

    // ===== 工具判断 =====

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isOperatorChar(char c) {
        return "=!<>+-*/%~^$?".indexOf(c) >= 0;
    }
}
