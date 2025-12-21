package xyz.ytora.sql4j.orm.scanner;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import xyz.ytora.sql4j.orm.IRepo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * IRepo 接口扫描
 */
public class RepoScanner {

    /**
     * BaseRepo 接口的路径
     */
    private static final String REPO_PATH = IRepo.class.getName();

    /**
     * 仓储代理类后缀
     */
    private static final String proxySuffix = "$$sql4JRepoProxy";

    private final String pkgToScan;

    public RepoScanner(String pkgToScan) {
        this.pkgToScan = pkgToScan;
    }

    /**
     * 扫描实现或继承IRepo接口的所有实现类或子接口，返回其class
     */
    public List<Class<?>> scanRepoInterfaces() {
        List<Class<?>> repoInterfaces = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(pkgToScan)
                .enableClassInfo()
                .scan()) {
            List<ClassInfo> classInfos = scanResult.getClassesImplementing(REPO_PATH);
            for (ClassInfo classInfo : classInfos) {
                repoInterfaces.add(classInfo.loadClass());
            }
        }
        return repoInterfaces;
    }

    /**
     * 生成指定IRepo类的代理类
     */
    public Class<?> getOrCreateProxyClass(Class<?> repoClazz) {
        // 1. 提取泛型参数 T
        Type actualTypeArgument = extractGenericType(repoClazz);

        // 2. 调用你原本的 ByteBuddy 逻辑生成子类
        return generateSubclass(repoClazz, actualTypeArgument);
    }

    /**
     * 提取 IRepo<T> 中的 T
     */
    private Type extractGenericType(Class<?> repoClazz) {
        Type[] genericInterfaces = repoClazz.getGenericInterfaces();
        return Arrays.stream(genericInterfaces)
                .filter(i -> i instanceof ParameterizedType pt && pt.getRawType().equals(IRepo.class))
                .map(i -> ((ParameterizedType) i).getActualTypeArguments()[0])
                .findFirst()
                .orElse(Object.class); // 默认 Object
    }

    /**
     * 用 ByteBuddy 生成子类, 实现抽象方法
     */
    private Class<?> generateSubclass(Class<?> superType, Type actualTypeArgument) {
        DynamicType.Builder<?> b;
        // 如果是接口
        if (superType.isInterface()) {
            b = new ByteBuddy()
                    .subclass(Object.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
                    .implement(superType)
                    .name(superType.getName() + proxySuffix)
                    .modifiers(Visibility.PUBLIC)
            ;
        }
        // 如果是非接口
        else {
            b = new ByteBuddy()
                    .subclass(superType, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
                    .name(superType.getName() + proxySuffix)
                    .modifiers(Visibility.PUBLIC);
        }

        // 只代理抽象方法
        b = b.method(isAbstract()
                        // 跳过 toString/hashCode 等Object里面定义的方法
                        .and(not(isDeclaredBy(Object.class)))
                        // 规避编译器合成方法（泛型桥接等）
                        .and(not(isSynthetic()))
                        .and(not(isBridge())))
                .intercept(MethodDelegation.to(new RepoProxyInterceptor(actualTypeArgument)));

        try (DynamicType.Unloaded<?> unloaded = b.make()) {
            return unloaded
                    .load(superType.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
        }
    }

}
