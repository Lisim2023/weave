package cn.filaura.weave.ref;

import java.util.Collection;

/**
 * 引用数据缓存接口
 * <p>根据提供的参数查询需要的数据，并将结果封装成指定的格式。
 */
public interface RefDataCache {

    /**
     * 缓存引用数据
     * @param refInfo 引用信息对象
     */
    void cacheRef(RefInfo refInfo);

    /**
     * 加载引用数据
     *
     * @param table   数据表名
     * @param columns 需要查询的列名集合（可能省略）
     * @param key     主键名
     * @param values  主键值集合
     * @return 包含查询结果的RefInfo对象
     */
    RefInfo loadRef(String table,
                    Collection<String> columns,
                    String key,
                    Collection<String> values);

}
