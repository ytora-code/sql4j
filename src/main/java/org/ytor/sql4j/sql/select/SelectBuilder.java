package org.ytor.sql4j.sql.select;

import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.sql.SqlBuilder;
import org.ytor.sql4j.translate.ITranslator;

import java.util.ArrayList;
import java.util.List;

/**
 * SELECT 构造器
 */
public class SelectBuilder extends SqlBuilder {

    private Boolean isSub = false;

    /**
     * DISTINCT 阶段
     */
    private DistinctStage distinctStage;

    /**
     * SELECT 阶段
     */
    private SelectStage selectStage;

    /**
     * FROM 阶段
     */
    private FromStage fromStage;

    /**
     * JOIN 阶段，一句 SELECT 可能 JOIN 很多次
     */
    private List<JoinStage> joinStages;

    /**
     * WHERE阶段，也就是条件
     */
    private SelectWhereStage whereStage;

    /**
     * GROUP BY阶段
     */
    private GroupByStage groupByStage;

    /**
     * HAVING 阶段
     */
    private HavingStage havingStage;

    /**
     * ORDER BY 阶段
     */
    private OrderByStage orderByStage;

    /**
     * LIMIT 阶段
     */
    private LimitStage limitStage;

    /**
     * OFFSET 阶段
     */
    private OffsetStage offsetStage;

    public SelectBuilder(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    @Override
    public ITranslator getTranslator() {
        return sqlHelper.getTranslator();
    }

    @Override
    public Boolean getIsSub() {
        return isSub;
    }

    @Override
    public void isSub() {
        this.isSub = true;
    }

    public void setDistinctStage(DistinctStage distinctStage) {
        this.distinctStage = distinctStage;
    }

    public DistinctStage getDistinctStage() {
        return distinctStage;
    }

    public void setSelectStage(SelectStage selectStage) {
        this.selectStage = selectStage;
    }

    public SelectStage getSelectStage() {
        return selectStage;
    }

    public void setFromBuilder(FromStage fromStage) {
        this.fromStage = fromStage;
    }

    public FromStage getFromStage() {
        return fromStage;
    }

    public void addJoinStages(JoinStage joinStage) {
        if (joinStages == null) {
            joinStages = new ArrayList<>();
        }
        joinStages.add(joinStage);
    }

    public List<JoinStage> getJoinStages() {
        return joinStages;
    }

    public void setWhereStage(SelectWhereStage whereStage) {
        this.whereStage = whereStage;
    }

    public SelectWhereStage getWhereStage() {
        return whereStage;
    }

    public void setGroupByStage(GroupByStage groupByStage) {
        this.groupByStage = groupByStage;
    }

    public GroupByStage getGroupByStage() {
        return groupByStage;
    }

    public void setHavingStage(HavingStage havingStage) {
        this.havingStage = havingStage;
    }

    public HavingStage getHavingStage() {
        return havingStage;
    }

    public void setOrderByStage(OrderByStage orderByStage) {
        this.orderByStage = orderByStage;
    }

    public OrderByStage getOrderByStage() {
        return orderByStage;
    }

    public void setLimitStage(LimitStage limitStage) {
        this.limitStage = limitStage;
    }

    public LimitStage getLimitStage() {
        return limitStage;
    }

    public void setOffsetStage(OffsetStage offsetStage) {
        this.offsetStage = offsetStage;
    }

    public OffsetStage getOffsetStage() {
        return offsetStage;
    }
}
