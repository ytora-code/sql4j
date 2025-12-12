package xyz.ytora.sql4j.dsl.eval.function;

import xyz.ytora.sql4j.dsl.eval.function.support.*;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * created by yangtong on 2025/8/6 23:33:50
 * <br/>
 * DSL函数
 */
public interface Func {

    /**
     * 获取所有内置函数
     */
    Set<Func> funcList = new HashSet<>() {
        {
            add(new Length());
            add(new Trim());
            add(new IfNull());
            add(new IfEmpty());
            add(new Now());
            add(new Id());
            add(new Concat());
            add(new Mill());
            add(new NumFunc());
            add(new StrFunc());
            add(new Mul());
            add(new Div());
            add(new Add());
            add(new Sub());
        }
    };

    /**
     * 注册函数
     */
    static void register(Func func) {
        funcList.add(func);
    }

    /**
     * 判断是否支持改函数
     */
    Boolean support(FunctionCallNode functionCallNode);

    /**
     * 函数调用
     * @param args 参数
     * @return 返回值
     */
    Object call(List<Object> args);

}
