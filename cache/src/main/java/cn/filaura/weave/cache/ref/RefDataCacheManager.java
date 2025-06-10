package cn.filaura.weave.cache.ref;



import cn.filaura.weave.MapUtils;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.ref.RefDataCache;
import cn.filaura.weave.ref.RefInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 引用数据缓存管理器
 * <p>引用缓存接口实现类，提供引用数据的缓存、加载、删除及过期时间管理功能。
 *
 * <p>可自定义缓存键前缀及自定义键生成策略
 * <p>可为不同表设置不同的过期时间
 *
 * <p>存取操作时忽略了参数中的columns，不支持列过滤
 * @see RefDataCache
 */
public class RefDataCacheManager implements RefDataCache {

    /** 缓存键前缀 */
    private String cacheKeyPrefix = "weave:ref";

    /** 缓存键生成器，默认生成"前缀：表名：主键名：主键值" */
    private CacheKeyGenerator cacheKeyGenerator = this::defaultKeyGenerator;

    /** 未设置主键时的占位符 */
    private String emptyKeyPlaceholder = "id";

    /** 默认全局过期时间（单位：秒） */
    private long defaultExpirySeconds = 60 * 60 * 24L;

    /** 表级过期时间配置（表名 -> 过期时间） */
    private Map<String, Long> tableExpiryMap = new HashMap<>();

    private final Serializer serializer;
    private final RefDataCacheOperations refDataCacheOperations;



    /**
     * 构造方法，需指定序列化器与缓存操作实现
     * @param serializer 序列化器
     * @param refDataCacheOperations 缓存操作接口
     */
    public RefDataCacheManager(Serializer serializer, RefDataCacheOperations refDataCacheOperations) {
        this.serializer = serializer;
        this.refDataCacheOperations = refDataCacheOperations;
    }



    /**
     * 缓存引用数据
     * @param refInfo 待缓存的引用信息对象
     */
    @Override
    public void cacheRef(RefInfo refInfo) {
        if (refInfo == null || refInfo.getResults() == null) {
            return;
        }

        Map<String, Map<String, Object>> results = refInfo.getResults();
        Map<String, String> serialized = new HashMap<>(results.size());
        results.forEach((k, v) -> {
            String cacheKey = generateCacheKey(refInfo.getTable(), refInfo.getKey(), k);
            String record = serializer.serialize(v);
            serialized.put(cacheKey, record);
        });
        long expiry = getExpiryTime(refInfo.getTable());
        refDataCacheOperations.cacheRef(serialized, expiry);
    }

    /**
     * 加载引用数据
     * @param table 表名
     * @param key 主键名
     * @param value 主键值
     * @return 引用数据对象
     */
    public RefInfo loadRef(String table, String key, String value) {
        List<String> values = Collections.singletonList(value);
        return loadRef(table, null, key, values);
    }

    /**
     * 批量加载引用数据
     * @param table 表名
     * @param columns 列名集合，接口预留参数，此实现中无实际作用
     * @param key 主键名
     * @param values 主键值集合
     * @return 引用数据对象
     */
    @Override
    public RefInfo loadRef(String table, Collection<String> columns, String key, Collection<String> values) {
        if (table == null || values == null || values.isEmpty()) {
            return null;
        }

        HashMap<String, Map<String, Object>> results = new HashMap<>(MapUtils.calculateHashMapCapacity(values.size()));
        Set<String> loadedValues = new LinkedHashSet<>();
        List<String> cacheKeys = values.stream()
                .map(value -> generateCacheKey(table, key, value))
                .collect(Collectors.toList());
        long expiry = getExpiryTime(table);
        refDataCacheOperations.expireRef(cacheKeys, expiry);
        List<String> records = refDataCacheOperations.loadRef(cacheKeys);

        int index = 0;
        for (String value : values) {
            String record = records.get(index++);
            if (record != null) {
                loadedValues.add(value);
                Map<String, Object> recordMap = (Map<String, Object>)(Map<String, ?>)serializer.deSerialize(record);
                results.put(value, recordMap);
            }
        }
        RefInfo refInfo = new RefInfo(table, key, results);
        refInfo.setKeyValues(loadedValues);
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
        String cacheKey = generateCacheKey(table, key, value);
        return refDataCacheOperations.removeRef(cacheKey);
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
                .map(value -> generateCacheKey(table, key, value))
                .collect(Collectors.toList());
        return refDataCacheOperations.removeRef(cacheKeys);
    }

    public String generateCacheKey(String table, String key, String value) {
        return cacheKeyGenerator.generateKey(cacheKeyPrefix, table, key, value);
    }

    private String defaultKeyGenerator(String prefix, String table, String key, String value) {
        String keyStr = (key == null || key.isEmpty()) ? emptyKeyPlaceholder : key;
        return prefix + ":" + table + ":" + keyStr + ":" + value;
    }



    public void setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    public String getCacheKeyPrefix() {
        return cacheKeyPrefix;
    }

    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    public String getEmptyKeyPlaceholder() {
        return emptyKeyPlaceholder;
    }

    public void setEmptyKeyPlaceholder(String emptyKeyPlaceholder) {
        this.emptyKeyPlaceholder = emptyKeyPlaceholder;
    }

    public long getDefaultExpirySeconds() {
        return defaultExpirySeconds;
    }

    public void setDefaultExpirySeconds(long defaultExpirySeconds) {
        this.defaultExpirySeconds = defaultExpirySeconds;
    }

    public void setTableCacheSeconds(String table, Long seconds) {
        tableExpiryMap.put(table, seconds);
    }

    public long getExpiryTime(String table) {
        return tableExpiryMap.getOrDefault(table, defaultExpirySeconds);
    }

    public Map<String, Long> getTableExpiryMap() {
        return tableExpiryMap;
    }

    public void setTableExpiryMap(Map<String, Long> tableExpiryMap) {
        this.tableExpiryMap = tableExpiryMap;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public RefDataCacheOperations getRefCacheHandler() {
        return refDataCacheOperations;
    }

}
