package xyz.ytora.sql4j.dsl.expr.node.support;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;

/**
 * created by yangtong on 2025/8/6 19:47:48
 * <br/>
 * 字符串、数字、null、true/false/empty等字面量节点
 */
public class LiteralNode implements ExprNode {

    public Object value;

    public String getType() {
        if (value == null) {
            return "null";
        }
        else if (value instanceof String) {
            return "string";
        }
        else if (value instanceof Number) {
            return "number";
        }
        else if (value instanceof Boolean) {
            return "boolean";
        }
        else {
            return "unknown";
        }
    }

    public LiteralNode(Object value) {
        this.value = value;
    }

}
