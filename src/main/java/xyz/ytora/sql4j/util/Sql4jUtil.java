package xyz.ytora.sql4j.util;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.orm.autofill.ColumnFiller;
import xyz.ytora.sql4j.orm.autofill.ColumnFillerAdapter;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.invoke.Reflects;
import xyz.ytora.ytool.str.Strs;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具类
 */
public class Sql4jUtil {

    private final static String methodName = "writeReplace";

    /**
     * 实体类中标有自动填充注解的列的映射
     */
    private final static Map<Class<?>, Map<String, Class<? extends ColumnFiller>>> fillerMapper = new ConcurrentHashMap<>();

    /**
     * ColumnFiller.class 于 ColumnFiller 对象的映射
     */
    private final static Map<Class<?>, ColumnFiller> fillerObjMapper = new ConcurrentHashMap<>();

    /**
     * 根据对象类型格式化 SQL 参数值
     */
    public static String formatVal(Object val) {
        if (val == null) {
            return "NULL";
        }
        if (val instanceof String || val instanceof Character) {
            return "'" + escapeSingleQuote(String.valueOf(val)) + "'";
        }
        if (val instanceof BigDecimal) {
            return ((BigDecimal) val).toPlainString();
        }
        if (val instanceof Number) {
            return val.toString();
        }
        if (val instanceof Boolean) {
            // 按常见习惯转 1/0，也可以根据数据库定制
            return ((Boolean) val) ? "1" : "0";
        }
        if (val instanceof Date) {
            // 简单处理，实际项目建议使用占位符+参数绑定
            return "'" + val + "'";
        }
        // 其他类型统一按字符串处理
        return "'" + escapeSingleQuote(val.toString()) + "'";
    }

    /**
     * SQL中的单引号转义
     */
    public static String escapeSingleQuote(String str) {
        return str.replace("'", "''");
    }

    /**
     * 根据方法引用对象解析出SerializedLambda
     */
    public static <T> SerializedLambda serializedLambda(SFunction<T, ?> fn) {
        try {
            // 根据方法引用 fn ，得到SerializedLambda对象
            Method writeReplace = fn.getClass().getDeclaredMethod(methodName);
            writeReplace.setAccessible(true);
            return (SerializedLambda) writeReplace.invoke(fn);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new Sql4JException(e);
        }
    }

    /**
     * 根据 SerializedLambda 得到全类名
     */
    public static String parseClassPath(SerializedLambda sl) {
        String mt = sl.getInstantiatedMethodType();
        int start = mt.indexOf('L');
        int end = mt.indexOf(';', start);
        if (start >= 0 && end > 1) {
            return mt.substring(start + 1, end).replace('/', '.');
        }
        throw new Sql4JException("根据方法引用解析class时出错：start:【" + start + "】, end:【" + end + "】");
    }

    /**
     * 根据 SerializedLambda 得到类
     */
    public static <T> Class<?> parseClass(SerializedLambda sl) {
        try {
            return Class.forName(parseClassPath(sl));
        } catch (ClassNotFoundException e) {
            throw new Sql4JException(e);
        }
    }

    /**
     * 根据 SerializedLambda 得到方法
     */
    public static String parseMethodName(SerializedLambda sl) {
        String methodName = sl.getImplMethodName();
        if (methodName == null) {
            throw new Sql4JException("方法名称不能为空！");
        }
        return methodName;
    }

    /**
     * 解析出拼接到SQL里面的字段，比如user.user_name
     */
    public static <T> String parseColumn(SFunction<T, ?> fn, AliasRegister register) {
        if (fn instanceof SQLFunc sqlFunc) {
            return sqlFunc.getValue();
        }
        SerializedLambda sl = serializedLambda(fn);
        Class<?> clazz = parseClass(sl);
        StringBuilder sb = new StringBuilder();
        // 如果不是单表，则要查表别名
        if (register != null && !register.single()) {
            String alias = register.getAlias(clazz);
            sb.append(alias).append('.');
        }

        // 方法
        String methodName = parseMethodName(sl);
        MethodMetadata methodMetadata = ClassCache.getMethod(clazz, methodName);
        // 对应的字段名称
        FieldMetadata fieldMetadata = methodMetadata.toField();
        Column anno = fieldMetadata.getAnnotation(Column.class);
        if (anno != null && !anno.value().isEmpty()) {
            sb.append(anno.value());
        } else {
            sb.append(Strs.toUnderline(fieldMetadata.getName()));
        }
        return sb.toString();
    }

    /**
     * 从 CLASS 对象中解析出表名称
     */
    public static String parseTableNameFromClass(Class<?> table) {
        Table anno = table.getAnnotation(Table.class);
        if (anno != null) {
            String tableName = anno.value();
            if (!tableName.isEmpty()) {
                return tableName;
            }
        }
        return Strs.toUnderline(table.getSimpleName());
    }

    /**
     * 从实体类型里面解析出 getter
     */
    public static <T> List<MethodMetadata> getter(Class<T> entity) {
        ClassMetadata<?> classMetadata = ClassCache.get(entity);
        return classMetadata.getMethods(m -> {
            String methodName = m.getName();
            if ((methodName.startsWith("get") || methodName.startsWith("is")) && m.parameters().isEmpty()) {
                Column columnAnno = m.toField().getAnnotation(Column.class);
                if (columnAnno != null) {
                    return columnAnno.exist();
                } else {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * 从实体类型里面解析出 setter
     */
    public static <T> List<MethodMetadata> setter(Class<T> entity) {
        ClassMetadata<?> classMetadata = ClassCache.get(entity);
        return classMetadata.getMethods(m -> {
            if (m.getName().startsWith("set") && m.parameters().size() == 1) {
                Column columnAnno = m.toField().getAnnotation(Column.class);
                if (columnAnno != null) {
                    return columnAnno.exist();
                } else {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * 解析自动填充列与其对应填充类的映射
     */
    public static Map<String, Class<? extends ColumnFiller>> parseAutoFillColMapper(Class<?> entityClass) {
        Map<String, Class<? extends ColumnFiller>> mapper = fillerMapper.get(entityClass);
        if (mapper != null) {
            return new HashMap<>(mapper);
        }

        ClassMetadata<?> classMetadata = ClassCache.get(entityClass);
        mapper = new HashMap<>();
        for (FieldMetadata fmd : classMetadata.getFields()) {
            Column colAnno = fmd.getAnnotation(Column.class);
            if (colAnno != null && !colAnno.fill().equals(ColumnFillerAdapter.class)) {
                Class<? extends ColumnFiller> fill = colAnno.fill();
                mapper.put(Strs.toUnderline(fmd.getName()), fill);
            }
        }
        fillerMapper.put(entityClass, mapper);
        return new HashMap<>(mapper);
    }

    /**
     * 根据 ColumnFiller.class 获取对应的对象
     */
    public static ColumnFiller getAutoFiller(Class<? extends ColumnFiller> entityClass) {
        ColumnFiller columnFiller = fillerObjMapper.get(entityClass);
        if (columnFiller != null) {
            return columnFiller;
        }
        try {
            columnFiller = Reflects.newInstance(entityClass);
            fillerObjMapper.put(entityClass, columnFiller);
            return columnFiller;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new Sql4JException(e);
        }
    }
}
