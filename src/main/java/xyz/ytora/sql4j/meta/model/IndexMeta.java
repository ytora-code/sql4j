package xyz.ytora.sql4j.meta.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 索引元数据
 */
public class IndexMeta {

    /**
     * 索引名称
     */
    private String name;
    /**
     * 是否唯一
     */
    private boolean unique;

    /**
     * 索引列
     */
    private List<IndexColumnMeta> columns = new ArrayList<>();

    public void addColumn(short position, String column, String order) {
        IndexColumnMeta c = new IndexColumnMeta();
        c.setPosition(position);
        c.setColumn(column);
        c.setOrder(order);
        columns.add(c);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public List<IndexColumnMeta> getColumns() {
        return columns;
    }

    public void setColumns(List<IndexColumnMeta> columns) {
        this.columns = columns;
    }
}
