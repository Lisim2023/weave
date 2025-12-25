package cn.filaura.weave.cache;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractCacheManager {

    protected static long DEFAULT_TTL_SECONDS = 60 * 60 * 2L;

    private static double JITTER_RATIO = 0.1;
    private static int MAX_JITTER_SECONDS = 300;

    private final CacheOperation cacheOperation;
    private final Serializer serializer;

    public AbstractCacheManager(CacheOperation cacheOperation, Serializer serializer) {
        this.cacheOperation = cacheOperation;
        this.serializer = serializer;
    }

    protected Map<String, Map<String, Object>> multiGet(List<String> originalKeys,
                                                        Function<String, String> cacheKeyMapper) {
        return doMultiGet(originalKeys, cacheKeyMapper, serializer::deSerialize);
    }

    protected <T> Map<String, T> multiGet(List<String> originalKeys,
                                          Class<T> targetType,
                                          Function<String, String> cacheKeyMapper) {
        return doMultiGet(originalKeys, cacheKeyMapper,
                value -> serializer.deSerialize(value, targetType));
    }

    protected void multiSet(Map<String, ?> data,
                            Function<String, String> cacheKeyMapper) {
        doMultiSet(data, cacheKeyMapper, null);
    }

    protected void multiSet(Map<String, ?> data,
                            long seconds,
                            Function<String, String> cacheKeyMapper) {
        doMultiSet(data, cacheKeyMapper, seconds);
    }

    protected void multiRemove(List<String> originalKeys,
                               Function<String, String> cacheKeyMapper) {
        if (originalKeys == null || originalKeys.isEmpty()) {
            return;
        }
        List<String> cacheKeys = originalKeys.stream()
                .map(cacheKeyMapper)
                .collect(Collectors.toList());
        cacheOperation.multiRemove(cacheKeys);
    }

    protected void remove(String originalKey,
                          Function<String, String> cacheKeyMapper) {
        if (originalKey == null || originalKey.isEmpty()) {
            return;
        }
        String cacheKey = cacheKeyMapper.apply(originalKey);
        cacheOperation.remove(cacheKey);
    }



    private <T> Map<String, T> doMultiGet(List<String> originalKeys,
                                          Function<String, String> cacheKeyMapper,
                                          Function<String, T> deserializer) {

        if (originalKeys == null || originalKeys.isEmpty()) {
            return Collections.emptyMap();
        }

        // 构建缓存 key 与原始 key 的映射
        Map<String, String> cacheKeyToOriginalKey = new HashMap<>();
        for (String originalKey : originalKeys) {
            String cacheKey = cacheKeyMapper.apply(originalKey);
            cacheKeyToOriginalKey.put(cacheKey, originalKey);
        }

        // 批量从缓存获取
        List<String> cacheKeys = new ArrayList<>(cacheKeyToOriginalKey.keySet());
        Map<String, String> dataMap = cacheOperation.multiGet(cacheKeys);

        // 反序列化并构建结果
        Map<String, T> result = new HashMap<>(dataMap.size());
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String cacheKey = entry.getKey();
            String value = entry.getValue();
            if (value != null) {
                T data = deserializer.apply(value); // 使用传入的反序列化逻辑
                String originalKey = cacheKeyToOriginalKey.get(cacheKey);
                result.put(originalKey, data);
            }
        }
        return result;
    }

    private void doMultiSet(Map<String, ?> recordMap,
                            Function<String, String> cacheKeyMapper,
                            Long ttl) {
        if (recordMap == null || recordMap.isEmpty()) {
            return;
        }
        Map<String, String> serialized = new HashMap<>(recordMap.size());
        for (Map.Entry<String, ?> entry : recordMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String serializedValue = serializer.serialize(value);
                String cacheKey = cacheKeyMapper.apply(key);
                serialized.put(cacheKey, serializedValue);
            }
        }
        if (ttl != null) {
            long safeTtl = addRandomJitter(ttl);
            cacheOperation.multiSet(serialized, safeTtl);
        } else {
            cacheOperation.multiSet(serialized); // overload without TTL
        }
    }

    private long addRandomJitter(long ttl) {
        if (ttl <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        if (JITTER_RATIO < 0 || JITTER_RATIO > 1) {
            throw new IllegalArgumentException("jitterRatio must be between 0.0 and 1.0");
        }
        if (MAX_JITTER_SECONDS < 0) {
            throw new IllegalArgumentException("maxJitterSeconds must be non-negative");
        }

        long calculatedJitter = (long) (ttl * JITTER_RATIO);
        long jitter = Math.min(calculatedJitter, MAX_JITTER_SECONDS);
        jitter = Math.max(jitter, 0);

        if (jitter == 0) {
            return ttl;
        }

        return ttl + ThreadLocalRandom.current().nextLong(jitter + 1);
    }


    public static long getDefaultTtlSeconds() {
        return DEFAULT_TTL_SECONDS;
    }

    public static void setDefaultTtlSeconds(long defaultTtlSeconds) {
        AbstractCacheManager.DEFAULT_TTL_SECONDS = defaultTtlSeconds;
    }

    public static double getJitterRatio() {
        return JITTER_RATIO;
    }

    public static void setJitterRatio(double jitterRatio) {
        AbstractCacheManager.JITTER_RATIO = jitterRatio;
    }

    public static int getMaxJitterSeconds() {
        return MAX_JITTER_SECONDS;
    }

    public static void setMaxJitterSeconds(int maxJitterSeconds) {
        AbstractCacheManager.MAX_JITTER_SECONDS = maxJitterSeconds;
    }

}
