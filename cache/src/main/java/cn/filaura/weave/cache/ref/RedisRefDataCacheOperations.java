package cn.filaura.weave.cache.ref;


import org.springframework.data.redis.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 使用RedisTemplate完成引用缓存操作
 */
public class RedisRefDataCacheOperations implements RefDataCacheOperations {

    /** 随机秒数上限 */
    private int randomUpperBound = 60 * 2;

    private RedisTemplate<String, String> redisTemplate;



    public RedisRefDataCacheOperations(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }



    @Override
    public void cacheRef(String key, String record, long seconds) {
        redisTemplate.opsForValue().set(key, record, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void cacheRef(Map<String, String> recordMap, long seconds) {
        if (recordMap == null || recordMap.isEmpty()) {
            return;
        }
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) {
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
                recordMap.forEach((key, record) -> {
                    ops.opsForValue().set(key, record, seconds + randomSeconds(), TimeUnit.SECONDS);
                });
                return null;
            }
        });
    }

    @Override
    public List<String> loadRef(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public void expireRef(Collection<String> keys, long seconds) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) {
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
                keys.forEach(key -> {
                    ops.expire(key, seconds + randomSeconds(), TimeUnit.SECONDS);
                });
                return null;
            }
        });
    }

    @Override
    public long removeRef(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key)) ? 1 : 0;
    }

    @Override
    public long removeRef(Collection<String> keys) {
        Long delete = redisTemplate.delete(keys);
        return delete == null ? 0 : delete;
    }

    private int randomSeconds() {
        return (int) (Math.random() * randomUpperBound);
    }



    public int getRandomUpperBound() {
        return randomUpperBound;
    }

    public void setRandomUpperBound(int upperBound) {
        this.randomUpperBound = Math.max(upperBound, 0);
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
