package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 00:13:01
 * <br/>
 * 如果第一个参数为空或空字符串，则返回第二个参数的值
 */
public class IfEmpty implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("ifempty");
    }

    @Override
    public Object call(List<Object> args) {
        if (args.size() != 2) {
            throw new IllegalArgumentException("trim函数只能有2个参数：ifempty(param1, param2)");
        }
        Object param1 = args.get(0);
        Object param2 = args.get(1);
        if (param1 == null) {
            return param2;
        }
        if (!(param1 instanceof String)) {
            throw new IllegalArgumentException("trim函数的第一个参数必须为字符串，现在为【" + param1.getClass().getSimpleName() + "】");
        }

        return ((String) param1).isBlank() ? param2 : param1;
    }
}
