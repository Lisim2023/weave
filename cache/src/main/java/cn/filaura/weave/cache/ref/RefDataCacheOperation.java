package cn.filaura.weave.cache.ref;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 引用数据缓存操作接口
 *
 * @see RedisRefDataCacheOperation
 */
public interface RefDataCacheOperation {


    /**
     * 缓存类型信息到Hash结构
     *
     * @param hashKey  类型信息存储的Hash键
     * @param field    Hash字段（格式：表名_主键名）
     * @param type     类型全限定名
     */
    void cacheTypeInfo(String hashKey, String field, String type);

    /**
     * 从Hash结构中加载类型信息
     *
     * @param hashKey 类型信息存储的Hash键
     * @param field   Hash字段（格式：表名_主键名）
     * @return        类型全限定名，不存在时返回null
     */
    String loadTypeInfo(String hashKey, String field);


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
