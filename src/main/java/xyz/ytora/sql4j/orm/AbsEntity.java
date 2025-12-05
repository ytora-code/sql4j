package xyz.ytora.sql4j.orm;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.sql.update.SetStage;
import xyz.ytora.sql4j.sql.update.UpdateBuilder;
import xyz.ytora.sql4j.sql.update.UpdateStage;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    private final Class<T> entityClass;
    private final ClassMetadata<T> classMetadata;
    private final Map<MethodMetadata, SFunction<Object, ?>> columnMap = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public AbsEntity() {
        this.entityClass = (Class<T>) this.getClass();
        this.classMetadata = ClassCache.get(entityClass);
        List<MethodMetadata> methodMetadata = classMetadata.getMethods(mmd -> mmd.getName().startsWith("get") || mmd.getName().startsWith("is"));

        for (MethodMetadata mmd : methodMetadata) {
            FieldMetadata fieldMetadata = mmd.toField();
            String columnName;
            Column columnAnno = fieldMetadata.getAnnotation(Column.class);
            if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                columnName = columnAnno.value();
            } else {
                columnName = Strs.toUnderline(fieldMetadata.getName());
            }
            columnMap.put(mmd, Raw.of(columnName));
        }
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public List<T> select() {
        return SQLHelper.getInstance()
                .select(entityClass)
                .from(entityClass)
                .where(SQLHelper.getInstance().toWhere(this))
                .submit(entityClass);
    }

    /**
     * 将当前实体类增加到数据库
     */
    public void insert() {
        // 获取该对象所有 getter 方法
        List<MethodMetadata> methodMetadata = classMetadata.getMethods(mmd -> mmd.getName().startsWith("get") || mmd.getName().startsWith("is"));
        List<Object> params = new ArrayList<>();
        for (MethodMetadata metadata : methodMetadata) {
            try {
                Object val = metadata.invoke(this);
                params.add(val);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new Sql4JException(e);
            }
        }
        List<Object> ids = SQLHelper.getInstance().insert(entityClass).into(columnMap.values()).value(params).submit();
        if (ids.isEmpty()) {
            throw new Sql4JException("INSERT 异常，插入数据后没有返回ID，请检查");
        }
        this.id = String.valueOf(ids.get(0));
    }

    /**
     * 根据id修改数据
     */
    public void update() {
        if (id == null) {
            throw new Sql4JException("UPDATE 时实体对象的 ID 不能为空");
        }
        // 获取该对象所有 getter 方法
        List<MethodMetadata> methodMetadata = classMetadata.getMethods(mmd -> mmd.getName().startsWith("get") || mmd.getName().startsWith("is"));
        Map<String, Object> setMap = new HashMap<>();
        for (MethodMetadata mmd : methodMetadata) {
            try {
                Object val = mmd.invoke(this);
                if (val != null) {
                    Column columnAnno = mmd.getAnnotation(Column.class);
                    String columnName;
                    if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                        columnName = columnAnno.value();
                    } else {
                        columnName = Strs.toUnderline(mmd.getName());
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

    public void delete() {
        SQLHelper.getInstance().delete().from(entityClass).where(SQLHelper.getInstance().toWhere(this)).submit();
    }
}
