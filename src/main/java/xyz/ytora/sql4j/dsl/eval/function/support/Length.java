package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;

import java.util.Collection;
import java.util.List;

/**
 * created by yangtong on 2025/8/6 23:48:32
 * <br/>
 * 获取字符串或数组长度
 */
public class Length implements Func {

    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("length");
    }

    @Override
    public Object call(List<Object> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("length函数参数只能有一个：length(arr|string)");
        }
        Object arg = args.get(0);
        if (arg instanceof Collection) return ((Collection<?>) arg).size();
        if (arg instanceof String) return ((String) arg).length();
        throw new RuntimeException("Unsupported type for length(): " + arg);
    }
}
