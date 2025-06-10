package cn.filaura.weave.cache.dict;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 字典缓存操作接口
 * <p>提供字典数据的存储、加载和删除功能。
 */
public interface DictDataCacheOperations {

    /**
     * 缓存字典数据
     * @param key 主键
     * @param field 字典编码
     * @param value 字典数据
     */
    void cacheDict(String key, String field, String value);

    /**
     * 批量缓存字典数据
     *
     * @param key 主键
     * @param data 多个字典数据，key为字典编码，value为字典数据
     */
    void cacheDict(String key, Map<String, String> data);

    /**
     * 加载字典数据
     * @param key 主键
     * @param field 字典编码
     * @return 对应的值
     */
    String loadDict(String key, String field);

    /**
     * 批量加载字典数据
     * <p>返回的列表顺序与输入字典编码集合的顺序一致，如果某个字段不存在则对应位置为null。
     *
     * @param key 主键，字典数据在缓存中的主键
     * @param fields 要加载的字典编码集合
     * @return 包含对应值的列表（列表的数量和顺序与字典编码一致）
     */
    List<String> loadDict(String key, Collection<String> fields);

    /**
     * 加载指定键下的所有字典的数据
     * @param key 主键，字典数据在缓存中的主键
     * @return 包含所有字典编码和对应字典数据的键值对映射
     */
    Map<String, String> loadAllDict(String key);

    /**
     * 删除指定字典字段
     * @param key 主键
     * @param field 需要删除的字典编码
     * @return 被删除的字典字段数量
     */
    long removeDict(String key, String field);

    /**
     * 批量删除指定键中的字典字段
     * @param key 主键
     * @param fields 需要删除的字典编码集合
     * @return 被删除的字典字段数量
     */
    long removeDict(String key, Collection<String> fields);


    /**
     * 删除指定键中的全部字典字段
     * @param key 主键
     * @return 被删除的字段数量
     */
    long removeAllDict(String key);
}
