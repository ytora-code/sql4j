package xyz.yangtong.sql4j.translate;

import xyz.yangtong.sql4j.sql.SqlInfo;
import xyz.yangtong.sql4j.sql.delete.DeleteBuilder;

/**
 * DELETE 翻译器
 */
public interface IDeleteTranslator {

    SqlInfo translate(DeleteBuilder builder);
}
