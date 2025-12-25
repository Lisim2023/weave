package cn.filaura.weave.cache;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisTemplateCacheOperation implements CacheOperation {

    private final StringRedisTemplate redisTemplate;

    public RedisTemplateCacheOperation(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, String> multiGet(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }

        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        List<Object> results = redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            for (String key : keys) {
                connection.get(stringSerializer.serialize(key));
            }
            return null;
        });

        Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String value = (String) results.get(i);
            if (value != null) {
                resultMap.put(keys.get(i), value);
            }
        }
        return resultMap;
    }

    @Override
    public void multiSet(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                connection.set(
                        stringSerializer.serialize(entry.getKey()),
                        stringSerializer.serialize(entry.getValue())
                );
            }
            return null;
        });
    }

    @Override
    public void multiSet(Map<String, String> data, long seconds) {
        if (data == null || data.isEmpty()) {
            return;
        }

        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                byte[] keyBytes = stringSerializer.serialize(entry.getKey());
                byte[] valueBytes = stringSerializer.serialize(entry.getValue());

                connection.setEx(keyBytes, seconds, valueBytes);
            }
            return null;
        });
    }

    @Override
    public void multiRemove(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
    }

    @Override
    public void remove(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        redisTemplate.delete(key);
    }
}
