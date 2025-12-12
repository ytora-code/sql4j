package xyz.ytora.sql4j.dsl.expr.node.support;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;

/**
 * created by yangtong on 2025/8/6 19:48:46
 * <br/>
 * 变量节点
 */
public class VariableNode implements ExprNode {
    public String name;

    public VariableNode(String name) {
        this.name = name;
    }
}
