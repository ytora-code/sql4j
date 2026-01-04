package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.Sql4JException;
import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.enums.IdType;
import xyz.ytora.sql4j.orm.autofill.ColumnFiller;
import xyz.ytora.sql4j.sql.AbsSql;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.ytool.id.IdGenerator;
import xyz.ytora.ytool.id.Ids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * VALUE 阶段，指定要插入的数据
 */
public class ValuesStage extends AbsInsert implements InsertEndStage {

    /**
     * 可能插入多条数据
     */
    private final List<List<Object>> insertedDataList = new ArrayList<>();

    /**
     * ID字段索引
     */
    private final Integer idIndex;

    /**
     * 字段数量
     */
    private final Integer count;

    public ValuesStage(InsertBuilder insertBuilder, List<Object> insertedData, Integer idIndex, Integer count) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setValuesStage(this);
        if (insertedData != null && !insertedData.isEmpty()) {
            for (Object datum : insertedData) {
                check(datum);
            }
            addRow(insertedData);
        }
        this.idIndex = idIndex;
        this.count = count;
    }

    /**
     * VALUE 后面可能继续 VALUE
     */
    public ValuesStage value(Object... insertedData) {
        return value(Arrays.asList(insertedData));
    }

    /**
     * VALUE 后面可能继续 VALUE
     */
    public ValuesStage value(List<Object> insertedData) {
        // TODO 取消字段个数匹配检查，一般不会有问题
//        int autoFillSize = getInsertBuilder().getIntoStage().getAutoFillInsertedColumn().size();
//        if (insertedData.size() + autoFillSize != count) {
//            throw new Sql4JException("插入数据的长度【" + insertedData.size() + "】与指定的字段长度【" + count + "】不匹配");
//        }
        for (Object datum : insertedData) {
            check(datum);
        }
        addRow(insertedData);
        return this;
    }

    /**
     * VALUE 后面可能继续 VALUE
     */
    public ValuesStage values(List<List<Object>> insertedDataList) {
        for (List<Object> insertedData : insertedDataList) {
            for (Object datum : insertedData) {
                check(datum);
            }
            if (insertedData instanceof ArrayList<Object>) {
                this.value(insertedData);
            } else {
                List<Object> newInsertedData = new ArrayList<>(insertedData.size());
                for (int i = 0; i < insertedData.size(); i++) {
                    newInsertedData.add(i, insertedData.get(i));
                }
                this.value(newInsertedData);
            }
        }
        return this;
    }

    public List<List<Object>> getInsertedDataList() {
        // 如果新增的id字段值为空，则根据默认id策略，赋予值
        Class<?> table = getInsertBuilder().getInsertStage().getTable();
        Table tableAnno = table.getAnnotation(Table.class);
        IdGenerator<?> idGenerator = null;
        if (tableAnno != null && tableAnno.idType() != null) {
            if (tableAnno.idType().equals(IdType.UUID)) {
                idGenerator = Ids.getUuid();
            } else if (tableAnno.idType().equals(IdType.UCID)) {
                idGenerator = Ids.getUlid();
            } else if (tableAnno.idType().equals(IdType.SNOWFLAKE)) {
                idGenerator = Ids.getSnowflakeId();
            }
        }
        if (idGenerator != null) {
            // 要插入的字段中没有id，则添加一个id
            if (idIndex < 0) {
                for (List<Object> list : insertedDataList) {
                    list.add(0, idGenerator.nextId());
                }
            }
            // 如果有id字段，并且id对应的插入字段为空，生成一个id
            else {
                for (List<Object> list : insertedDataList) {
                    if (idIndex < list.size()) {
                        Object id = list.get(idIndex);
                        if (id == null) {
                            list.set(idIndex, idGenerator.nextId());
                        }
                    }
                }
            }
        }

        return insertedDataList;
    }

    @Override
    public SqlInfo end() {
        return getInsertBuilder().getTranslator().translate(getInsertBuilder());
    }

    @Override
    public List<Object> submit() {
        return getInsertBuilder().getSQLHelper().getSqlExecutionEngine().executeInsert(getInsertBuilder().getTranslator().translate(getInsertBuilder())).getIds();
    }

    /**
     * 增加一行插入数据
     */
    private void addRow(List<Object> row) {
        // row 里面已有的自动填充字段
        Map<Integer, ColumnFiller> autoFillColIndexMap = getInsertBuilder().getIntoStage().getAutoFillColIndexMap();
        for (Integer index : autoFillColIndexMap.keySet()) {
            Object value = row.get(index);
            if (value == null) {
                ColumnFiller columnFiller = autoFillColIndexMap.get(index);
                row.set(index, columnFiller.fillOnInsert());
            }
        }

        // 额外的自动填充字段
        List<ColumnFiller> columnFillers = getInsertBuilder().getIntoStage().getAutoFillInsertedColumn();
        for (ColumnFiller columnFiller : columnFillers) {
            row.add(columnFiller.fillOnInsert());
        }
        insertedDataList.add(row);
    }

    /**
     * 由于 ValuesStage 和 SelectValueStage 是两个独立的阶段类，所以 ValuesStage 不允许子查询
     */
    private void check(Object v) {
        if (v instanceof AbsSql) {
            throw new UnsupportedOperationException("非法的 INSERT 字段，该 INSERT 内部必须全部使用方法引用，当前字段类型【" + v.getClass() + "】");
        }
    }
}
