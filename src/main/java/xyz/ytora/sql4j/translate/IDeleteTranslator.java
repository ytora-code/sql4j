package xyz.ytora.sql4j.translate;

import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.delete.DeleteBuilder;

/**
 * DELETE 翻译器
 */
public interface IDeleteTranslator {

    SqlInfo translate(DeleteBuilder builder);
}
