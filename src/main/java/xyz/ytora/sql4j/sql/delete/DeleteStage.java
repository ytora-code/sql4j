package xyz.ytora.sql4j.sql.delete;

/**
 * DELETE 阶段，创建对象
 * <pre/>
 * DELETE FROM USER WHERE XXX
 */
public class DeleteStage extends AbsDelete {

    public DeleteStage(DeleteBuilder deleteBuilder) {
        setDeleteBuilder(deleteBuilder);
        getDeleteBuilder().setDeleteStage(this);
    }

    /**
     * DELETE 后面一定是 FROM 阶段
     */
    public FromStage from(Class<?> table) {
        return new FromStage(getDeleteBuilder(), table);
    }

    /**
     * DELETE 后面一定是 FROM 阶段
     */
    public FromStage from(String tableStr) {
        return new FromStage(getDeleteBuilder(), tableStr);
    }
}
