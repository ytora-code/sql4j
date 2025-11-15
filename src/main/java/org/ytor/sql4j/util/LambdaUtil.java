package org.ytor.sql4j.util;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.sql.AliasRegister;
import org.ytor.sql4j.sql.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Lambda 工具类
 */
public class LambdaUtil {

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
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        }
        return StrUtil.toLowerUnderline(methodName);
    }

    /**
     * 解析出拼接到SQL里面的字段，比如user.user_name
     */
    public static <T> String parseColumn(SFunction<T, ?> fn, AliasRegister register) {
        SerializedLambda sl = serializedLambda(fn);
        StringBuilder sb = new StringBuilder();
        // 如果不是单表，则要查表别名
        if (register != null && !register.single()) {
            String alias = register.getAlias(parseClassPath(sl));
            sb.append(alias).append('.');
        }
        sb.append(parseMethodName(sl));
        return sb.toString();
    }

}
