package cn.filaura.weave.cache.ref;

import cn.filaura.weave.cache.AbstractCacheManager;
import cn.filaura.weave.cache.CacheOperation;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.ref.RecordCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordCacheManager extends AbstractCacheManager implements RecordCache {

    /** 缓存键前缀 */
    private static final String DEFAULT_PREFIX = "weave:record";

    /** 表级过期时间配置（数据记录类型全类名 -> 过期时间） */
    private final Map<String, Long> ttlByClassName = new HashMap<>();

    private String prefix = DEFAULT_PREFIX;

    /** 缓存键生成器，默认生成"前缀：类名：主键值" */
    private RecordCacheKeyGenerator keyGenerator = RecordCacheManager::buildCacheKey;

    public RecordCacheManager(CacheOperation cacheOperation, Serializer serializer) {
        super(cacheOperation, serializer);
    }

    public static String buildCacheKey(String prefix, Class<?> recordType, String id) {
        return prefix + ":" + recordType.getSimpleName() + ":" + id;
    }

    @Override
    public <T> Map<String, T> loadRecords(List<String> ids, Class<T> recordType) {
        return multiGet(ids, recordType, id ->
                keyGenerator.generateKey(prefix, recordType, id));
    }

    @Override
    public void putRecords(Map<String, ?> recordMap, Class<?> recordType) {
        Long ttl = ttlByClassName.getOrDefault(recordType.getName(), DEFAULT_TTL_SECONDS);
        if (ttl != null && ttl > 0) {
            multiSet(recordMap, ttl, id -> keyGenerator.generateKey(prefix, recordType, id));
        }
    }

    @Override
    public void removeRecords(List<String> ids, Class<?> recordType) {
        multiRemove(ids, id -> keyGenerator.generateKey(prefix, recordType, id));
    }

    @Override
    public void removeRecord(String id, Class<?> recordType) {
        remove(id, a -> keyGenerator.generateKey(prefix, recordType, a));
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public RecordCacheKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(RecordCacheKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public void registerTtl(String typeName, long seconds) {
        ttlByClassName.put(typeName, seconds);
    }

}
