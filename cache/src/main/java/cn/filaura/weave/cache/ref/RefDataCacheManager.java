package cn.filaura.weave.cache.ref;



import cn.filaura.weave.cache.SerializationException;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.ref.RefDataCache;
import cn.filaura.weave.ref.RefInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 引用数据缓存管理器
 * <p>引用缓存接口实现类，提供引用数据的缓存、加载、删除及过期时间管理功能。
 *
 * <p>可自定义缓存键前缀及自定义键生成策略
 * <p>可为不同表设置不同的过期时间
 *
 * @see RefDataCache
 */
public class RefDataCacheManager implements RefDataCache {

    /** 缓存键前缀 */
    private static final String DEFAULT_PREFIX = "weave:ref";
    private static final String TYPE_HASH_KEY_SUFFIX = "::type_info";

    /** 缓存键生成器，默认生成"前缀：表名：主键名：主键值" */
    private CacheKeyGenerator cacheKeyGenerator = this::defaultKeyGenerator;

    /** 全局过期时间（单位：秒） */
    private long globalTtl = 60 * 60 * 24L;

    /** 表级过期时间配置（表名 -> 过期时间） */
    private final Map<String, Long> tableTtlMap = new ConcurrentHashMap<>();

    /** 类型信息本地缓存 */
    private final Map<String, Class<?>> typeInfoCache = new ConcurrentHashMap<>();

    private final RefDataCacheOperation cacheOperations;
    private final Serializer serializer;
    private String prefix = DEFAULT_PREFIX;

    public RefDataCacheManager(Serializer serializer, RefDataCacheOperation cacheOperations) {
        this.cacheOperations = cacheOperations;
        this.serializer = serializer;
    }

    /** 设置缓存键前缀 */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /** 设置全局过期时间 */
    public void setGlobalTtl(long globalTtl) {
        this.globalTtl = globalTtl;
    }

    /** 设置表级过期时间 */
    public void setTableTtl(String table, long seconds) {
        tableTtlMap.put(table, seconds);
    }

    /** 设置缓存键生成器 */
    public void setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @Override
    public void cacheRef(RefInfo refInfo) {
        if (refInfo == null) return;

        String table = refInfo.getTable();
        String key = refInfo.getKey();
        Map<String, Object> records = refInfo.getResults();
        if (records == null || records.isEmpty()) return;

        // 计算表级过期时间
        long expireSeconds = getExpireSeconds(table);
        if (expireSeconds == 0) return;

        // 批量缓存数据
        Map<String, String> cacheMap = new HashMap<>();
        String typeField = null;
        Class<?> recordClass = null;

        for (Map.Entry<String, Object> entry : records.entrySet()) {
            String recordKey = buildCacheKey(table, key, entry.getKey());
            Object record = entry.getValue();

            // 序列化数据
            String serialized = serializer.serialize(record);
            cacheMap.put(recordKey, serialized);

            // 记录类型信息（仅非Map类型）
            if (!(record instanceof Map) && record != null) {
                if (recordClass == null) {
                    recordClass = record.getClass();
                    typeField = buildTypeField(table, key);
                }
            }
        }

        // 批量缓存数据
        if (!cacheMap.isEmpty()) {
            cacheOperations.cacheRef(cacheMap, expireSeconds);
        }

        // 缓存类型信息
        if (typeField != null && recordClass != null) {
            typeInfoCache.put(typeField, recordClass);
            cacheOperations.cacheTypeInfo(getTypeHashKey(), typeField, recordClass.getName());
        }
    }

    @Override
    public RefInfo loadRef(String table, Collection<String> columns, String key, Collection<String> values) {
        RefInfo refInfo = new RefInfo(table, key);
        if (values == null || values.isEmpty()) return refInfo;

        Map<String, Object> result = new HashMap<>();
        // 获取类型信息
        String typeField = buildTypeField(table, key);
        Class<?> targetClass = typeInfoCache.computeIfAbsent(typeField, k -> {
            String className = cacheOperations.loadTypeInfo(getTypeHashKey(), typeField);
            if (className != null) {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new SerializationException(e);
                }
            }
            return null;
        });

        // 构建缓存键并批量加载
        List<String> cacheKeys = values.stream()
                .map(value -> buildCacheKey(table, key, value))
                .collect(Collectors.toList());
        List<String> cachedData = cacheOperations.loadRef(cacheKeys);

        // 反序列化数据
        Iterator<String> keyIter = values.iterator();
        Iterator<String> dataIter = cachedData.iterator();

        while (keyIter.hasNext() && dataIter.hasNext()) {
            String dataValue = dataIter.next();
            String originalKey = keyIter.next();

            if (dataValue != null) {
                Object record;
                if (targetClass == null) {
                    record = serializer.deSerialize(dataValue);
                }else {
                    record = serializer.deSerialize(dataValue, targetClass);
                }
                result.put(originalKey, record);
            }
        }

        // 刷新过期时间
        long expireSeconds = getExpireSeconds(table);
        cacheOperations.expireRef(cacheKeys, expireSeconds);

        refInfo.setResults(result);
        return refInfo;
    }

    /**
     * 删除引用数据
     * @param table 表名
     * @param key 主键名
     * @param value 主键值
     * @return 删除的引用数据条数
     */
    public long removeRef(String table, String key, String value) {
        String cacheKey = buildCacheKey(table, key, value);
        return cacheOperations.removeRef(cacheKey);
    }

    /**
     * 批量删除引用数据
     * @param table 表名
     * @param key 主键名
     * @param values 主键值集合
     * @return 删除的引用数据条数
     */
    public long removeRef(String table, String key, Collection<String> values) {
        Objects.requireNonNull(values, "values cannot be null");
        List<String> cacheKeys = values.stream()
                .map(value -> buildCacheKey(table, key, value))
                .collect(Collectors.toList());
        return cacheOperations.removeRef(cacheKeys);
    }

    private String defaultKeyGenerator(String prefix, String table, String key, String value) {
        if (key == null || key.isEmpty()) {
            return String.join(":", prefix, table, value);
        }
        return String.join(":", prefix, table, key, value);
    }

    /** 获取过期时间 */
    private long getExpireSeconds(String table) {
        Long tableExpire = tableTtlMap.get(table);
        return tableExpire != null ? tableExpire : globalTtl;
    }

    private String buildCacheKey(String table, String key, String value) {
        return cacheKeyGenerator.generateKey(prefix, table, key, value);
    }

    private String getTypeHashKey() {
        return prefix + TYPE_HASH_KEY_SUFFIX;
    }

    /** 构建类型键：前缀:type:表名_主键名 */
    private String buildTypeField(String table, String key) {
        if (key == null || key.isEmpty()) {
            return table;
        }
        return table + "_" + key;
    }

}