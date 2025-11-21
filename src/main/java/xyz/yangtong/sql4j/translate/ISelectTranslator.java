package xyz.yangtong.sql4j.translate;

import xyz.yangtong.sql4j.sql.SqlInfo;
import xyz.yangtong.sql4j.sql.select.SelectBuilder;

/**
 * SELECT 翻译器
 */
public interface ISelectTranslator {

    SqlInfo translate(SelectBuilder builder);
}
