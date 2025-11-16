package org.ytor.sql4j.core;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.caster.TypeCaster;
import org.ytor.sql4j.enums.DatabaseType;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.util.StrUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    public <T> List<T> toBeans(Class<T> clazz) {
        if (resultList.isEmpty()) {
            return Collections.emptyList();
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return (List<T>) resultList;
        }

        List<T> list = new ArrayList<T>();
        for (Map<String, Object> row : resultList) {
            T bean = toBean(clazz, row);
            list.add(bean);
        }
        return list;
    }

    public <T> T toBean(Class<T> clazz) {
        return toBean(clazz, resultList.isEmpty() ? null : resultList.get(0));
    }

    public <T> T toBean(Class<T> clazz, Map<String, Object> row) {
        try {
            T bean = clazz.newInstance();
            if (row == null) {
                return bean;
            }
            Set<String> columns = row.keySet();
            for (Method method : clazz.getMethods()) {
                // 调用setter方法（setter方法定义：方法名称以set开头，并且参数个数等于1个）
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    String fieldName = method.getName().substring(3);
                    fieldName = StrUtil.toLowerUnderline(fieldName);
                    if (columns.contains(fieldName)) {
                        // 得到数据库中的原始值
                        Object value = row.get(fieldName);
                        // 判断是否可以直接将原始值赋给该方法的第一个参数
                        if (parameterType.isAssignableFrom(value.getClass())) {
                            method.invoke(bean, value);
                        }
                        // 如果不能直接赋值，则要类型转换
                        else {
                            TypeCaster typeCaster = sqlHelper.getTypeCaster();
                            value = typeCaster.cast(value, parameterType);
                            method.invoke(bean, value);
                        }
                    }
                }
            }
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
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
