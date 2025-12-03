package xyz.ytora.sql4j.sql;

import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.translate.ITranslator;

/**
 * SQL 构造器
 */
public abstract class SqlBuilder extends AliasRegister {

    /**
     * SQLHelper
     */
    protected SQLHelper sqlHelper;

    public SQLHelper getSQLHelper() {
        return sqlHelper;
    }

    /**
     * 获取 SQL 翻译器
     */
    abstract protected ITranslator getTranslator();

    /**
     * 是否属于子查询
     */
    abstract public Boolean getIsSub();

    /**
     * 设置子查询
     */
    abstract public void isSub();
}
