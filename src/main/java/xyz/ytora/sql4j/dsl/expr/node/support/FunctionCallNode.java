package xyz.ytora.sql4j.dsl.expr.node.support;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;

import java.util.List;

/**
 * created by yangtong on 2025/8/6 19:51:24
 * <br/>
 * 函数表达式节点
 */
public class FunctionCallNode implements ExprNode {
    /**
     * 函数名称
     */
    public String functionName;
    /**
     * 参数
     */
    public List<ExprNode> arguments;

    public FunctionCallNode(String functionName, List<ExprNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }
}
