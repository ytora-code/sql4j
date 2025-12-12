package xyz.ytora.sql4j.dsl.eval.function.support;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.coll.Colls;
import xyz.ytora.ytool.number.Numbers;

import java.util.List;
import java.util.Objects;

/**
 * created by yangtong on 2025/8/7 00:19:32
 * <br/>
 * 乘法函数
 */
public class Mul implements Func {
    @Override
    public Boolean support(FunctionCallNode functionCallNode) {
        return functionCallNode.functionName.equalsIgnoreCase("mul");
    }

    @Override
    public Object call(List<Object> args) {
        args = args.stream().filter(Objects::nonNull).toList();
        if (Colls.isEmpty(args)) {
            return null;
        }

        Number num = Numbers.toNumber(args.get(0).toString());
        for (int i = 1; i < args.size(); i++) {
            num = Numbers.mul(Numbers.toNumber(args.get(i).toString()), num);
        }

        return num;
    }

}