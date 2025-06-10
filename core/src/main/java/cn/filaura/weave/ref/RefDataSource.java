package cn.filaura.weave.ref;

import java.util.Collection;

/**
 * 引用数据源接口
 * <p>根据参数查询对应的数据，并将结果封装成要求的格式。
 */
public interface RefDataSource {

    /**
     * 查询引用数据
     *
     * @param table   数据表名
     * @param columns 需要查询的列名集合（可能省略）
     * @param key     主键名
     * @param values  主键值集合
     * @return 包含查询结果的RefInfo对象
     */
    RefInfo queryRefData(String table,
                         Collection<String> columns,
                         String key,
                         Collection<String> values);

}
