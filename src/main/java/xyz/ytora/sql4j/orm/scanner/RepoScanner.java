package xyz.ytora.sql4j.orm.scanner;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.orm.BaseRepo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Repo 接口扫描
 */
public class RepoScanner {

    private static final String REPO_PATH = BaseRepo.class.getName();

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
                    }
                }
            }
        }
        return proxyRepos;
    }

}
