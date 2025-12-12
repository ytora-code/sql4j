package xyz.ytora.sql4j.dsl.expr.node.support;


import xyz.ytora.sql4j.dsl.expr.node.ExprNode;

/**
 * created by yangtong on 2025/8/6 19:49:14
 * <br/>
 * 一个最小的表达式节点，比如 name is 'zs'
 */
public class BinaryOpNode implements ExprNode {
    /**
     * 操作符，and, or, ==, is, >, ...
     */
    public String operator;
    /**
     * 左值
     */
    public ExprNode left;
    /**
     * 右值
     */
    public ExprNode right;

    public BinaryOpNode(String operator, ExprNode left, ExprNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
}
