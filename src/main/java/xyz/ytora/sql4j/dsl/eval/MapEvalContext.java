package xyz.ytora.sql4j.dsl.eval;

import xyz.ytora.sql4j.dsl.eval.function.Func;
import xyz.ytora.sql4j.dsl.expr.node.support.FunctionCallNode;
import xyz.ytora.ytool.invoke.Reflects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * created by yangtong on 2025/8/6 21:35:17
 * <br/>
 */
public class MapEvalContext implements EvalContext {
    private final Map<String, Object> variables;

    public MapEvalContext() {
        this.variables = new HashMap<>();
    }

    public MapEvalContext put(String key, Object value) {
        variables.put(key, value);
        return this;
    }

    public MapEvalContext putAll(Map<String, Object> params) {
        variables.putAll(params);
        return this;
    }

    @Override
    public Object getVariable(String path) {
        String[] parts = path.split("\\.");
        Object current = variables;

        for (String part : parts) {
            // 没找到，则返回空
            if (current == null) {
                return null;
            }
            // map
            else if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            }
            // 数组
            else if (current instanceof List) {
                try {
                    int index = Integer.parseInt(part);
                    current = ((List<?>) current).get(index);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    return null;
                }
            }
            // 当作对象处理
            else {
                // 尝试用反射读取 Java Bean 属性
                try {
                    current = Reflects.getFieldValue(current, part);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return current;
    }

    @Override
    public Object callFunc(FunctionCallNode node, List<Object> args) {
        // 获取所有内置函数
        Set<Func> funcList = Func.funcList;
        for (Func func : funcList) {
            if (func.support(node)) {
                return func.call(args);
            }
        }

        throw new RuntimeException("Unknown function: " + node.functionName);
    }
}

