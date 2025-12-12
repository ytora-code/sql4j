package xyz.ytora.sql4j.dsl.expr;


import xyz.ytora.sql4j.dsl.expr.node.ExprNode;
import xyz.ytora.sql4j.dsl.expr.node.support.*;

/**
 * AST 打印器（中文、兼容性好，不依赖 switch 模式匹配）
 */
public class ExprAstPrinter {

    public static void printAst(ExprNode node) {
        printAst(node, "", true);
    }

    private static void printAst(ExprNode node, String prefix, boolean isLast) {
        String connector = isLast ? "└── " : "├── ";
        System.out.print(prefix + connector);

        if (node == null) {
            System.out.println("〈空节点〉");
            return;
        }

        // 依次判断具体类型（避免使用 JDK 预览特性）
        if (node instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) node;
            System.out.println("一元操作: " + u.operator);
            printAst(u.operand, childPrefix(prefix, isLast), true);
            return;
        }

        if (node instanceof BinaryOpNode) {
            BinaryOpNode bin = (BinaryOpNode) node;
            // 直接打印操作符字面量：支持 == != > >= < <= is/not in/like/between 以及 ~= ^= $= *= ?? + - * / %
            System.out.println("二元操作: " + bin.operator);
            printAst(bin.left, childPrefix(prefix, isLast), false);
            printAst(bin.right, childPrefix(prefix, isLast), true);
            return;
        }

        if (node instanceof VariableNode) {
            VariableNode var = (VariableNode) node;
            System.out.println("变量: " + var.name);
            return;
        }

        if (node instanceof LiteralNode) {
            LiteralNode lit = (LiteralNode) node;
            System.out.println("字面量: " + formatLiteral(lit.value));
            return;
        }

        if (node instanceof FunctionCallNode) {
            FunctionCallNode func = (FunctionCallNode) node;
            System.out.println("函数调用: " + func.functionName + " (参数个数=" + func.arguments.size() + ")");
            for (int i = 0; i < func.arguments.size(); i++) {
                printAst(func.arguments.get(i), childPrefix(prefix, isLast), i == func.arguments.size() - 1);
            }
            return;
        }

        if (node instanceof TupleNode) {
            TupleNode tuple = (TupleNode) node;
            System.out.println("元组: (size=" + tuple.elements.size() + ")");
            for (int i = 0; i < tuple.elements.size(); i++) {
                ExprNode elem = tuple.elements.get(i);
                String idxPrefix = childPrefix(prefix, isLast);
                // 在每个元素前标注索引
                System.out.print(idxPrefix + (i == tuple.elements.size() - 1 ? "└── " : "├── "));
                System.out.println("[#" + i + "]");
                // 元素本身再作为一层子节点打印
                printAst(elem, idxPrefix + (i == tuple.elements.size() - 1 ? "    " : "│   "), true);
            }
            return;
        }

        // 未知节点兜底
        System.out.println("未知节点类型: " + node.getClass().getSimpleName());
    }

    /** 计算子层级前缀（保持树形美观） */
    private static String childPrefix(String prefix, boolean isLast) {
        return prefix + (isLast ? "    " : "│   ");
    }

    /** 将字面量按类型美化输出 */
    private static String formatLiteral(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) {
            // 字符串用双引号包裹，替换内部换行便于单行展示
            String shown = s.replace("\n", "\\n").replace("\r", "\\r");
            return "\"" + shown + "\"";
        }
        if (v instanceof Character c) {
            return "'" + c + "'";
        }
        // 布尔与数字直接输出
        return String.valueOf(v);
    }
}
