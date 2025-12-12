package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.coll.Colls;
import xyz.ytora.ytool.id.Ids;

import java.util.List;

/**
 * created by yangtong on 2025/8/7 00:19:32
 * <br/>
 */
public class Id implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("id");
    }

    @Override
    public Object call(List<Object> args) {
        if (Colls.isNotEmpty(args)) {
            throw new IllegalArgumentException("id函数不能有参数：id()");
        }

        return Ids.snowflakeId();
    }
}