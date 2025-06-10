package cn.filaura.weave.ref;



import java.util.*;

/**
 * 引用信息类
 *
 * <p>以表为单位保存查询引用数据需要的参数以及查询结果
 * <p>通过集中管理查询参数，避免对数据库的重复访问。
 */
public class RefInfo {

    /**
     * 将Map的键转换为String类型（忽略null键），便于后续处理时保持键类型一致。
     *
     * @param data 原始数据
     * @return 转换后的数据
     */
    public static <T> Map<String, T> mapKeyToString(Map<Object, T> data) {
        if (data == null) {
            return new HashMap<>();
        }
        Map<String, T> resultMap = new HashMap<>(data.size());
        data.forEach((key, value) -> {
            if (key != null) {
                resultMap.put(key.toString(), value);
            }
        });

        return resultMap;
    }



    /** 表名 */
    private String table;

    /** 需要查询的目标字段集合（可能省略） */
    private Set<String> columns = new HashSet<>();

    /** 主键名 */
    private String key;

    /** 待查询的主键值集合 */
    private Set<String> keyValues = new LinkedHashSet<>();

    /**
     * 查询结果
     * <p>
     * 双层Map结构：
     * <ul>
     *   <li>外层Key: 主键值（字符串形式）</li>
     *   <li>内层Map: 主键值对应的数据记录（键为字段名，值为字段值）</li>
     * </ul>
     * <p>例如：
     * <pre>{@code
     * {
     * "1": {"name": "Alice", "age": "20"},
     * "2": {"name": "Bob", "age": "25"}
     * }
     * }
     */
    private Map<String, Map<String, Object>> results = new HashMap<>();



    public RefInfo(String table, String key){
        this.table = table;
        this.key = key;
    }

    public RefInfo(String table, Set<String> columns, String key, Set<String> keyValues){
        this(table, key);
        this.columns = columns;
        this.keyValues = keyValues;
    }

    public RefInfo(String table, String key, Map<String, Map<String, Object>> results) {
        this(table, key);
        this.results = results;
    }

    public RefInfo(String table, Set<String> columns, String key, Set<String> keyValues, Map<String, Map<String, Object>> results){
        this(table, columns, key, keyValues);
        this.results = results;
    }



    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public Set<String> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(Set<String> keyValues) {
        this.keyValues = keyValues;
    }

    public Map<String, Map<String, Object>> getResults() {
        return results;
    }

    public void setResults(Map<String, Map<String, Object>> results) {
        this.results = results;
    }

}
