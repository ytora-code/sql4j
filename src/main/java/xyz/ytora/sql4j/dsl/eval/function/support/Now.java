package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.coll.Colls;
import xyz.ytora.ytool.date.Dates;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 00:18:08
 * <br/>
 * 获取当前时间
 */
public class Now implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("now");
    }

    @Override
    public Object call(List<Object> args) {
        if (Colls.isNotEmpty(args)) {
            throw new IllegalArgumentException("now函数不能有参数：now()");
        }

        return Dates.now();
    }
}
