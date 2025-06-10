package cn.filaura.weave.example.config;



import cn.filaura.weave.cache.JacksonSerializer;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.cache.dict.DictDataCacheManager;
import cn.filaura.weave.cache.dict.DictDataCacheOperations;
import cn.filaura.weave.cache.dict.RedisDictDataCacheOperations;
import cn.filaura.weave.cache.ref.RedisRefDataCacheOperations;
import cn.filaura.weave.cache.ref.RefDataCacheManager;
import cn.filaura.weave.cache.ref.RefDataCacheOperations;
import cn.filaura.weave.dict.*;
import cn.filaura.weave.ref.*;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 带缓存的配置，启用前需要先配置redis，并禁用WeaveConfig1
 */
//@Configuration
public class WeaveConfig2 {

    @Resource(name = "refDataSource1")
    private RefDataSource refDataSource;

    @Resource(name = "dictDataSourceImpl")
    private DictDataSource dictDataSource;

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;



    @Bean
    public Serializer serializer() {
        return new JacksonSerializer();
    }


    @Bean
    public RefDataCacheManager refDataCacheManager() {
        RefDataCacheOperations refDataCacheOperations = new RedisRefDataCacheOperations(stringRedisTemplate);
        return new RefDataCacheManager(serializer(), refDataCacheOperations);
    }

    @Bean
    public RefHelper refHelper() {
        RefDataProvider refDataProvider = new CacheFirstRefDataProvider(refDataSource, refDataCacheManager());
        return new RefHelper(refDataProvider);
    }


    @Bean
    public DictDataCacheManager dictDataCacheManager() {
        DictDataCacheOperations dictDataCacheOperations = new RedisDictDataCacheOperations(stringRedisTemplate);
        return new DictDataCacheManager(serializer(), dictDataCacheOperations);
    }

    @Bean
    public DictHelper dictHelper() {
        DictDataProvider dictDataProvider = new CacheFirstDictDataProvider(dictDataSource, dictDataCacheManager());
        return new DictHelper(dictDataProvider);
    }
}
