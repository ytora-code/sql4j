package xyz.ytora.sql4j.orm;

import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.select.SelectBuilder;

import java.util.List;
import java.util.function.Consumer;

/**
 * 持久层接口
 */
public interface IRepo<T extends Entity<T>> {

    SQLHelper sqlHelper();

    /**
     * 查询符合条件的唯一数据
     */
    T one(SelectBuilder selectBuilder);

    T one(Consumer<ConditionExpressionBuilder> where);

    T one(ConditionExpressionBuilder where);

    T one(T where);

    /**
     * 查询符合条件的数据总条数
     */
    Long count(SelectBuilder selectBuilder);

    Long count(Consumer<ConditionExpressionBuilder> where);

    Long count(ConditionExpressionBuilder where);

    Long count(T where);

    /**
     * 查询符合条件的数据列表
     */
    List<T> list(SelectBuilder selectBuilder);

    List<T> list(Consumer<ConditionExpressionBuilder> where);

    List<T> list(ConditionExpressionBuilder where);

    List<T> list(T where);

    /**
     * 分页查询符合条件的数据列表
     */
    Page<T> page(Integer pageNo, Integer pageSize, SelectBuilder selectBuilder);

    Page<T> page(Integer pageNo, Integer pageSize, Consumer<ConditionExpressionBuilder> where);

    Page<T> page(Integer pageNo, Integer pageSize, ConditionExpressionBuilder where);

    Page<T> page(Integer pageNo, Integer pageSize, T where);

    /**
     * 插入数据
     */
    void insert(T entity);

    /**
     * 批量插入
     */
    void insert(List<T> entities);

    /**
     * 根据指定条件修改数据
     */
    void update(T entity, Consumer<ConditionExpressionBuilder> where);

    void update(T entity, ConditionExpressionBuilder where);

    /**
     * 根据指定条件删除数据
     */
    void delete(Consumer<ConditionExpressionBuilder> where);

    void delete(ConditionExpressionBuilder where);

    void delete(T where);

}
