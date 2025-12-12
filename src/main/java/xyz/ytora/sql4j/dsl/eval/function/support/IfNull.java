package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 00:08:32
 * <br/>
 * 如果第一个参数为空，则返回第二个参数的值
 */
public class IfNull implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("ifnull");
    }

    @Override
    public Object call(List<Object> args) {
        if (args.size() != 2) {
            throw new IllegalArgumentException("trim函数只能有2个参数：ifnull(param1, param2)");
        }
        Object param1 = args.get(0);
        Object param2 = args.get(1);
        return param1 != null ? param1 : param2;
    }
}
