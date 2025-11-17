package org.ytor.sql4j.sql;

import org.ytor.sql4j.func.SFunction;

import java.util.function.Consumer;

/**
 * 带有条件的表达式构造父类（用于 WHERE / HAVING / ON）
 */
public class ConditionExpressionBuilder extends ExpressionBuilder {

    public ConditionExpressionBuilder(AliasRegister register) {
        super(register);
    }

    // 覆盖父类的方法，返回子类类型
    @Override
    public <T> ConditionExpressionBuilder eq(SFunction<T, ?> column, Object value) {
        super.eq(column, value);
        return this;
    }

    @Override
    public <L, R> ConditionExpressionBuilder eq(SFunction<L, ?> leftColumn, SFunction<R, ?> rightColumn) {
        super.eq(leftColumn, rightColumn);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder ne(SFunction<T, ?> column, Object value) {
        super.ne(column, value);
        return this;
    }

    @Override
    public <L, R> ConditionExpressionBuilder ne(SFunction<L, ?> leftColumn, SFunction<R, ?> rightColumn) {
        super.ne(leftColumn, rightColumn);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder gt(SFunction<T, ?> column, Object value) {
        super.gt(column, value);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder ge(SFunction<T, ?> column, Object value) {
        super.ge(column, value);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder lt(SFunction<T, ?> column, Object value) {
        super.lt(column, value);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder le(SFunction<T, ?> column, Object value) {
        super.le(column, value);
        return this;
    }

    @Override
    public <T> ExpressionBuilder like(SFunction<T, ?> column, Object value) {
        super.like(column, value);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder isNull(SFunction<T, ?> column) {
        super.isNull(column);
        return this;
    }

    @Override
    public <T> ConditionExpressionBuilder isNotNull(SFunction<T, ?> column) {
        super.isNotNull(column);
        return this;
    }

    @Override
    public ConditionExpressionBuilder and() {
        super.and();
        return this;
    }

    @Override
    public ConditionExpressionBuilder and(Consumer<ExpressionBuilder> nested) {
        super.and(nested);
        return this;
    }

    @Override
    public ConditionExpressionBuilder or() {
        super.or();
        return this;
    }

    @Override
    public ConditionExpressionBuilder or(Consumer<ExpressionBuilder> nested) {
        super.or(nested);
        return this;
    }

    /*=========================== ConditionExpressionBuilder特有的方法 =================================*/

    /**
     * 等值匹配：column1 = value
     */
    public <T> ConditionExpressionBuilder eq(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return eq(column, value);
        }
        return this;
    }

    /**
     * 等值匹配：column1 = column2
     */
    public <L, R> ConditionExpressionBuilder eq(boolean condition, SFunction<L, ?> leftColumn, SFunction<R, ?> rightColumn) {
        if (condition) {
            return eq(leftColumn, rightColumn);
        }
        return this;
    }

    /**
     * 不等于：column != value
     */
    public <T> ConditionExpressionBuilder ne(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return ne(column, value);
        }
        return this;
    }

    /**
     * 不等于：column1 != column2
     */
    public <L, R> ConditionExpressionBuilder ne(boolean condition, SFunction<L, ?> leftColumn, SFunction<R, ?> rightColumn) {
        if (condition) {
            return ne(leftColumn, rightColumn);
        }
        return this;
    }

    /**
     * 大于：column > value
     */
    public <T> ConditionExpressionBuilder gt(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return gt(column, value);
        }
        return this;
    }

    /**
     * 大于等于：column >= value
     */
    public <T> ConditionExpressionBuilder ge(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return ge(column, value);
        }
        return this;
    }

    /**
     * 小于：column < value
     */
    public <T> ConditionExpressionBuilder lt(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return lt(column, value);
        }
        return this;
    }

    /**
     * 小于等于：column <= value
     */
    public <T> ConditionExpressionBuilder le(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return le(column, value);
        }
        return this;
    }

    /**
     * LIKE：column like value
     */
    public <T> ExpressionBuilder like(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return like(column, value);
        }
        return this;
    }

    /**
     * IS NULL
     */
    public <T> ConditionExpressionBuilder isNull(boolean condition, SFunction<T, ?> column) {
        if (condition) {
            return isNull(column);
        }
        return this;
    }

    /**
     * IS NOT NULL
     */
    public <T> ConditionExpressionBuilder isNotNull(boolean condition, SFunction<T, ?> column) {
        if (condition) {
            return isNotNull(column);
        }
        return this;
    }

    public ConditionExpressionBuilder and(boolean condition, Consumer<ExpressionBuilder> nested) {
        if (condition) {
            return and(nested);
        }
        return this;
    }

    public ConditionExpressionBuilder or(boolean condition, Consumer<ExpressionBuilder> nested) {
        if (condition) {
            return or(nested);
        }
        return this;
    }
}
