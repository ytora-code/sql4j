package xyz.ytora.sql4j.sql.insert;

import xyz.ytora.sql4j.anno.Table;
import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.orm.autofill.ColumnFiller;
import xyz.ytora.sql4j.sql.select.AbsSelect;
import xyz.ytora.sql4j.util.Sql4jUtil;
import xyz.ytora.ytool.str.Strs;

import java.util.*;
import java.util.stream.Collectors;

/**
 * INTO 阶段，指定要插入的字段
 */
public class IntoStage extends AbsInsert {

    /**
     * 即将被新增的字段（原始）
     */
    private final List<SFunction<?, ?>> sourceInsertedColumn = new ArrayList<>();

    /**
     * 即将被新增的字段
     */
    private final List<SFunction<?, ?>> insertedColumn = new ArrayList<>();

    /**
     * 有序的自动填充器对象
     */
    private final List<ColumnFiller> autoFillInsertedColumn = new ArrayList<>();

    /**
     * id列索引下标
     */
    private Integer idIndex = -1;

    /**
     * 自动填充类的索引下标与自动填充类对象的映射
     */
    Map<Integer, ColumnFiller> autoFillColIndexMap = new HashMap<>();

    public <T> IntoStage(InsertBuilder insertBuilder, Collection<SFunction<T, ?>> insertedColumn) {
        setInsertBuilder(insertBuilder);
        getInsertBuilder().setIntoStage(this);
        this.sourceInsertedColumn.addAll(insertedColumn);
        this.insertedColumn.addAll(insertedColumn);
        // 解析自动填充列
        Map<String, Class<? extends ColumnFiller>> mapper = Sql4jUtil.parseAutoFillColMapper(getInsertBuilder().getInsertStage().getTable());
        Set<String> autoFillColNameSet = mapper.keySet().stream().map(Strs::toUnderline).collect(Collectors.toSet());

        // 获取主键列和自动填充列所在的索引
        List<SFunction<?, ?>> bakColList = new ArrayList<>(insertedColumn);
        for (int i = 0; i < bakColList.size(); i++) {
            SFunction<?, ?> col = bakColList.get(i);
            String colName = Sql4jUtil.parseColumn(col, null);
            // TODO 暂时根据字段名称来判断主键
            if (colName.equalsIgnoreCase("id")) {
                idIndex = i;
                continue;
            }

            // 判断该列有没有在mapper出现
            // 如果有，记录下列的索引
            if (autoFillColNameSet.contains(colName)) {
                autoFillColIndexMap.put(i, Sql4jUtil.getAutoFiller(mapper.get(colName)));
                mapper.remove(colName);
            }
        }

        // 如果目标插入表的主键有填充策略，并且主键在插入字段中不存在，则手动在插入字段列表中添加一个主键列
        Class<?> table = getInsertBuilder().getInsertStage().getTable();
        Table tableAnno = table.getAnnotation(Table.class);
        if (idIndex < 0 && tableAnno != null && tableAnno.idType() != null) {
            this.insertedColumn.add(0, Raw.of("id"));
        }

        // mapper里面剩下的数据都是此次 insert 没有涉及的字段，但依然要自动填充
        for (String colName : mapper.keySet()) {
            this.insertedColumn.add(Raw.of(colName));
            autoFillInsertedColumn.add(Sql4jUtil.getAutoFiller(mapper.get(colName)));
        }
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage value(Object... insertedData) {
        List<Object> values;
        if (insertedData == null || insertedData.length == 0) {
            values = new ArrayList<>();
        } else {
            values = new ArrayList<>(insertedData.length);
            values.addAll(Arrays.asList(insertedData));
        }
        return new ValuesStage(getInsertBuilder(), values, idIndex, insertedColumn.size());
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage value(List<Object> insertedData) {
        return new ValuesStage(getInsertBuilder(), insertedData, idIndex, insertedColumn.size());
    }

    /**
     * INTO 后面是 VALUE
     */
    public ValuesStage values(List<List<Object>> insertedDataList) {
        return new ValuesStage(getInsertBuilder(), new ArrayList<>(), idIndex, insertedColumn.size()).values(insertedDataList);
    }

    /**
     * 将 SELECT 的查询结果集作为插入的数据，需要忽略主键策略和自动填充策略
     */
    public SelectValueStage values(AbsSelect subSelect) {
        // 回滚原始插入字段
        insertedColumn.clear();
        insertedColumn.addAll(sourceInsertedColumn);
        subSelect.getSelectBuilder().isSub();
        return new SelectValueStage(getInsertBuilder(), subSelect);
    }

    public List<SFunction<?, ?>> getInsertedColumn() {
        return insertedColumn;
    }

    public List<ColumnFiller> getAutoFillInsertedColumn() {
        return autoFillInsertedColumn;
    }

    public Integer getIdIndex() {
        return idIndex;
    }

    public Map<Integer, ColumnFiller> getAutoFillColIndexMap() {
        return autoFillColIndexMap;
    }
}
