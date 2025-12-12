package xyz.ytora.sql4j.orm.scanner;

import net.bytebuddy.implementation.bind.annotation.*;
import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Segment;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.util.Sql4jUtil;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.str.Strs;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 抽象方法拦截器
 */
public class RepoProxyInterceptor {

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
        ClassMetadata<?> classMetadata = ClassCache.get(targetClass);
        Segment segAnno = targetMethod.getAnnotation(Segment.class);
        if (segAnno != null) {
            SQLHelper.getInstance().getLogger().info("暂未实现 segment");
        }
        // 根据方法名称产生 SQL
        else {
            List<MethodMetadata> getters = Sql4jUtil.getter(targetClass);
            List<FieldMetadata> fieldMetadata = getters.stream().map(MethodMetadata::toField).toList();
            String name = targetMethod.getName();
            if (name.startsWith("select")) {
                throw new Sql4JException("暂时只支持以 select 开头的方法: " + name);
            }
            // 可能是 "", All, AllById, ById, UserNameByIdUserName
            String afterSelect = name.substring("select".length());
            int byIndex = afterSelect.indexOf("By");
            String selectPart;
            String wherePart;

            if (byIndex < 0) {
                // 没有 By，比如 selectAll 或 selectUserName
                selectPart = afterSelect;
                wherePart = "";
            } else {
                selectPart = afterSelect.substring(0, byIndex);    // "", "All", "UserNameAge"
                wherePart = afterSelect.substring(byIndex + 2);    // "", "IdUserName"
            }

            // 1) 解析 select 部分
            boolean selectAll = false;
            List<String> selectProps;

            if (selectPart.isEmpty() || "All".equalsIgnoreCase(selectPart)) {
                // select / selectByXxx / selectAll / selectAllByXxx
                selectAll = true;
                selectProps = Collections.emptyList(); // 用标记位区分
            } else {
                // 字符串首字母大写
                name = Strs.firstCapitalize(name);
                String names = Strs.splitCamelCase(name, "-");
                System.out.println(names);
            }
        }
        return null;
    }

}
