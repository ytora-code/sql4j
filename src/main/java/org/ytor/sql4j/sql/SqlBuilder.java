package org.ytor.sql4j.sql;

import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.translate.ITranslator;

/**
 * SQL 构造器
 */
public abstract class SqlBuilder extends AliasRegister {

    /**
     * SQLHelper
     */
    protected SQLHelper sqlHelper;

    abstract protected ITranslator getTranslator();
}
