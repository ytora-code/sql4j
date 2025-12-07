package xyz.ytora.sql4j.orm;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.sql.update.SetStage;
import xyz.ytora.sql4j.sql.update.UpdateBuilder;
import xyz.ytora.sql4j.sql.update.UpdateStage;
import xyz.ytora.sql4j.util.Sql4jUtil;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象实体类
 */
public class AbsEntity<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 114514L;

    /**
     * 主键id
     */
    private String id;

    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public List<T> select() {
        Class<T> entityClass = (Class<T>) this.getClass();
        return SQLHelper.getInstance()
                .select(entityClass)
                .from(entityClass)
                .where(SQLHelper.getInstance().toWhere(this))
                .submit(entityClass);
    }

    /**
     * 将当前实体类增加到数据库
     */
    @SuppressWarnings("unchecked")
    public void insert() {
        Class<T> entityClass = (Class<T>) this.getClass();
        // 获取该对象所有 getter 方法
        List<MethodMetadata> getters = Sql4jUtil.getter(entityClass);
        List<SFunction<Object, ?>> insertColumns = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (MethodMetadata getter : getters) {
            try {
                // INSERT 字段
                FieldMetadata fieldMetadata = getter.toField();
                String columnName;
                Column columnAnno = fieldMetadata.getAnnotation(Column.class);
                if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                    columnName = columnAnno.value();
                } else {
                    columnName = Strs.toUnderline(fieldMetadata.getName());
                }
                insertColumns.add(Raw.of(columnName));

                // INSERT 的数据
                Object val = getter.invoke(this);
                params.add(val);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new Sql4JException(e);
            }
        }

        List<Object> ids = SQLHelper.getInstance().insert(entityClass).into(insertColumns).value(params).submit();
        if (ids.isEmpty()) {
            throw new Sql4JException("INSERT 异常，插入数据后没有返回ID，请检查");
        }
        this.id = String.valueOf(ids.get(0));
    }

    /**
     * 根据id修改数据
     */
    @SuppressWarnings("unchecked")
    public void update() {
        if (id == null) {
            throw new Sql4JException("UPDATE 时实体对象的 ID 不能为空");
        }
        Class<T> entityClass = (Class<T>) this.getClass();
        // 获取该对象所有 getter 方法
        List<MethodMetadata> getters = Sql4jUtil.getter(entityClass);
        Map<String, Object> setMap = new HashMap<>();
        for (MethodMetadata getter : getters) {
            try {
                Object val = getter.invoke(this);
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

        UpdateStage updateStage = new UpdateStage(new UpdateBuilder(SQLHelper.getInstance()), entityClass);
        SetStage setStage = null;
        for (String key : setMap.keySet()) {
            setStage = updateStage.set(Raw.of(key), setMap.get(key));
        }

        if (setStage != null) {
            setStage.where(w -> w.eq(Raw.of("id"), id)).submit();
        }
    }

    @SuppressWarnings("unchecked")
    public void delete() {
        Class<T> entityClass = (Class<T>) this.getClass();
        SQLHelper.getInstance().delete().from(entityClass).where(SQLHelper.getInstance().toWhere(this)).submit();
    }
}
