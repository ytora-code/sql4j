package xyz.ytora.sql4j.util;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 工具类
 */
public class Sql4jUtil {

    private final static String methodName = "writeReplace";

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
                return columnAnno != null && columnAnno.exist();
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
                return columnAnno != null && columnAnno.exist();
            }
            return false;
        });
    }
}
