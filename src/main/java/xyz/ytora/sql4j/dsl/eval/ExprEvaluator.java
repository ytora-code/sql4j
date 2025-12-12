package xyz.ytora.sql4j.dsl.eval;

import xyz.ytora.sql4j.dsl.expr.node.ExprNode;
import xyz.ytora.sql4j.dsl.expr.node.support.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * created by yangtong on 2025/8/6 21:35:59
 * <br/>
 * 表达式AST树计算器（BigDecimal算术 + 正则保护）
 */
public class ExprEvaluator {

    // 可调：算术精度/舍入
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    public Object evaluate(ExprNode node, EvalContext context) {
        if (node instanceof LiteralNode) {
            return ((LiteralNode) node).value;
        }
        if (node instanceof VariableNode) {
            String name = ((VariableNode) node).name;
            return context.getVariable(name);
        }
        if (node instanceof UnaryOpNode) {
            return evalUnary((UnaryOpNode) node, context);
        }
        if (node instanceof BinaryOpNode) {
            return evalBinary((BinaryOpNode) node, context);
        }
        if (node instanceof FunctionCallNode) {
            return evalFunction((FunctionCallNode) node, context);
        }
        if (node instanceof TupleNode) {
            List<Object> values = new ArrayList<>();
            for (ExprNode item : ((TupleNode) node).elements) {
                values.add(evaluate(item, context));
            }
            return values;
        }
        throw new RuntimeException("未知的AST节点类型：" + node.getClass().getName());
    }

    // ===== 一元运算 =====

    private Object evalUnary(UnaryOpNode node, EvalContext context) {
        Object v = evaluate(node.operand, context);
        String op = node.operator;
        if ("!".equals(op)) {
            return !toBoolean(v);
        }
        throw new RuntimeException("不支持的一元操作符：" + op);
    }

    // ===== 二元运算（短路优先） =====

    private Object evalBinary(BinaryOpNode node, EvalContext context) {
        String op = node.operator.toLowerCase();
        Object left = evaluate(node.left, context);

        // 短路：and
        if ("and".equals(op)) {
            if (!toBoolean(left)) return false;
            return toBoolean(evaluate(node.right, context));
        }

        // 短路：or
        if ("or".equals(op)) {
            if (toBoolean(left)) return true;
            return toBoolean(evaluate(node.right, context));
        }

        // 短路：??（空合并，右结合）
        if ("??".equals(op)) {
            if (!isNullish(left)) return left;     // 左值不“空”，直接返回左值
            return evaluate(node.right, context);  // 否则取右值
        }

        // 其余运算需要右值
        Object right = evaluate(node.right, context);

        return switch (op) {
            // 相等 / 比较 / 谓词
            case "==", "is" -> equal(left, right);
            case "!=", "not is", "is not" -> !equal(left, right);
            case ">" -> compare(left, right) > 0;
            case ">=" -> compare(left, right) >= 0;
            case "<" -> compare(left, right) < 0;
            case "<=" -> compare(left, right) <= 0;

            case "like" -> like(left, right);
            case "not like" -> !like(left, right);
            case "in" -> in(left, right);
            case "not in" -> !in(left, right);
            case "between" -> between(left, right);
            case "not between" -> !between(left, right);

            // 字符串关系
            case "~=" -> regexMatch(left, right);
            case "^=" -> startsWith(left, right);
            case "$=" -> endsWith(left, right);
            case "*=" -> contains(left, right);

            // 算术 / 拼接
            case "+" -> plus(left, right);
            case "-" -> minus(left, right);
            case "*" -> multiply(left, right);
            case "/" -> divide(left, right);
            case "%" -> mod(left, right);

            default -> throw new RuntimeException("未知的二元操作符：" + op);
        };
    }

    // ===== 函数调用 =====

    private Object evalFunction(FunctionCallNode node, EvalContext context) {
        List<Object> args = new ArrayList<>();
        for (ExprNode argNode : node.arguments) {
            args.add(evaluate(argNode, context));
        }
        return context.callFunc(node, args);
    }

    // ===== 布尔与“空”判定 =====

