package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.coll.Colls;

import java.util.List;

/**
 * created by yangtong on 2025/8/15 17:22:24
 * <br/>
 */
public class Mill implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("mill");
    }

    @Override
    public Object call(List<Object> args) {
        if (Colls.isNotEmpty(args)) {
            throw new IllegalArgumentException("mill函数不能有参数：now()");
        }

        return System.currentTimeMillis();
    }
}
