package cn.filaura.weave.cache.ref;


import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 使用RedisTemplate完成引用缓存操作
 */
public class RedisRefDataCacheOperation implements RefDataCacheOperation {

    private final RedisTemplate<String, String> redisTemplate;

    /** 过期时间偏移量 */
    private int randomTtlOffset = 60 * 5;

    public RedisRefDataCacheOperation(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public int getRandomTtlOffset() {
        return randomTtlOffset;
    }

    public void setRandomTtlOffset(int randomTtlOffset) {
        this.randomTtlOffset = randomTtlOffset;
    }



    @Override
    public void cacheRef(Map<String, String> recordMap, long seconds) {
        if (recordMap == null || recordMap.isEmpty()) return;

        // 使用管道批量操作提高性能
        redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            for (Map.Entry<String, String> entry : recordMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // 为每条数据添加随机缓存时间
                long actualExpire = seconds + randomSeconds();

                connection.setEx(
                        key.getBytes(),
                        actualExpire,
                        value.getBytes()
                );
            }
            return null;
        });
    }

    @Override
    public List<String> loadRef(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) return Collections.emptyList();

        // 批量获取值
        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        // 确保返回列表与输入顺序一致
        return values != null ? values : keys.stream()
                .map(k -> (String) null)
                .collect(Collectors.toList());
    }

    @Override
    public void expireRef(Collection<String> keys, long seconds) {
        if (CollectionUtils.isEmpty(keys)) return;

        // 使用管道批量设置过期时间
        redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            for (String key : keys) {
                // 为每个key添加随机缓存时间
                long actualExpire = seconds + randomSeconds();

                connection.expire(
                        key.getBytes(),
                        actualExpire
                );
            }
            return null;
        });
    }

    @Override
    public void cacheTypeInfo(String hashKey, String field, String type) {
        // 类型信息永不过期
        redisTemplate.opsForHash().put(hashKey, field, type);
    }

    @Override
    public String loadTypeInfo(String hashKey, String field) {
        Object value = redisTemplate.opsForHash().get(hashKey, field);
        return value != null ? value.toString() : null;
    }

    @Override
    public long removeRef(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key)) ? 1 : 0;
    }

    @Override
    public long removeRef(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    private int randomSeconds() {
        return ThreadLocalRandom.current().nextInt(randomTtlOffset + 1);
    }

}
