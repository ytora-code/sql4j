package xyz.ytora.sql4j.dsl.expr;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;
import xyz.ytora.sql4j.dsl.expr.node.support.*;
import xyz.ytora.sql4j.dsl.token.Token;
import xyz.ytora.sql4j.dsl.token.TokenType;
import xyz.ytora.ytool.number.Numbers;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yangtong on 2025/8/6 19:53:04
 * <br/>
 * AST 解析器（递归下降）
 */
public class ExprAstParser {
    private final List<Token> tokens;
    private int pos = 0;
    private final Boolean debugFlag;

    public ExprAstParser(List<Token> tokens) {
        this(tokens, false);
    }

    public ExprAstParser(List<Token> tokens, Boolean debugFlag) {
        this.tokens = tokens;
        this.debugFlag = (debugFlag != null) ? debugFlag : false;
        if (this.debugFlag) {
            System.out.println("Tokens:");
            for (int i = 0; i < tokens.size(); i++) {
                System.out.printf("[%d] %s (%s) @[%d,%d]%n",
                        i, tokens.get(i).type, tokens.get(i).literal, tokens.get(i).startIndex, tokens.get(i).endIndex);
            }
        }
    }

    /** 入口：解析一个完整表达式 */
    public ExprNode parseExpression() {
        ExprNode expr = parseOrExpr();
        // 顶层应当只剩 EOF；如果还剩下非右括号的 token，说明有多余输入或语法错误
        if (!isAtEnd() && peek().type != TokenType.RPAREN) {
            throw new RuntimeException("语法错误：在位置 " + pos + " 处遇到多余的标记 " + peek() + "。");
        }
        if (debugFlag) {
            System.out.println("解析完成：pos=" + pos + ", token=" + peek());
        }
        return expr;
    }

    /** orExpr := andExpr ( OR andExpr )* */
    private ExprNode parseOrExpr() {
        log("parseOrExpr");
        ExprNode left = parseAndExpr();
        while (match(TokenType.OR)) {
            Token op = prev();
            ExprNode right = parseAndExpr();
            left = new BinaryOpNode(op.literal, left, right);
        }
        return left;
    }

    /** andExpr := nullishExpr ( AND nullishExpr )* */
    private ExprNode parseAndExpr() {
        log("parseAndExpr");
        ExprNode left = parseNullishExpr(); // AND 下一级是 ??（空合并）
        while (match(TokenType.AND)) {
            Token op = prev();
            ExprNode right = parseNullishExpr();
            left = new BinaryOpNode(op.literal, left, right);
        }
        return left;
    }

    /** nullishExpr := comparisonExpr ( COALESCE nullishExpr )?   // 右结合 */
    private ExprNode parseNullishExpr() {
        log("parseNullishExpr");
        ExprNode left = parseComparisonExpr();
        if (match(TokenType.COALESCE)) { // a ?? (b ?? c)
            Token op = prev();
            ExprNode right = parseNullishExpr();
            return new BinaryOpNode(op.literal, left, right);
        }
        return left;
    }

    /**
     * comparisonExpr := additiveExpr ( compOp ( additiveExpr | tuple ) )*
     * compOp 包含：==、!=、>、>=、<、<=、IS/IS NOT/NOT IS/IN/NOT IN/LIKE/NOT LIKE/BETWEEN/NOT BETWEEN
     *            以及 ~=、^=、$=、*=
     *
     * 说明：此处允许链式比较（左结合）。若不想允许，可改为仅允许进入一次 while。
     */
    private ExprNode parseComparisonExpr() {
        log("parseComparisonExpr");
        ExprNode left = parseAdditiveExpr();

        while (match(
                TokenType.IS, TokenType.IS_NOT, TokenType.NOT_IS,
                TokenType.IN, TokenType.NOT_IN,
                TokenType.LIKE, TokenType.NOT_LIKE,
                TokenType.BETWEEN, TokenType.NOT_BETWEEN,
                TokenType.EQ, TokenType.NE, TokenType.LT, TokenType.LE, TokenType.GT, TokenType.GE,
                TokenType.REGEX_MATCH, TokenType.STARTS_WITH, TokenType.ENDS_WITH, TokenType.CONTAINS
        )) {
            Token op = prev();
            ExprNode right;

            // IN/BETWEEN 的右值必须是元组
            if (op.type == TokenType.BETWEEN || op.type == TokenType.NOT_BETWEEN ||
                    op.type == TokenType.IN || op.type == TokenType.NOT_IN) {
                right = parseTuple();
            } else {
                right = parseAdditiveExpr();
                if (debugFlag) {
                    System.out.println("    → 比较右侧子表达式解析完毕, pos=" + pos + ", token=" + peek());
                }
            }

            left = new BinaryOpNode(op.literal, left, right);
        }
        return left;
    }

