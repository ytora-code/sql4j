package org.ytor.sql4j.core;

import org.ytor.sql4j.Sql4JException;
import org.ytor.sql4j.caster.Caster;
import org.ytor.sql4j.caster.TypeCaster;
import org.ytor.sql4j.caster.TypePair;
import org.ytor.sql4j.core.support.SqlExecutionEngine;
import org.ytor.sql4j.enums.SqlType;
import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.interceptor.SqlInterceptor;
import org.ytor.sql4j.interceptor.support.PreventFullTableUpdateInterceptor;
import org.ytor.sql4j.log.ISqlLogger;
import org.ytor.sql4j.log.support.DefaultSqlLogger;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.sql.delete.DeleteBuilder;
import org.ytor.sql4j.sql.delete.DeleteStage;
import org.ytor.sql4j.sql.insert.InsertBuilder;
import org.ytor.sql4j.sql.insert.InsertStage;
import org.ytor.sql4j.sql.select.DistinctStage;
import org.ytor.sql4j.sql.select.SelectBuilder;
import org.ytor.sql4j.sql.select.SelectStage;
import org.ytor.sql4j.sql.update.UpdateBuilder;
import org.ytor.sql4j.sql.update.UpdateStage;
import org.ytor.sql4j.translate.ITranslator;
import org.ytor.sql4j.translate.support.base.BaseTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLHelper {

    /**
     * SQL 翻译器
     */
    private ITranslator translator = new BaseTranslator();

    /**
     * 类型转换器
     */
    private TypeCaster typeCaster = new TypeCaster();

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

    public SQLHelper() {
        sqlInterceptors.add(new PreventFullTableUpdateInterceptor());
    }

    public void registerTranslator(ITranslator translator) {
        this.translator = translator;
    }

    public ITranslator getTranslator() {
        return translator;
    }

    public void registerTypeCaster(TypeCaster typeCaster) {
        this.typeCaster = typeCaster;
    }

    public TypeCaster getTypeCaster() {
        return typeCaster;
    }

    /**
     * 注册类型转换器
     */
    public void registerCaster(TypePair pair, Caster<?, ?> caster) {
        typeCaster.register(pair, caster);
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

    public DistinctStage distinct() {
        SelectBuilder selectBuilder = new SelectBuilder(this);
        return new DistinctStage(selectBuilder);
    }

    @SafeVarargs
    public final <T> SelectStage select(SFunction<T, ?>... columns) {
        SelectBuilder selectBuilder = new SelectBuilder(this);
        return new SelectStage(selectBuilder, Arrays.asList(columns));
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

}
