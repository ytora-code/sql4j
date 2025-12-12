package xyz.ytora.sql4j.dsl.eval;

import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;

import java.util.List;

/**
 * created by yangtong on 2025/8/6 21:34:03
 * <br/>
 * 上下文接口
 */
public interface EvalContext {
    /**
     * 得到根据变量名称得到值
     * @param name 变量名称
     * @return 值
     */
    Object getVariable(String name);

    /**
     * 函数调用
     * @param node 函数
     * @param args 参数
     * @return 函数计算结果
     */
    Object callFunc(FunctionCallNode node, List<Object> args);
}
