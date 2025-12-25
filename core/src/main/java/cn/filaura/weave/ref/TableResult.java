package cn.filaura.weave.ref;


import java.util.Map;

public class TableResult {

    /** 原始的表查询条件 */
    private final TableQuery tableQuery;

    /**
     * 查询结果映射
     * <p>外层的Key为对象的ID，Value为以Map表示的数据对象</p>
     * <p>内层的Key为字段名，Value为字段值</p>
     */
    private final Map<String, Map<String, Object>> results;

    public TableResult(TableQuery tableQuery, Map<String, Map<String, Object>> results) {
        this.tableQuery = tableQuery;
        this.results = results;
    }

    public TableQuery getDbRefInfo() {
        return tableQuery;
    }

    public Map<String, Map<String, Object>> getResults() {
        return results;
    }
}
