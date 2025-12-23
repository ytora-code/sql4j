package xyz.ytora.sql4j.sql;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.enums.SegmentType;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.select.AbsSelect;
import xyz.ytora.sql4j.util.Sql4jUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 带有条件的表达式构造父类（用于 WHERE / HAVING / ON）
 * <p>
 * 使用方式示例：
 * <pre>
 * w -> w.eq(User1::getName, 1)
 *       .ne(User2::getName, 2)
 *       .and(w1 -> w1.eq(User2::getId, User1::getId)
 *                    .or()
 *                    .gt(User3::getAge, 12))
 *       .or()
 *       .isNotNull(User3::getAge);
 * </pre>
 */
public class ExpressionBuilder extends AbsSql {

    /**
     * 条件表达式 SQL 片段
     */
    protected final StringBuilder expression = new StringBuilder();

    /**
     * SQL的占位符参数
     */
    protected final List<Object> params = new ArrayList<>();

    /**
     * 上一个片段类型，用于自动补 AND、控制空格
     */
    protected SegmentType lastType = SegmentType.START;

    public ExpressionBuilder(AliasRegister register) {
        this.register = register;
    }

    // ===================== 提供给外部的方法 =====================

    /**
     * 获取构造好的条件表达式
     */
    public String build() {
        return expression.toString().trim();
    }

    /**
     * 获取占位符参数
     */
    public List<Object> getParams() {
        return params;
    }

    // ===================== 取反 =====================
    /**
     * 取反
     */
    public ExpressionBuilder not() {
        appendLogical("NOT");
        return this;
    }

    // ===================== 条件运算符 =====================

