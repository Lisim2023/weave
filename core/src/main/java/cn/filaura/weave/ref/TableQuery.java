package cn.filaura.weave.ref;

import java.util.*;

public class TableQuery {

    /** 表名 */
    private String table;

    /** 需要查询的目标字段集合 */
    private Set<String> columns = new HashSet<>();

    /** 主键名 */
    private String keyColumn;

    /** 待查询的主键值集合 */
    private Set<Object> ids = new HashSet<>();



    public TableQuery(String table, String keyColumn) {
        this.table = table;
        this.keyColumn = keyColumn;
    }


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public Set<Object> getIds() {
        return ids;
    }

    public void setIds(Set<Object> ids) {
        this.ids = ids;
    }

}
