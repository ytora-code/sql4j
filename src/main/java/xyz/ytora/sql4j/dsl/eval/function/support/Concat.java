package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.coll.Colls;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 09:55:37
 * <br/>
 * 拼接字符串
 */
public class Concat implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("concat");
    }

    @Override
    public Object call(List<Object> args) {
        if (Colls.isEmpty(args)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg == null) {
                sb.append("null");
            } else {
                sb.append(arg);
            }
        }

        return sb.toString();
    }
}