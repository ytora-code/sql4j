package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 00:01:48
 * <br/>
 * 去除字符串两边的空字符
 */
public class Trim implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("trim");
    }

    @Override
    public Object call(List<Object> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("trim函数只能有一个参数：trim(string)");
        }
        Object arg = args.get(0);
        if (arg == null) {
            return null;
        }
        if (!(arg instanceof String)) {
            throw new IllegalArgumentException("trim函数的参数必须是字符串，当前是【" + arg.getClass().getSimpleName() + "】");
        }

        return ((String) arg).trim();
    }
}