    /** additiveExpr := multiplicativeExpr ( ('+'|'-') multiplicativeExpr )* */
    private ExprNode parseAdditiveExpr() {
        log("parseAdditiveExpr");
        ExprNode left = parseMultiplicativeExpr();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = prev();
            ExprNode right = parseMultiplicativeExpr();
            left = new BinaryOpNode(op.literal, left, right);
        }
        return left;
    }

    /** multiplicativeExpr := unaryExpr ( ('*'|'/'|'%') unaryExpr )* */
    private ExprNode parseMultiplicativeExpr() {
        log("parseMultiplicativeExpr");
        ExprNode left = parseUnaryExpr();
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token op = prev();
            ExprNode right = parseUnaryExpr();
            left = new BinaryOpNode(op.literal, left, right);
        }
        return left;
    }

    /**
     * unaryExpr := ('!' )* primaryExpr
     * 要求：相邻的 '!' 必须逐个紧贴；最后一个 '!' 与其后的表达式也必须紧贴。
     */
    private ExprNode parseUnaryExpr() {
        List<Token> bangs = new ArrayList<>();
        while (match(TokenType.BANG)) {
            bangs.add(prev());
        }

        if (!bangs.isEmpty()) {
            // 相邻 '!' 逐对校验紧贴
            for (int i = 1; i < bangs.size(); i++) {
                requireTightAfter(bangs.get(i - 1), bangs.get(i),
                        "一元取反 '!' 必须与下一个 '!' 紧贴。");
            }
            // 最后一个 '!' 与被取反表达式紧贴（此处使用 peek() 观察后项）
            Token next = peek();
            requireTightAfter(bangs.get(bangs.size() - 1), next,
                    "一元取反 '!' 后必须紧贴一个表达式（不能有空格）。");
        }

        ExprNode node = parsePrimaryExpr();

        // !!!x == !( !( !x ) )
        for (int i = bangs.size() - 1; i >= 0; i--) {
            node = new UnaryOpNode("!", node);
        }
        return node;
    }

    /** primaryExpr := '(' expression ')' | IDENT func? | STRING | NUMBER | KEYWORD */
    private ExprNode parsePrimaryExpr() {
        log("parsePrimaryExpr");

        if (match(TokenType.LPAREN)) {
            ExprNode expr = parseExpression();
            consume(TokenType.RPAREN, "缺少右括号 ')' 以结束括号表达式。");
            return expr;
        } else if (match(TokenType.IDENT)) {
            Token ident = prev();
            if (match(TokenType.LPAREN)) {
                return parseFunctionCall(ident.literal);
            } else {
                return new VariableNode(ident.literal);
            }
        } else if (match(TokenType.STRING)) {
            return new LiteralNode(prev().literal);
        } else if (match(TokenType.NUMBER)) {
            return new LiteralNode(Numbers.toNumber(prev().literal));
        } else if (match(TokenType.KEYWORD)) {
            String literal = prev().literal;
            if (literal.equalsIgnoreCase("true")) {
                return new LiteralNode(true);
            } else if (literal.equalsIgnoreCase("false")) {
                return new LiteralNode(false);
            } else if (literal.equalsIgnoreCase("null")) {
                return new LiteralNode(null);
            } else {
                // 其余关键字（如 empty）作为字面值字符串交由求值阶段处理
                return new LiteralNode(literal);
            }
        } else {
            throw new RuntimeException("意外的标记 '" + peek().literal + "'（类型 " + peek().type + "），解析位置：" + pos);
        }
    }

    /** tuple := '(' [ comparisonExpr (',' comparisonExpr)* ] ')' */
    private TupleNode parseTuple() {
        log("parseTuple");
        if (!match(TokenType.LPAREN)) {
            throw new RuntimeException("期望 '(' 用于开始元组。");
        }

        List<ExprNode> items = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                items.add(parseComparisonExpr());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "期望 ')' 以结束元组。");
        return new TupleNode(items);
    }

    /** functionCall := '(' [ comparisonExpr (',' comparisonExpr)* ] ')' */
    private ExprNode parseFunctionCall(String functionName) {
        log("parseFunctionCall");
        List<ExprNode> args = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                args.add(parseComparisonExpr());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "期望 ')' 以结束函数参数列表。");
        return new FunctionCallNode(functionName, args);
    }

    // ====== 公共小工具 ======

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) pos++;
        if (debugFlag) {
            System.out.println("→ advance 到 pos=" + pos + ", token=" + peek());
        }
        return prev();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token prev() {
        return tokens.get(pos - 1);
    }

    private void consume(TokenType type, String message) {
        if (check(type)) {
            advance();
        } else {
            throw new RuntimeException(message + " 实际读取到：" + peek());
        }
    }

    /**
     * 校验两个 token 是否“紧贴”（next.startIndex 必须等于 prev.endIndex）
     * 依赖 Token/Tokenizer 预先填充 startIndex/endIndex。
     */
    private void requireTightAfter(Token prevTok, Token nextTok, String message) {
        if (prevTok == null || nextTok == null) {
            throw new RuntimeException(message + "（缺少必要的标记）。");
        }
        if (prevTok.endIndex < 0 || nextTok.startIndex < 0) {
            throw new RuntimeException("缺少位置信息：请确认 Tokenizer 已设置 startIndex/endIndex。提示：" + message);
        }
        if (prevTok.endIndex != nextTok.startIndex) {
            throw new RuntimeException(message + " 违规标记：前一个=" + prevTok + "，后一个=" + nextTok + "，解析游标位置=" + pos);
        }
    }

    /** 调试日志 */
    private void log(String from) {
        if (debugFlag) {
            System.out.printf("[%-20s] pos=%-3d | token=%-15s (%s)%n", from, pos, peek().type, peek().literal);
        }
    }
}
