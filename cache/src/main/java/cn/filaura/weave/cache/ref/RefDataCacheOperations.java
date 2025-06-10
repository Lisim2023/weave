package cn.filaura.weave.cache.ref;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 引用数据缓存操作接口
 *
 * @see RefDataCacheManager
 */
public interface RefDataCacheOperations {

    /**
     * 缓存单个引用数据，并设置过期时间。
     *
     * @param key 缓存的键
     * @param record 供引用的数据
     * @param seconds 过期时间（秒）
     */
    void cacheRef(String key, String record, long seconds);

    /**
     * 批量缓存引用数据，并设置过期时间。
     *
     * @param recordMap 包含多条引用数据的Map，key为缓存键，value为引用数据
     * @param seconds 过期时间（秒）
     */
    void cacheRef(Map<String, String> recordMap, long seconds);

    /**
     * 批量加载引用数据。
     * <p>返回值的数量与顺序应与参数keys一致
     *
     * @param keys 缓存的键集合
     * @return 引用数据列表（数量与顺序应与参数keys一致）
     */
    List<String> loadRef(Collection<String> keys);

    /**
     * 批量设置缓存过期时间。
     *
     * @param keys   缓存的键集合
     * @param seconds 过期时间（秒）
     */
    void expireRef(Collection<String> keys, long seconds);

    /**
     * 删除单个引用缓存。
     *
     * @param key 缓存的键
     * @return 返回删除操作的结果
     */
    long removeRef(String key);

    /**
     * 批量删除引用缓存。
     *
     * @param keys 缓存的键集合
     * @return 返回删除操作的结果数量
     */
    long removeRef(Collection<String> keys);

}
