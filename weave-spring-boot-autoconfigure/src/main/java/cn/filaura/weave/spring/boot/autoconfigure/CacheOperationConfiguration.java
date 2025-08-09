package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.cache.dict.DictDataCacheOperation;
import cn.filaura.weave.cache.dict.RedisDictDataCacheOperation;
import cn.filaura.weave.cache.ref.RedisRefDataCacheOperation;
import cn.filaura.weave.cache.ref.RefDataCacheOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


/**
 * Redis缓存操作实现配置类
 * <p>
 * 提供基于Redis的字典数据和引用数据缓存操作实现，包括：
 * <ul>
 *   <li>Redis字典数据缓存操作器：（{@link RedisDictDataCacheOperation}）</li>
 *   <li>Redis引用数据缓存操作器：（{@link RedisRefDataCacheOperation}）</li>
 * </ul>
 * 要求项目中引入Redis相关依赖，并已将StringRedisTemplate配置为Spring Bean
 * </p>
 */
@Configuration
@ConditionalOnClass({RedisDictDataCacheOperation.class, RedisRefDataCacheOperation.class, StringRedisTemplate.class})
@EnableConfigurationProperties(WeaveProperties.class)
public class CacheOperationConfiguration {

    private final WeaveProperties weaveProperties;

    public CacheOperationConfiguration(WeaveProperties weaveProperties) {
        this.weaveProperties = weaveProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public DictDataCacheOperation dictDataCacheOperation(StringRedisTemplate stringRedisTemplate) {
        return new RedisDictDataCacheOperation(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RefDataCacheOperation refDataCacheOperation(StringRedisTemplate stringRedisTemplate) {
        RedisRefDataCacheOperation refRedisCacheOperations = new RedisRefDataCacheOperation(stringRedisTemplate);
        if (weaveProperties.getCache().getRefRandomTtlOffset() != null) {
            refRedisCacheOperations.setRandomTtlOffset(weaveProperties.getCache().getRefRandomTtlOffset());
        }
        return refRedisCacheOperations;
    }

}
