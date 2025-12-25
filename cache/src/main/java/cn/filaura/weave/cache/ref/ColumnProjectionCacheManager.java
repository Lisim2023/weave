package cn.filaura.weave.cache.ref;

import cn.filaura.weave.cache.AbstractCacheManager;
import cn.filaura.weave.cache.CacheOperation;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.ref.ColumnProjectionCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnProjectionCacheManager extends AbstractCacheManager
        implements ColumnProjectionCache {

    /** 缓存键前缀 */
    private static final String DEFAULT_PREFIX = "weave:column_projection";

    /** 表级过期时间配置（表名 -> 过期时间） */
    private final Map<String, Long> ttlByTable = new HashMap<>();

    private String prefix = DEFAULT_PREFIX;

    /** 缓存键生成器，默认生成"前缀：类名：主键值" */
    private ColumnProjectionCacheKeyGenerator keyGenerator =
            ColumnProjectionCacheManager::buildCacheKey;

    public ColumnProjectionCacheManager(CacheOperation cacheOperation, Serializer serializer) {
        super(cacheOperation, serializer);
    }

    public static String buildCacheKey(String prefix, String table, String keyColumn, String id) {
        return prefix + ":" + table + ":" + keyColumn + ":" + id;
    }

    @Override
    public Map<String, Map<String, Object>> loadProjections(String table,
                                                            String keyColumn,
                                                            List<String> ids) {
        return multiGet(ids, id -> keyGenerator.generateKey(prefix, table, keyColumn, id));
    }

    @Override
    public void putProjections(String table,
                               String keyColumn,
                               Map<String, Map<String, Object>> recordMap) {
        Long ttl = ttlByTable.getOrDefault(table, DEFAULT_TTL_SECONDS);
        if (ttl != null && ttl > 0) {
            multiSet(recordMap, ttl, id ->
                    keyGenerator.generateKey(prefix, table, keyColumn, id));
        }
    }

    @Override
    public void removeProjections(String table, String keyColumn, List<String> ids) {
        multiRemove(ids, id -> keyGenerator.generateKey(prefix, table, keyColumn, id));
    }

    @Override
    public void removeProjection(String table, String keyColumn, String id) {
        remove(id, a -> keyGenerator.generateKey(prefix, table, keyColumn, a));
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ColumnProjectionCacheKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(ColumnProjectionCacheKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public void registerTtl(String table, long seconds) {
        ttlByTable.put(table, seconds);
    }

}
