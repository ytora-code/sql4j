package xyz.ytora.sql4j.util;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.core.ExecResult;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.support.Count;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.Entity;
import xyz.ytora.sql4j.orm.Page;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.update.SetStage;
import xyz.ytora.sql4j.sql.update.UpdateStage;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 封装了ORM操作
 */
public class OrmUtil {

    /**
     * 查询符合条件的唯一数据
     */
    public static <T extends Entity<T>> T one(Class<T> clazz, Consumer<ConditionExpressionBuilder> where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        SqlInfo sqlInfo = sqlHelper.select(clazz).from(clazz).where(where).end();
        ExecResult execResult = sqlHelper.getSqlExecutionEngine().executeSelect(sqlInfo);
        List<Map<String, Object>> resultList = execResult.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else if (resultList.size() == 1) {
            return execResult.toBean(clazz);
        } else {
            throw new Sql4JException(Strs.format("one: 根据条件只希望查到最多一条数据，实际查到[{}]", resultList.size()));
        }
    }

    /**
     * 查询符合条件的唯一数据
     */
    public static <T extends Entity<T>> T one(Class<T> clazz, T where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        return one(clazz, sqlHelper.toWhere(where));
    }

    /**
     * 查询符合条件的数据总条数
     */
    public static <T extends Entity<T>> long count(Class<T> clazz, Consumer<ConditionExpressionBuilder> where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        SqlInfo sqlInfo = sqlHelper.select(Count.of("1").as("count")).from(clazz).where(where).end();
        ExecResult execResult = sqlHelper.getSqlExecutionEngine().executeSelect(sqlInfo);
        return execResult.getResultList().size();
    }

    /**
     * 查询符合条件的数据总条数
     */
    public static <T extends Entity<T>> long count(Class<T> clazz, T where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        return count(clazz, sqlHelper.toWhere(where));
    }

    /**
     * 查询符合条件的数据列表
     */
    public static <T extends Entity<T>> List<T> list(Class<T> clazz, Consumer<ConditionExpressionBuilder> where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        return sqlHelper.select(clazz).from(clazz).where(where).submit(clazz);
    }

    /**
     * 查询符合条件的数据列表
     */
    public static <T extends Entity<T>> List<T> list(Class<T> clazz, T where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        return list(clazz, sqlHelper.toWhere(where));
    }

    /**
     * 分页查询符合条件的数据列表
     */
    public static <T extends Entity<T>> Page<T> page(Class<T> clazz, Integer pageNo, Integer pageSize, Consumer<ConditionExpressionBuilder> where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        // 1.查询符合条件的总数据量
        long total = count(clazz, where);
        // 2.根据分页信息计算出limit和offset
        int limit = pageSize;
        int offset = (pageNo - 1) * pageSize;
        // 3.查询并返回page
        List<T> execResult = sqlHelper.select(clazz).from(clazz).where(where).limit(limit).offset(offset).submit(clazz);
        Page<T> page = new Page<>(pageNo, pageSize);
        page.setPages((int) ((total + pageSize - 1) / pageSize));
        page.setTotal(total);
        page.setRecords(execResult);
        return page;
    }

    /**
     * 分页查询符合条件的数据列表
     */
    public static <T extends Entity<T>> Page<T> page(Class<T> clazz, Integer pageNo, Integer pageSize, T where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        return page(clazz, pageNo, pageSize, sqlHelper.toWhere(where));
    }

    /**
     * 插入数据
     */
    public static <T extends Entity<T>> void insert(Class<T> clazz, T entity) {
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        insert(clazz, entities);
    }

    /**
     * 批量插入
     */
    public static <T extends Entity<T>> void insert(Class<T> clazz, List<T> entities) {
        SQLHelper sqlHelper = SQLHelper.getInstance();

        // 1.获取要插入的字段
        List<MethodMetadata> insertColumns = new ArrayList<>();
        List<MethodMetadata> getters = Sql4jUtil.getter(clazz);
        for (MethodMetadata getter : getters) {
            // INSERT 字段
            FieldMetadata fieldMetadata = getter.toField();
            Column columnAnno = fieldMetadata.getAnnotation(Column.class);
            if (columnAnno != null && !columnAnno.exist()) {
                continue;
            }
            insertColumns.add(getter);
        }

        // 2.获取要插入的数据
        List<List<Object>> insertedDataList = new ArrayList<>();
        for (T entity : entities) {
            List<Object> params = new ArrayList<>();
            for (MethodMetadata getter : insertColumns) {
                try {
                    // 反射获取数据
                    Object val = getter.invoke(entity);
                    params.add(val);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new Sql4JException(e);
                }
            }
            insertedDataList.add(params);
        }

        // 3.插入数据
        List<SFunction<Object, ?>> insertColumnList = new ArrayList<>();
        for (MethodMetadata getter : insertColumns) {
            FieldMetadata fieldMetadata = getter.toField();
            String columnName;
            Column columnAnno = fieldMetadata.getAnnotation(Column.class);
            if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                columnName = columnAnno.value();
            } else {
                columnName = Strs.toUnderline(fieldMetadata.getName());
            }
            insertColumnList.add(Raw.of(columnName));
        }

        // 依次赋予对象新增数据的ID
        List<Object> ids = sqlHelper.insert(clazz).into(insertColumnList).values(insertedDataList).submit();
        for (int i = 0; i < entities.size(); i++) {
            Object id = ids.get(i);
            T entity = entities.get(i);
            entity.setId(id == null ? null : id.toString());
        }
    }

    /**
     * 根据指定条件修改数据
     */
    public static <T extends Entity<T>> void update(Class<T> clazz, T entity, Consumer<ConditionExpressionBuilder> where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        List<MethodMetadata> getters = Sql4jUtil.getter(clazz);
        Map<String, Object> setMap = new HashMap<>();
        for (MethodMetadata getter : getters) {
            try {
                Object val = getter.invoke(entity);
                if (val != null) {
                    Column columnAnno = getter.getAnnotation(Column.class);
                    String columnName;
                    if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                        columnName = columnAnno.value();
                    } else {
                        columnName = Strs.toUnderline(getter.getName());
                    }
                    setMap.put(columnName, val);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new Sql4JException(e);
            }
        }
        if (setMap.isEmpty()) {
            throw new Sql4JException("UPDATE 时实体对象的字段值不能全为 NULL");
        }
        UpdateStage updateStage = sqlHelper.update(clazz);
        SetStage setStage = null;
        for (String key : setMap.keySet()) {
            setStage = updateStage.set(Raw.of(key), setMap.get(key));
        }

        if (setStage != null) {
            setStage.where(where).submit();
        }
    }

    /**
     * 根据指定条件删除数据
     */
    public static <T extends Entity<T>> void delete(Class<T> clazz, Consumer<ConditionExpressionBuilder> where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        sqlHelper.delete().from(clazz).where(where).submit();
    }

    /**
     * 根据指定条件删除数据
     */
    public static <T extends Entity<T>> void delete(Class<T> clazz, T where) {
        SQLHelper sqlHelper = SQLHelper.getInstance();
        delete(clazz, sqlHelper.toWhere(where));
    }

}
