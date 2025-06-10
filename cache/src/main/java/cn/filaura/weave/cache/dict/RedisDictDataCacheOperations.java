package cn.filaura.weave.cache.dict;



import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;



/**
 * 使用RedisTemplate完成字典缓存操作
 *
 * @see DictDataCacheOperations
 */
public class RedisDictDataCacheOperations implements DictDataCacheOperations {

    private RedisTemplate<String, String> redisTemplate;

    private HashOperations<String, String, String> hashOperations() {
        return redisTemplate.opsForHash();
    }




    public RedisDictDataCacheOperations(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }




    @Override
    public void cacheDict(String key, String field, String value) {
        hashOperations().put(key, field, value);
    }

    @Override
    public void cacheDict(String key, Map<String, String> data) {
        hashOperations().putAll(key, data);
    }

    @Override
    public List<String> loadDict(String key, Collection<String> fields) {
        return hashOperations().multiGet(key, fields);
    }

    @Override
    public String loadDict(String key, String field) {
        return hashOperations().get(key, field);
    }

    @Override
    public Map<String, String> loadAllDict(String key) {
        return hashOperations().entries(key);
    }

    @Override
    public long removeDict(String key, String field) {
        return hashOperations().delete(key, field);
    }

    @Override
    public long removeDict(String key, Collection<String> fields) {
        return hashOperations().delete(key, fields);
    }

    @Override
    public long removeAllDict(String key) {
        Long size = redisTemplate.opsForHash().size(key);
        redisTemplate.delete(key);
        return size;
    }




    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
