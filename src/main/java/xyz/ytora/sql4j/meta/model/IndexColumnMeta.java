package xyz.ytora.sql4j.meta.model;

/**
 * 索引列元数据
 */
public class IndexColumnMeta {

    /**
     * 索引字段
     */
    private String column;
    /**
     * 索引position
     */
    private short position;
    /**
     * 升序还是降序 ASC / DESC
     */
    private String order;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public short getPosition() {
        return position;
    }

    public void setPosition(short position) {
        this.position = position;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
