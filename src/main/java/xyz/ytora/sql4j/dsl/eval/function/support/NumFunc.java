package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.number.Numbers;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 00:01:48
 * <br/>
 * 去除字符串两边的空字符
 */
public class NumFunc implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("number");
    }

    @Override
    public Object call(List<Object> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("number函数只能有一个参数：number(string)");
        }
        Object arg = args.get(0);
        if (arg == null) {
            return null;
        }
        if (!(arg instanceof String)) {
            throw new IllegalArgumentException("number函数的参数必须是字符串，当前是【" + arg.getClass().getSimpleName() + "】");
        }

        return Numbers.toNumber(arg.toString());
    }
}
