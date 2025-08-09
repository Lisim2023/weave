package cn.filaura.weave.ref;

import java.util.Collection;
import java.util.List;


/**
 * 引用数据源接口，查询符合条件的数据记录以供引用
 */
public interface RefDataSource {

    /**
     * 查询引用数据
     *
     * @param table   数据表名
     * @param columns 需要查询的列名集合（可能省略）
     * @param key     主键名
     * @param values  主键值集合
     * @return 数据记录列表，可以是以下两种形式之一：
     * <ul>
     *     <li>封装好的数据对象列表，如{@code List<User>}、{@code List<Product>}等</li>
     *     <li>Map形式的数据记录列表，即{@code List<Map<String, Object>>}</li>
     * </ul>
     */
    List<?> queryRefData(String table,
                         Collection<String> columns,
                         String key,
                         Collection<String> values);

}