    /**
     * 等值匹配：column = value
     */
    public <T> ExpressionBuilder eq(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append("=").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 等值匹配：column1 = column2
     */
    public <L, R> ExpressionBuilder eq(SFunction<L, ?> leftColumn, SFunction<R, ?> rightColumn) {
        appendPredicateStart();
        String left = parsePlaceholder(leftColumn);
        String right = parsePlaceholder(rightColumn);
        expression.append(left).append(SPACE).append("=").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 不等于：column != value
     */
    public <T> ExpressionBuilder ne(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append("<>").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 不等于：column1 != column2
     */
    public <L, R> ExpressionBuilder ne(SFunction<L, ?> leftColumn, SFunction<R, ?> rightColumn) {
        appendPredicateStart();
        String left = parsePlaceholder(leftColumn);
        String right = parsePlaceholder(rightColumn);
        expression.append(left).append(SPACE).append("<>").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 大于：column > value
     */
    public <T> ExpressionBuilder gt(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append(">").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 大于等于：column >= value
     */
    public <T> ExpressionBuilder ge(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append(">=").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 小于：column < value
     */
    public <T> ExpressionBuilder lt(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append("<").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 小于等于：column <= value
     */
    public <T> ExpressionBuilder le(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append("<=").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * LIKE：column like value
     */
    public <T> ExpressionBuilder like(SFunction<T, ?> column, Object value) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        String right = parsePlaceholder(value);
        expression.append(left).append(SPACE).append("LIKE").append(SPACE).append("'%").append(right).append("%'");
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 在xx范围：id in (1, 2, 3)
     */
    public <T> ExpressionBuilder in(SFunction<T, ?> column, Object... values) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        List<Object> valueList = Arrays.stream(values).flatMap(v -> {
            if (v instanceof Collection<?> coll) {
                return coll.stream();
            } else {
                return Stream.of(v);
            }
        }).toList();
        String right = '(' +
                valueList.stream().map(this::parsePlaceholder).collect(Collectors.joining(", ")) +
                ')';
        expression.append(left).append(SPACE).append("IN").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * IN 子查询
     */
    public <T> ExpressionBuilder in(SFunction<T, ?> column, AbsSelect subSelect) {
        int columnCount = subSelect.getSelectBuilder().getSelectStage().getSelectColumns().size();
        if (columnCount != 1) {
            throw new Sql4JException("IN 子查询时，查询字段必须为1，目前查询字段：【" + columnCount + "】");
        }
        appendPredicateStart();
        SqlInfo sqlInfo = subSelect.getSelectBuilder().getTranslator().translate(subSelect.getSelectBuilder());
        params.addAll(sqlInfo.getOrderedParms());
        String left = parsePlaceholder(column);
        String right = "(" + sqlInfo.getSql() + ")";
        expression.append(left).append(SPACE).append("IN").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * 在xx范围：id between 1 and 2
     */
    public <T> ExpressionBuilder betweenAnd(SFunction<T, ?> column, Object leftValue, Object rightValue) {
        appendPredicateStart();
        String c = parsePlaceholder(column);
        String left = parsePlaceholder(leftValue);
        String right = parsePlaceholder(rightValue);
        expression.append(c).append(SPACE).append("BETWEEN").append(SPACE).append(left).append(SPACE).append("AND").append(SPACE).append(right);
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * IS NULL
     */
    public <T> ExpressionBuilder isNull(SFunction<T, ?> column) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        expression.append(left).append(SPACE).append("IS NULL");
        lastType = SegmentType.PREDICATE;
        return this;
    }

    /**
     * IS NOT NULL
     */
    public <T> ExpressionBuilder isNotNull(SFunction<T, ?> column) {
        appendPredicateStart();
        String left = parsePlaceholder(column);
        expression.append(left).append(SPACE).append("IS NOT NULL");
        lastType = SegmentType.PREDICATE;
        return this;
    }

    // ===================== 逻辑运算符 =====================

    /**
     * 追加 AND（用于连续简单条件）
     * <pre> w.eq(...).and().ne(...)</pre>
     */
    public ExpressionBuilder and() {
        appendLogical("AND");
        return this;
    }

    /**
     * 追加带括号的 AND 子表达式
     * <pre> w.and(w1 -> w1.eq(...).or().gt(...)) </pre>
     * 等价于 AND ( ... )
     */
    public ExpressionBuilder and(Consumer<ExpressionBuilder> nested) {
        if (nested == null) {
            return and();
        }
        appendLogical("AND");
        expression.append("(");
        lastType = SegmentType.OPEN_PAREN;

        ExpressionBuilder inner = new ExpressionBuilder(register);
        nested.accept(inner);
        expression.append(inner.build());
        expression.append(")");
        lastType = SegmentType.PREDICATE;

        // 把子表达式参数按顺序合并进来
        this.params.addAll(inner.getParams());
        return this;
    }

    /**
     * 追加 OR（用于连续简单条件）
     * <pre> w.eq(...).or().ne(...)</pre>
     */
    public ExpressionBuilder or() {
        appendLogical("OR");
        return this;
    }

    /**
     * 追加带括号的 OR 子表达式
     * <pre> w.or(w1 -> w1.eq(...).gt(...)) </pre>
     * 等价于 OR ( ... )
     */
    public ExpressionBuilder or(Consumer<ExpressionBuilder> nested) {
        if (nested == null) {
            return or();
        }
        appendLogical("OR");
        expression.append("(");
        lastType = SegmentType.OPEN_PAREN;

        ExpressionBuilder inner = new ExpressionBuilder(register);
        nested.accept(inner);
        expression.append(inner.build());
        expression.append(")");
        lastType = SegmentType.PREDICATE;

        // 把子表达式参数按顺序合并进来
        this.params.addAll(inner.getParams());
        return this;
    }

    // ===================== 内部工具方法 =====================

    /**
     * 在追加一个新的条件前，如果前面已经有条件，则默认补一个 AND
     * <p>
     * 规则：
     * - 开头：不加 AND
     * - 上一个是逻辑运算符（AND/OR）：不加 AND
     * - 上一个是 "("：不加 AND
     * - 上一个是一个完整条件：补 AND
     */
    protected void appendPredicateStart() {
        if (lastType == SegmentType.PREDICATE) {
            expression.append(SPACE).append("AND").append(SPACE);
            lastType = SegmentType.LOGICAL;
        }
    }

    /**
     * 追加逻辑运算符
     */
    protected void appendLogical(String op) {
        // 如果是开头或刚刚是逻辑运算符，则不重复追加
        if (lastType == SegmentType.START || lastType == SegmentType.LOGICAL) {
            return;
        }
        expression.append(SPACE).append(op).append(SPACE);
        lastType = SegmentType.LOGICAL;
    }

    /**
     * 解析并拼接参数
     */
    protected String parsePlaceholder(Object value) {
        // 不需要拼接占位符，直接拼接到 SQL 字符串
        if (value instanceof Wrapper) {
            return ((Wrapper) value).getRealValue();
        }
        // 函数，特殊处理，本质也是字符串
        else if (value instanceof SQLFunc func) {
            func.addAliasRegister(register);
            return func.getValue();
        }
        // 普通字段，例如 SysUser::getName
        else if (value instanceof SFunction) {
            return Sql4jUtil.parseColumn((SFunction) value, register);
        }
        // value 是字面量
        else {
            // 需要拼接占位符，并将真实参数记录下来
            params.add(value);
            return "?";
        }
    }


}
