package xyz.ytora.sql4j.dsl.expr.node.support;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;

import java.util.List;

/**
 * created by yangtong on 2025/8/6 19:52:21
 * <br/>
 * 元组
 */
public class TupleNode implements ExprNode {
    public List<ExprNode> elements;

    public TupleNode(List<ExprNode> elements) {
        this.elements = elements;
    }
}
