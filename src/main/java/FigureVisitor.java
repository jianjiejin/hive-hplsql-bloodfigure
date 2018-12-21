import domain.Relation;
import java.util.HashSet;
import java.util.Set;


public class FigureVisitor extends HplsqlBaseVisitor {

    private String tableName = null;
    private String fromTableName = null;
    private String procName = null;
    private String viewName = null;
    //用于存放最终结果
    Set<Relation> resultSet = new HashSet<Relation>();
    //用于存放影响表,保存结果中使用，会清空
    Set<String> tmpSet = new HashSet<>();
    // 用于结果去重
    Set<String> addedSet = new HashSet<>();

    /**
     * 判断是否是表名，过滤中间表，日志表
     */
    private boolean isTableName(String name) {
        if (!name.contains("SESSION.") && !name.contains("ETL_ERRLOG_INFO")
            && !name.contains("ETL.PROCLOG")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将表之间关系，或表和存储过程关系存入 relation 对象
     */
    private void insertResultSet(String name1, String name2) {
        Relation relation = new Relation();
        relation.setFromTable(name1);
        relation.setToTable(name2);
        resultSet.add(relation);
        addedSet.add(name1 + name2);
    }

    /**
     * 保存结果
     */
     private void saveResult(String tableName, Set<String> tmpSet) {
        if (isTableName(tableName)) {
            //判断是否获得存储过程名和影响表
            if (!tmpSet.isEmpty()) {
                if (procName != null) {
                    //先保存存储过程和目标表的关系
                    if (!addedSet.contains(procName + tableName)) {
                        insertResultSet(procName, tableName);
                    }
                    //再保存影响表和存储过程的关系
                    for (String fromTableName : tmpSet
                    ) {
                        if (!addedSet.contains(fromTableName + procName)) {
                            insertResultSet(fromTableName, procName);
                        }
                    }
                    //保存后，清空 tmpSet
                    tmpSet.clear();
                } else {
                    //不存在存储过程名

                    for (String fromTableName : tmpSet) {
                        if (!addedSet.contains(fromTableName + tableName)) {
                            insertResultSet(fromTableName, tableName);
                        }

                    }
                    tmpSet.clear();
                }
            }
        }
    }


    /**
     *
     * @param ctx
     * @return
     */
    @Override
    public Object visitInsert_stmt(HplsqlParser.Insert_stmtContext ctx) {
        tableName = ctx.table_name().getText().toUpperCase();
        //先保存一次结果
        saveResult(tableName, tmpSet);
        //返回执行对象，否则会退出遍历
        Object ctx2 = visitChildren(ctx);
        //遍历之后再保存一次结果
        saveResult(tableName, tmpSet);
        return ctx2;
    }

    /**
     *
     * @param ctx
     * @return
     */
    @Override
    public Object visitFrom_table_name_clause(HplsqlParser.From_table_name_clauseContext ctx) {
        fromTableName = ctx.table_name().ident().getText().toUpperCase();
        if (!fromTableName.contains("SESSION.")) {
            tmpSet.add(fromTableName);
        }

        return visitChildren(ctx);
    }

    /**
     *
     * @param ctx
     * @return
     */
    @Override
    public Object visitCreate_procedure_stmt(HplsqlParser.Create_procedure_stmtContext ctx) {
        procName = ctx.ident().get(0).getText().toUpperCase();
        return visitChildren(ctx);
    }

    /**
     *
     * @param ctx
     * @return
     */
    @Override
    public Object visitMerge_stmt(HplsqlParser.Merge_stmtContext ctx) {
        tableName = ctx.merge_table(0).table_name().ident().getText().toUpperCase();
        saveResult(tableName, tmpSet);
        Object ctx2 = visitChildren(ctx);
        if (ctx.merge_table(1) != null && ctx.merge_table(1).select_stmt() == null) {
            fromTableName = ctx.merge_table(1).table_name().ident().getText().toUpperCase();
            tmpSet.add(fromTableName);
        }
        saveResult(tableName, tmpSet);
        return ctx2;
    }

    /**
     * 获取视图表名，直接存入结果
     */
    @Override
    public Object visitCreate_view_stmt(HplsqlParser.Create_view_stmtContext ctx) {
        viewName = ctx.ident().getText().toUpperCase();
        Object ctx2 = visitChildren(ctx);
        for (String tableName : tmpSet) {
            if (!addedSet.contains(tableName + viewName)) {
                insertResultSet(tableName, viewName);
            }
        }
        tmpSet.clear();
        return ctx2;
    }

    /**
     * 返回结果
     */
    public Set<Relation> getResultSet() {
        return resultSet;
    }

}
