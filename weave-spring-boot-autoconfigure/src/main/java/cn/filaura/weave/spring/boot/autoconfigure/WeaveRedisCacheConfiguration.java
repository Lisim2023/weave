package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.cache.CacheOperation;
import cn.filaura.weave.cache.RedisTemplateCacheOperation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;



@Configuration
@ConditionalOnClass({CacheOperation.class, StringRedisTemplate.class})
public class WeaveRedisCacheConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    public CacheOperation cacheOperation(StringRedisTemplate stringRedisTemplate) {
        return new RedisTemplateCacheOperation(stringRedisTemplate);
    }

}
