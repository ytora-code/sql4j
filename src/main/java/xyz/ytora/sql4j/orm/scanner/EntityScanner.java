package xyz.ytora.sql4j.orm.scanner;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.orm.Entity;

/**
 * 类扫描
 */
public class EntityScanner {

    private static final String ENTITY_PATH = Entity.class.getName();
    private static final String TABLE_ANNO_PATH = Table.class.getName();

    /**
     * 只能扫描一次
     */
    private volatile boolean scanFlag;

    private final SQLHelper sqlHelper;
    private final String pkgToScan;

    public EntityScanner(SQLHelper sqlHelper, String pkgToScan) {
        this.sqlHelper = sqlHelper;
        this.pkgToScan = pkgToScan;
    }

    /**
     * 开始扫描指定路径下面所有实体类，如果实体类对应的表不存在，则创建
     */
    public void createTableIfNotExist() {
        // 一次运行期间只能扫描一次
        if (scanFlag) {
            return;
        }

        long start = System.currentTimeMillis();
        long classCount = 0;
        long handledCount = 0;

        scanFlag = true;

        // 开始扫描
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()      // 启用类信息扫描（继承关系）
                .enableAnnotationInfo() // 启用注解信息扫描
                .acceptPackages(pkgToScan) // 指定扫描路径
                .scan()) {
            // 1. 获取所有继承了 AbsEntity 的实体类
            ClassInfoList candidates = scanResult.getSubclasses(ENTITY_PATH);

            for (ClassInfo classInfo : candidates) {
                // 2. 判断实体类是否有 @Table 注解
                if (classInfo.hasAnnotation(TABLE_ANNO_PATH)) {
                    // 触发类加载
                    Class<?> clazz = classInfo.loadClass();

                    sqlHelper.getLogger().info("扫描到实体类：" + clazz.getName());
                    if (sqlHelper.getTableCreatorManager().createTableIfNotExist(sqlHelper, clazz)) {
                        handledCount++;
                    }
                    classCount++;
                }
            }
        }
        sqlHelper.getLogger().info("扫描完毕，共扫描到实体类个数：" + classCount + "，共为" + handledCount + "个实体类建表，耗时" + (System.currentTimeMillis() - start));
    }

}