    private Boolean toBoolean(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean b) return b;
        if (val instanceof String s) return !s.isBlank();
        if (val.getClass().isArray()) return Array.getLength(val) > 0;
        if (val instanceof Map<?, ?> m) return !m.isEmpty();
        if (val instanceof Iterable<?> it) return it.iterator().hasNext();
        return true; // 其他非空对象为真
    }

    // “空值”定义：null、空白串、空数组、空集合、空 map。数字 0 不是空。
    private boolean isNullish(Object val) {
        if (val == null) return true;
        if (val instanceof String s) return s.isBlank();
        if (val.getClass().isArray()) return Array.getLength(val) == 0;
        if (val instanceof Map<?, ?> m) return m.isEmpty();
        if (val instanceof Iterable<?> it) return !it.iterator().hasNext();
        return false;
    }

    // ===== 相等与比较 =====

    private boolean equal(Object left, Object right) {
        // 特殊字面量 "empty"
        if (Objects.equals(right, "empty")) {
            if (left == null) return true;
            else if (left instanceof String strLeft) return strLeft.isBlank();
            else if (left.getClass().isArray()) return Array.getLength(left) == 0;
            else if (left instanceof Map<?, ?> map) return map.isEmpty();
            else if (left instanceof Iterable<?> iterable) return !iterable.iterator().hasNext();
            else
                throw new IllegalArgumentException("empty 只能和字符串、集合、map、数组比较，现在比较对象：" + left.getClass().getName());
        }

        // 数值等价：1 与 1.0 视为相等
        if (left instanceof Number && right instanceof Number) {
            return toBigDecimal((Number) left).compareTo(toBigDecimal((Number) right)) == 0;
        }

        return Objects.equals(left, right);
    }

    private int compare(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return toBigDecimal((Number) a).compareTo(toBigDecimal((Number) b));
        }
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        }
        throw new RuntimeException("不支持的比较操作：类型 " + typeName(a) + " 与 " + typeName(b));
    }

    // ===== LIKE 家族 =====

    private boolean like(Object a, Object b) {
        if (a == null || b == null) return false;
        String aStr = a.toString();
        String p = b.toString();

        // 全模糊
        if (p.startsWith("%") && p.endsWith("%") && p.length() >= 2) {
            return aStr.contains(p.substring(1, p.length() - 1));
        }
        // 左模糊
        if (p.startsWith("%")) {
            return aStr.endsWith(p.substring(1));
        }
        // 右模糊
        if (p.endsWith("%")) {
            return aStr.startsWith(p.substring(0, p.length() - 1));
        }

        // 如果你希望“无通配符时等价于完全匹配”，把下行改为：return aStr.equals(p);
        return false;
    }

    // ===== IN / BETWEEN =====

    private boolean in(Object left, Object right) {
        if (!(right instanceof List)) {
            throw new RuntimeException("IN 操作的右操作数必须是元组(List)。");
        }
        for (Object elem : (List<?>) right) {
            if (equal(left, elem)) return true;
        }
        return false;
    }

    private boolean between(Object val, Object tuple) {
        if (!(tuple instanceof List) || ((List<?>) tuple).size() != 2) {
            throw new RuntimeException("BETWEEN 的右值必须是长度为 2 的元组。");
        }
        Object min = ((List<?>) tuple).get(0);
        Object max = ((List<?>) tuple).get(1);
        return compare(val, min) >= 0 && compare(val, max) <= 0;
    }

    // ===== 字符串关系运算 =====

    private boolean regexMatch(Object a, Object b) {
        if (a == null || b == null) return false;
        String text = a.toString();
        String pattern = b.toString();
        try {
            // 如需大小写忽略：Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
            return Pattern.compile(pattern).matcher(text).matches();
            // “包含式匹配”可改为 .find()
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("无效的正则表达式：\"" + pattern + "\"。错误：" + e.getDescription());
        }
    }

    private boolean startsWith(Object a, Object b) {
        if (a == null || b == null) return false;
        return a.toString().startsWith(b.toString());
    }

    private boolean endsWith(Object a, Object b) {
        if (a == null || b == null) return false;
        return a.toString().endsWith(b.toString());
    }

    private boolean contains(Object a, Object b) {
        if (a == null || b == null) return false;
        return a.toString().contains(b.toString());
    }

    // ===== 算术 / 拼接（Number 使用 BigDecimal，字符串拼接保持原行为） =====

    private Object plus(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return toBigDecimal((Number) a).add(toBigDecimal((Number) b), MC);
        }
        // 只要有一边不是 Number，则做字符串拼接（null 按空串处理）
        return String.valueOf(a == null ? "" : a) + String.valueOf(b == null ? "" : b);
    }

    private Object minus(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return toBigDecimal((Number) a).subtract(toBigDecimal((Number) b), MC);
        }
        throw new RuntimeException("操作符 '-' 仅支持数字类型，实际为：" + typeName(a) + " 与 " + typeName(b));
    }

    private Object multiply(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return toBigDecimal((Number) a).multiply(toBigDecimal((Number) b), MC);
        }
        throw new RuntimeException("操作符 '*' 仅支持数字类型，实际为：" + typeName(a) + " 与 " + typeName(b));
    }

    private Object divide(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            BigDecimal divisor = toBigDecimal((Number) b);
            if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                throw new RuntimeException("除数不能为 0。");
            }
            // 可根据业务选择保留小数位，这里用 MC 控制精度
            return toBigDecimal((Number) a).divide(divisor, MC);
        }
        throw new RuntimeException("操作符 '/' 仅支持数字类型，实际为：" + typeName(a) + " 与 " + typeName(b));
    }

    private Object mod(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            BigDecimal bv = toBigDecimal((Number) b);
            if (bv.compareTo(BigDecimal.ZERO) == 0) {
                throw new RuntimeException("取模的除数不能为 0。");
            }
            BigDecimal av = toBigDecimal((Number) a);
            // BigDecimal 的余数：使用 remainder；如需与整数取模一致性，确保两侧是整数
            return av.remainder(bv, MC);
        }
        throw new RuntimeException("操作符 '%' 仅支持数字类型，实际为：" + typeName(a) + " 与 " + typeName(b));
    }

    // ===== 工具方法 =====

    private String typeName(Object o) {
        return o == null ? "null" : o.getClass().getSimpleName();
    }

    private BigDecimal toBigDecimal(Number n) {
        if (n instanceof BigDecimal bd) return bd;
        if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
            return new BigDecimal(n.longValue());
        }
        if (n instanceof Float || n instanceof Double) {
            // 避免二进制小数误差，用字符串构造更稳妥
            return new BigDecimal(n.toString());
        }
        // 兜底：尽量用字符串
        return new BigDecimal(n.toString());
    }
}
