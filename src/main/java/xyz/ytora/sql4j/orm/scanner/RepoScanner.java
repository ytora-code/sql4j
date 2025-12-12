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
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.orm.BaseRepo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.isBridge;
import static net.bytebuddy.matcher.ElementMatchers.isSynthetic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * Repo 接口扫描
 */
public class RepoScanner {

    /**
     * BaseRepo 接口的路径
     */
    private static final String REPO_PATH = BaseRepo.class.getName();

    /**
     * 仓储代理类后缀
     */
    private static final String proxySuffix = "$$sql4JRepoProxy";

    /**
     * 只能扫描一次
     */
    private volatile boolean scanFlag;

    private final SQLHelper sqlHelper;
    private final String pkgToScan;

    public RepoScanner(SQLHelper sqlHelper, String pkgToScan) {
        scanFlag = false;
        this.sqlHelper = sqlHelper;
        this.pkgToScan = pkgToScan;
    }

    /**
     * 开始扫描指定路径下面所有实体类，如果实体类对应的表不存在，则创建
     */
    public List<Object> createProxyRepo() {
        List<Object> proxyRepos = new ArrayList<>();
        // 一次运行期间只能扫描一次
        if (scanFlag) {
            return proxyRepos;
        }
        long start = System.currentTimeMillis();
        long classCount = 0;

        scanFlag = true;

        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(pkgToScan) // 设置扫描包路径
                .enableClassInfo() // 启用类信息
                .scan()) {
            // 获取实现了 BaseRepo<T> 的所有类或接口
            List<ClassInfo> classInfos = scanResult.getClassesImplementing(BaseRepo.class.getName());
            for (ClassInfo classInfo : classInfos) {
                // 加载Repo
                Class<?> repoClazz = classInfo.loadClass();

                // 获取所有实现的接口
                Type[] genericInterfaces = repoClazz.getGenericInterfaces();
                Optional<Type> typeOp = Arrays.stream(genericInterfaces).filter(i -> {
                    if (i.equals(BaseRepo.class)) {
                        return true;
                    } else if (i instanceof ParameterizedType parameterizedType) {
                        return parameterizedType.getRawType().equals(BaseRepo.class);
                    }
                    return false;
                }).findAny();
                if (typeOp.isPresent()) {
                    Type genericInterface = typeOp.get();
                    if (genericInterface.equals(BaseRepo.class)) {
                        sqlHelper.getLogger().warn("类型：{} 未定义 BaseRepo 接口的泛型，跳过", repoClazz.getName());
                    } else if (genericInterface instanceof ParameterizedType parameterizedType) {
                        // 获取BaseRepo的泛型类型参数
                        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                        sqlHelper.getLogger().info("Generic type: " + actualTypeArgument.getTypeName());

                        // 生成代理类
                        Class<?> implType = generateSubclass(repoClazz);
                    }
                }
            }
        }
        return proxyRepos;
    }

    /**
     * 用 ByteBuddy 生成子类, 实现抽象方法
     */
    private Class<?> generateSubclass(Class<?> superType) {
        DynamicType.Builder<?> b;
        // 如果是接口
        if (superType.isInterface()) {
            b = new ByteBuddy()
                    .subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                    .implement(superType)
                    .name(superType.getName() + proxySuffix)
                    .modifiers(Visibility.PUBLIC);
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
                .intercept(MethodDelegation.to(new RepoProxyInterceptor()));

        try (DynamicType.Unloaded<?> unloaded = b.make()) {
            return unloaded
                    .load(superType.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
        }
    }

}
