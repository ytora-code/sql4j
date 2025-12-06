package xyz.ytora.sql4j.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Column;
import xyz.ytora.sql4j.caster.Caster;
import xyz.ytora.sql4j.caster.DefaultTypeCaster;
import xyz.ytora.sql4j.caster.ITypeCaster;
import xyz.ytora.sql4j.core.support.SqlExecutionEngine;
import xyz.ytora.sql4j.enums.SqlType;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.interceptor.SqlInterceptor;
import xyz.ytora.sql4j.interceptor.support.PreventFullTableUpdateInterceptor;
import xyz.ytora.sql4j.log.ISqlLogger;
import xyz.ytora.sql4j.log.support.DefaultSqlLogger;
import xyz.ytora.sql4j.orm.TableCreatorManager;
import xyz.ytora.sql4j.sql.ConditionExpressionBuilder;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.delete.DeleteBuilder;
import xyz.ytora.sql4j.sql.delete.DeleteStage;
import xyz.ytora.sql4j.sql.insert.InsertBuilder;
import xyz.ytora.sql4j.sql.insert.InsertStage;
import xyz.ytora.sql4j.sql.select.DistinctStage;
import xyz.ytora.sql4j.sql.select.SelectBuilder;
import xyz.ytora.sql4j.sql.select.SelectStage;
import xyz.ytora.sql4j.sql.update.UpdateBuilder;
import xyz.ytora.sql4j.sql.update.UpdateStage;
import xyz.ytora.sql4j.translate.ITranslator;
import xyz.ytora.sql4j.translate.support.base.BaseTranslator;
import xyz.ytora.ytool.classcache.ClassCache;
import xyz.ytora.ytool.classcache.classmeta.ClassMetadata;
import xyz.ytora.ytool.classcache.classmeta.FieldMetadata;
import xyz.ytora.ytool.classcache.classmeta.MethodMetadata;
import xyz.ytora.ytool.convert.TypePair;
import xyz.ytora.ytool.str.Strs;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public class SQLHelper {

    /**
     * 实体类只能扫描一次
     */
    private static boolean scanFlag = false;

    /**
     * 当前正在使用的全局唯一的 SQLHelper 对象
     */
    private static SQLHelper instance;

    /**
     * SQL 翻译器
     */
    private ITranslator translator = new BaseTranslator();

    /**
     * 类型转换器
     */
    private ITypeCaster typeCaster = new DefaultTypeCaster();

    /**
     * 数据库连接提供者
     */
    private IConnectionProvider connectionProvider;

    /**
     * SQL 执行引擎
     */
    private ISqlExecutionEngine sqlExecutionEngine;

    /**
     * SQL 日志记录器
     */
    private ISqlLogger logger = new DefaultSqlLogger();

    /**
     * 拦截器
     */
    private final List<SqlInterceptor> sqlInterceptors = new ArrayList<>();

    /**
     * TableCreator 管理器
     */
    private TableCreatorManager tableCreatorManager = new TableCreatorManager();

    public SQLHelper() {
        sqlInterceptors.add(new PreventFullTableUpdateInterceptor());
        SQLHelper.instance = this;
    }

    public static SQLHelper getInstance() {
        if (instance == null) {
            throw new Sql4JException("SQLHelper 对象还未创建，请先 new 一个 SQLHelper 对象");
        }
        return instance;
    }

    public void registerTranslator(ITranslator translator) {
        this.translator = translator;
    }

    public ITranslator getTranslator() {
        return translator;
    }

    public void registerTypeCaster(ITypeCaster typeCaster) {
        this.typeCaster = typeCaster;
    }

    public ITypeCaster getTypeCaster() {
        return typeCaster;
    }

    /**
     * 注册类型转换器
     */
    public void addCaster(TypePair pair, Caster<?, ?> caster) {
        if (pair != null && caster != null) {
            typeCaster.register(pair, caster);
        }
    }

    /**
     * 注册数据库连接提供组件
     */
    public void registerConnectionProvider(IConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        initSqlExecutionEngine();
    }

    /**
     * 获取数据库连接提供组件
     */
    public IConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    /**
     * 初始化 SQL 执行引擎
     */
    public void initSqlExecutionEngine() {
        if (connectionProvider == null) {
            throw new NullPointerException("请提供 ConnectionProvider 组件");
        }
        sqlExecutionEngine = new SqlExecutionEngine(this);
    }

    public ISqlExecutionEngine getSqlExecutionEngine() {
        return sqlExecutionEngine;
    }

    /**
     * 注册 SQL 日志记录器
     */
    public void registerLogger(ISqlLogger logger) {
        this.logger = logger;
    }

    public ISqlLogger getLogger() {
        return logger;
    }

    /**
     * 添加拦截器
     */
    public void addSqlInterceptor(SqlInterceptor interceptor) {
        if (interceptor != null) {
            this.sqlInterceptors.add(interceptor);
        }
    }

    public List<SqlInterceptor> getSqlInterceptors() {
        return sqlInterceptors;
    }

    public void registerTableCreatorManager(TableCreatorManager tableCreatorManager) {
        this.tableCreatorManager = tableCreatorManager;
    }

    public TableCreatorManager getTableCreatorManager() {
        return tableCreatorManager;
    }

    /**
     * 扫描所有 AbsEntity 子类，并判断是否应该创建数据库表
     */
    public void createTableIfNotExist(String pkgToScan) {
        // 一次运行期间只能扫描一次
        if (!scanFlag) {
            SQLHelper sqlHelper = SQLHelper.getInstance();
            long start = System.currentTimeMillis();
            long classCount = 0;

            scanFlag = true;

            // AbsEntity 实体类和 Table 注解的全类名
            String entityPath = "xyz.ytora.sql4j.orm.AbsEntity";
            String tableAnnoPath = "xyz.ytora.sql4j.anno.Table";

            // 开始扫描
            try (ScanResult scanResult = new ClassGraph()
                    .enableClassInfo()      // 启用类信息扫描（继承关系）
                    .enableAnnotationInfo() // 启用注解信息扫描
                    .acceptPackages(pkgToScan) // 指定扫描路径
                    .scan()) {
                // 1. 获取所有继承了 AbsEntity 的实体类
                ClassInfoList candidates = scanResult.getSubclasses(entityPath);

                for (ClassInfo classInfo : candidates) {
                    // 2. 判断实体类是否有 @Table 注解
                    if (classInfo.hasAnnotation(tableAnnoPath)) {
                        // 触发类加载
                        Class<?> clazz = classInfo.loadClass();

                        sqlHelper.getLogger().info("扫描到实体类：" + clazz.getName());
                        tableCreatorManager.createTableIfNotExist(sqlHelper, clazz);
                        classCount++;
                    }
                }
            }
            sqlHelper.getLogger().info("扫描完毕，共扫描到实体类个数：" + classCount + " ，耗时" + (System.currentTimeMillis() - start));
        }
    }

    /*================================== SQL链式调用起点 =======================================*/

    public DistinctStage distinct() {
        SelectBuilder selectBuilder = new SelectBuilder(this);
        return new DistinctStage(selectBuilder);
    }

    @SafeVarargs
    public final <T> SelectStage select(SFunction<T, ?>... columns) {
        SelectBuilder selectBuilder = new SelectBuilder(this);
        return new SelectStage(selectBuilder, Arrays.asList(columns));
    }

    public SelectStage select(Class<?> tableColumns) {
        SelectBuilder selectBuilder = new SelectBuilder(this);
        return new SelectStage(selectBuilder, tableColumns);
    }

    public InsertStage insert(Class<?> table) {
        InsertBuilder insertBuilder = new InsertBuilder(this);
        return new InsertStage(insertBuilder, table);
    }

    public UpdateStage update(Class<?> table) {
        UpdateBuilder updateBuilder = new UpdateBuilder(this);
        return new UpdateStage(updateBuilder, table);
    }

    public DeleteStage delete() {
        DeleteBuilder deleteBuilder = new DeleteBuilder(this);
        return new DeleteStage(deleteBuilder);
    }

    /**
     * 直接执行 SQL
     */
    public ExecResult execDirectly(String sql, Object... parms) {
        if (sql.startsWith("SELECT") || sql.startsWith("select")) {
            return sqlExecutionEngine.executeQuery(new SqlInfo(null, SqlType.SELECT, sql, Arrays.asList(parms)));
        } else if (sql.startsWith("INSERT") || sql.startsWith("insert")) {
            return sqlExecutionEngine.executeQuery(new SqlInfo(null, SqlType.INSERT, sql, Arrays.asList(parms)));
        } else if (sql.startsWith("UPDATE") || sql.startsWith("update")) {
            return sqlExecutionEngine.executeQuery(new SqlInfo(null, SqlType.UPDATE, sql, Arrays.asList(parms)));
        } else if (sql.startsWith("DELETE") || sql.startsWith("delete")) {
            return sqlExecutionEngine.executeQuery(new SqlInfo(null, SqlType.DELETE, sql, Arrays.asList(parms)));
        }
        throw new Sql4JException("未知的 SQL 类型，确保 SQL 字符串以 SELECT、INSERT、UPDATE或DELETE开头");
    }

    /**
     * 将传入的实体对象转为 WHERE 条件
     */
    public <T> Consumer<ConditionExpressionBuilder> toWhere(T entity) {
        ClassMetadata<?> classMetadata = ClassCache.get(entity.getClass());
        // 依次调用 getter 方法
        List<MethodMetadata> getters = classMetadata.getMethods(methodMetadata -> methodMetadata.getName().startsWith("get"));
        Map<String, Object> params = new LinkedHashMap<>();
        for (MethodMetadata getter : getters) {
            try {
                Object val = getter.invoke(entity);
                if (val != null) {
                    String columnName = null;
                    FieldMetadata fieldMetadata = getter.toField();
                    Column columnAnno = fieldMetadata.getAnnotation(Column.class);
                    if (columnAnno != null && Strs.isNotEmpty(columnAnno.value())) {
                        columnName = columnAnno.value();
                    } else {
                        columnName = Strs.toUnderline(fieldMetadata.getName());
                    }
                    params.put(columnName, val);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }

        return w -> {
            for (String key : params.keySet()) {
                w.eq(Raw.of(key), params.get(key));
            }
        };
    }

}
