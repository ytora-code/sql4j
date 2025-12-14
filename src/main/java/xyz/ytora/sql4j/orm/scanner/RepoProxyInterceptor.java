package xyz.ytora.sql4j.orm.scanner;

import net.bytebuddy.implementation.bind.annotation.*;
import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Segment;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.sql.select.SelectWhereStage;
import xyz.ytora.ytool.str.Strs;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 抽象方法拦截器
 */
public class RepoProxyInterceptor {
    private Class<?> sourceClass;

    public RepoProxyInterceptor(Type sourceClass) {
        this.sourceClass = (Class<?>) sourceClass;
    }

    /**
     * 抽象方法统一走的代理
     */
    @RuntimeType
    public Object intercept(@Origin Method targetMethod,  // 目标方法
                            @Origin Class<?> targetClass, // 目标class
                            @AllArguments Object[] args,    // 方法参数值
                            @This Object proxy,     // 代理对象
                            @SuperMethod(nullIfImpossible = true) Method superMethod) {
        // 获取目标类的类元缓存
        Segment segAnno = targetMethod.getAnnotation(Segment.class);
        SQLHelper sqlHelper = SQLHelper.getInstance();
        if (segAnno != null) {
            sqlHelper.getLogger().info("暂未实现 segment");
        }
        // 根据方法名称产生 SQL
        else {
            String name = targetMethod.getName();
            // 如果方法名称是selectByUserNameAndAge(String userName, int age)，就翻译为select * from user_name = ? and age = ?
            if (name.startsWith("select")) {
                name = name.substring("select".length());
                if (!name.startsWith("By")) {
                    return null;
                }
                name = name.substring("By".length());
                String[] wheres = name.split("And");
                if (wheres.length != args.length) {
                    throw new Sql4JException("抽象方法的方法名称和方法参数个数不一致");
                }

                SelectWhereStage sql = sqlHelper.select().from(sourceClass)
                        .where(w -> {
                            for (int i = 0; i < wheres.length; i++) {
                                int j = i;
                                String where = wheres[j];
                                String columnName = Strs.toUnderline(where);
                                w.eq(Raw.of(columnName), args[j]);
                            }
                        });

                Class<?> returnType = targetMethod.getReturnType();
                if (returnType.equals(void.class)) {
                    return null;
                }
                // 如果是集合类型
                else if (Collection.class.isAssignableFrom(returnType)) {
                    // 获取集合的内部类型
                    Type genericReturnType = targetMethod.getGenericReturnType();
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        if (typeArguments.length > 0) {
                            Class<?> typeArgument = (Class<?>) typeArguments[0];
                            return sql.submit(typeArgument);
                        }
                    }
                }
                // 如果是普通bean类型
                else {
                    return sqlHelper.getSqlExecutionEngine().executeSelect(sql.getSelectBuilder().getTranslator().translate(sql.getSelectBuilder())).toBean(returnType);
                }


            } else {
                throw new Sql4JException("暂时只支持以 select 开头的方法: " + name);
            }
        }
        return null;
    }
}
