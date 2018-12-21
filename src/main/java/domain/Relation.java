package domain;

/**
 * 目标表，影响表的关系类
 */
public class Relation {

    private String fromTable;
    private String toTable;

    public String getFromTable() {
        return fromTable;
    }

    public String getToTable() {
        return toTable;
    }

    public void setFromTable(String fromTable) {
        this.fromTable = fromTable;
    }

    public void setToTable(String toTable) {
        this.toTable = toTable;
    }
}
