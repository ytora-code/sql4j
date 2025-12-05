package xyz.ytora.sql4j.core;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.caster.ITypeCaster;
import xyz.ytora.sql4j.caster.SQLReader;
import xyz.ytora.sql4j.caster.DefaultTypeCaster;
import xyz.ytora.sql4j.enums.DatabaseType;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL执行结果
 */
public class ExecResult {

    private SQLHelper sqlHelper;

    /**
     * 执行的 SQL 信息
     */
    private SqlInfo sqlInfo;

    /**
     * 数据库类型
     */
    private DatabaseType databaseType;

    /**
     * SELECT 执行结果（仅当 SQL 类型是 SELECT 时该字段才有值）
     */
    private List<Map<String, Object>> resultList;

    /**
     * 受影响的行数（仅当 SQL 类型是 INSERT、DELETE、UPDATE 时该字段才有值）
     */
    private Integer effectedRows;

    /**
     * 数据新增后，产生的主键ID（仅当 SQL 类型是 INSERT 时该字段才有值）
     */
    private List<Object> ids;

    /**
     * SQL 执行时间
     */
    private Long executionTime;

    /**
     * 执行状态，0-正常，1-before被拦截，2-执行出错
     */
    private Integer status;

    public void setSqlHelper(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    public SqlInfo getSqlInfo() {
        return sqlInfo;
    }

    public void setSqlInfo(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public Integer getEffectedRows() {
        return effectedRows;
    }

    public void setEffectedRows(Integer effectedRows) {
        this.effectedRows = effectedRows;
    }

    public List<Object> getIds() {
        return ids;
    }

    public void setIds(List<Object> ids) {
        this.ids = ids;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> toBeans(Class<T> clazz) {
        if (resultList.isEmpty()) {
            return Collections.emptyList();
        }
        if (Map.class.isAssignableFrom(clazz) || Object.class.equals(clazz)) {
            return (List<T>) resultList;
        }

        List<T> list = new ArrayList<>();
        for (Map<String, Object> row : resultList) {
            T bean = toBean(clazz, row);
            list.add(bean);
        }
        return list;
    }

    public <T> T toBean(Class<T> clazz) {
        return toBean(clazz, resultList.isEmpty() ? null : resultList.get(0));
    }

    @SuppressWarnings("unchecked")
    public <T> T toBean(Class<T> clazz, Map<String, Object> row) {
        if (Map.class.isAssignableFrom(clazz) || Object.class.equals(clazz)) {
            return (T) row;
        }
        if (String.class.equals(clazz)) {
            return (T) row.values().stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        try {
            // 实例化Bean对象
            T bean = clazz.getDeclaredConstructor().newInstance();
            if (row == null) {
                return bean;
            }
            ClassMetadata<T> classMetadata = ClassCache.get(clazz);
            Set<String> columns = row.keySet();
            // 获取所有 setter 方法
            List<MethodMetadata> setters = classMetadata.getMethods(m -> m.getName().startsWith("set") && m.parameters().size() == 1);
            for (MethodMetadata setter : setters) {
                String methodName = setter.getName();
                // 调用setter方法（setter方法定义：方法名称以set开头，并且参数个数等于1个）
                methodName = methodName.substring(3);
                Class<?> parameterType = setter.parameters().get(0).getType();
                // 获取数据库列名称
                String columnName;
                FieldMetadata fieldMetadata = setter.toField();
                Column anno = fieldMetadata.getAnnotation(Column.class);
                if (anno != null && !anno.value().isEmpty()) {
                    columnName = anno.value();
                } else {
                    columnName = Strs.toUnderline(fieldMetadata.getName());
                }
                if (columns.contains(columnName)) {
                    // 得到数据库中的原始值
                    Object value = row.get(columnName);

                    // 如果该字段类型实现了 SQLReader
                    if (SQLReader.class.isAssignableFrom(fieldMetadata.getSourceField().getType())) {
                        // 如果实现了 SQLReader，则需要回调read方法，获取其自定义的value
                        SQLReader fieldObj = (SQLReader) fieldMetadata.getSourceField().getType().getDeclaredConstructor().newInstance();
                        value = fieldObj.read(value);
                    }

                    // 如果是空值，直接赋值 NULL
                    if (value == null) {
                        setter.invoke(bean, (Object) null);
                    }
                    // 判断是否可以直接将原始值赋给该方法的第一个参数
                    else if (parameterType.isAssignableFrom(value.getClass())) {
                        setter.invoke(bean, value);
                    }
                    // 如果不能直接赋值，则要类型转换
                    else {
                        ITypeCaster typeCaster = sqlHelper.getTypeCaster();
                        value = typeCaster.cast(value, parameterType);
                        setter.invoke(bean, value);
                    }
                }
            }
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new Sql4JException(e);
        }
    }

    @Override
    public String toString() {
        return "ExecResult{" +
                "sqlInfo=" + sqlInfo +
                ", databaseType=" + databaseType +
                ", resultList=" + resultList +
                ", effectedRows=" + effectedRows +
                ", ids=" + ids +
                ", executionTime=" + executionTime +
                '}';
    }
}
