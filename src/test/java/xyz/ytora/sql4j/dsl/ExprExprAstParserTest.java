package xyz.ytora.sql4j.dsl;

import org.junit.jupiter.api.Test;
import xyz.ytora.sql4j.dsl.eval.ExprEvaluator;
import xyz.ytora.sql4j.dsl.eval.MapEvalContext;
import xyz.ytora.sql4j.dsl.expr.ExprAstParser;
import xyz.ytora.sql4j.dsl.expr.ExprAstPrinter;
import xyz.ytora.sql4j.dsl.expr.node.ExprNode;
import xyz.ytora.sql4j.dsl.token.Token;
import xyz.ytora.sql4j.dsl.token.Tokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表达式解析测试用例
 */
public class ExprExprAstParserTest {

    /**
     * 测试token解析
     */
    @Test
    public void testTokenParser() {
        Map<String, Object> person = new HashMap<>();
        person.put("name", "张三");
        person.put("age", 12);
        person.put("abc", true);
        person.put("hobby", List.of(1, 2, 3, 4, 5, 6));
        MapEvalContext context = new MapEvalContext()
                .put("person", person)
                .put("age", List.of(1, 2, 3, 4, 5))
                .put("title", "测试分张三为氛围二位分为");
        testEval("fwefewfw ?? ffff ?? person.hobby.3 ?? 12312 + 2", context);
    }

    @Test
    public void Tokenizer() {
        String dslString = "(1 + 2 * 3 - 4) + 'hahah'";
        Tokenizer tokenizer = new Tokenizer(dslString);
        List<Token> tokens = tokenizer.tokenize();

        System.out.println(tokens);

        ExprAstParser exprAstParser = new ExprAstParser(tokens);
        ExprNode ast = exprAstParser.parseExpression();
        ExprAstPrinter.printAst(ast);
    }

    /**
     * 测试DSL计算
     */
    public void testEval(String dslString, MapEvalContext context) {
//        String dslString = "(person.name is not null and person.age > 20) or (length(person.hobby) > 1 and title  like '%打游戏%')";
//        String dslString = "title like concat('%', person.name, '%')";


        Tokenizer tokenizer = new Tokenizer(dslString);
        List<Token> tokens = tokenizer.tokenize();

        ExprAstParser exprAstParser = new ExprAstParser(tokens);
        ExprNode ast = exprAstParser.parseExpression();
        ExprAstPrinter.printAst(ast);

        ExprEvaluator exprEvaluator = new ExprEvaluator();
        Object evaluate = exprEvaluator.evaluate(ast, context);
        System.out.println(evaluate);
    }

    private static void testExpr(String input) {
        System.out.println("Input DSL: " + input);
        try {
            // 1. 分词
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.tokenize();
            System.out.println("Tokens:");
            // tokens.forEach(System.out::println);

            // 2. 构建 AST
            ExprAstParser exprAstParser = new ExprAstParser(tokens);
            ExprNode ast = exprAstParser.parseExpression();

            //3. 打印AST
            ExprAstPrinter.printAst(ast);
        } catch (Exception e) {
            System.out.println("❌ 解析失败：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--------------------------------------------------\n");
    }

}
