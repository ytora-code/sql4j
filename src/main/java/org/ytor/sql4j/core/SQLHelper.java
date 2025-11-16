package org.ytor.sql4j.core;

import org.ytor.sql4j.core.support.SqlExecutionEngine;
import org.ytor.sql4j.sql.SFunction;
import org.ytor.sql4j.sql.delete.DeleteBuilder;
import org.ytor.sql4j.sql.delete.DeleteStage;
import org.ytor.sql4j.sql.insert.InsertBuilder;
import org.ytor.sql4j.sql.insert.InsertStage;
import org.ytor.sql4j.sql.select.SelectBuilder;
import org.ytor.sql4j.sql.select.SelectStage;
import org.ytor.sql4j.sql.update.UpdateBuilder;
import org.ytor.sql4j.sql.update.UpdateStage;
import org.ytor.sql4j.translate.ITranslator;
import org.ytor.sql4j.translate.support.base.BaseTranslator;

import java.util.Arrays;

public class SQLHelper {

    /**
     * SQL 翻译器
     */
    private ITranslator translator = new BaseTranslator();

    /**
     * 数据库连接提供者
     */
    private IConnectionProvider connectionProvider;

    /**
     * SQL 执行引擎
     */
    private ISqlExecutionEngine sqlExecutionEngine;

    public void registerTranslator(ITranslator translator) {
        this.translator = translator;
    }

    public ITranslator getTranslator() {
        return translator;
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

    public void initSqlExecutionEngine() {
        if (connectionProvider == null) {
            throw new NullPointerException("请提供 ConnectionProvider 组件");
        }
        sqlExecutionEngine = new SqlExecutionEngine(connectionProvider);
    }

    public ISqlExecutionEngine getSqlExecutionEngine() {
        return sqlExecutionEngine;
    }

    @SafeVarargs
    public final <T> SelectStage select(SFunction<T, ?>... Columns) {
        SelectBuilder selectBuilder = new SelectBuilder(this);
        return new SelectStage(selectBuilder, Arrays.asList(Columns));
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
}
