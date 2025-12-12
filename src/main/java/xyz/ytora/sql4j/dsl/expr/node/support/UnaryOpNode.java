package xyz.ytora.sql4j.dsl.expr.node.support;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;

/**
 * created by yangtong on 2025/8/6 19:50:59
 * <br/>
 * 一元操作节点
 */
public class UnaryOpNode implements ExprNode {
    // !
    public String operator;
    public ExprNode operand;

    public UnaryOpNode(String operator, ExprNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
}
