package xyz.yangtong.sql4j.translate;

import xyz.yangtong.sql4j.sql.SqlInfo;
import xyz.yangtong.sql4j.sql.insert.InsertBuilder;

/**
 * INSERT 翻译器
 */
public interface IInsertTranslator {

    SqlInfo translate(InsertBuilder builder);
}
