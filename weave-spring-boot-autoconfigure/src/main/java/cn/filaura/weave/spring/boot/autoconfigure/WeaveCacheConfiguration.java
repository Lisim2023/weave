package cn.filaura.weave.spring.boot.autoconfigure;



import cn.filaura.weave.cache.*;

import cn.filaura.weave.cache.dict.DictCacheKeyGenerator;
import cn.filaura.weave.cache.dict.DictCacheManager;
import cn.filaura.weave.cache.ref.ColumnProjectionCacheKeyGenerator;
import cn.filaura.weave.cache.ref.ColumnProjectionCacheManager;
import cn.filaura.weave.cache.ref.RecordCacheKeyGenerator;
import cn.filaura.weave.cache.ref.RecordCacheManager;
import cn.filaura.weave.dict.DictCache;

import cn.filaura.weave.ref.ColumnProjectionCache;
import cn.filaura.weave.ref.RecordCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnClass({CacheOperation.class, Serializer.class})
@EnableConfigurationProperties(WeaveProperties.class)
@Import({WeaveRedisCacheConfiguration.class, WeaveJacksonSerializerConfiguration.class})
public class WeaveCacheConfiguration {

    private final WeaveProperties weaveProperties;

    public WeaveCacheConfiguration(WeaveProperties weaveProperties) {
        this.weaveProperties = weaveProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public DictCacheKeyGenerator dictCacheKeyGenerator() {
        return DictCacheManager::buildCacheKey;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WeaveProperties.DICT_CACHE_ENABLED)
    @ConditionalOnBean({CacheOperation.class, Serializer.class})
    public DictCache dictCache(CacheOperation cacheOperation,
                               Serializer serializer,
                               DictCacheKeyGenerator keyGenerator) {
        DictCacheManager dictCacheManager = new DictCacheManager(cacheOperation, serializer);
        if (weaveProperties.getCache().getDictPrefix() != null) {
            dictCacheManager.setPrefix(weaveProperties.getCache().getDictPrefix());
        }
        dictCacheManager.setKeyGenerator(keyGenerator);
        return dictCacheManager;
    }



    @Bean
    @ConditionalOnMissingBean
    public ColumnProjectionCacheKeyGenerator columnProjectionCacheKeyGenerator() {
        return ColumnProjectionCacheManager::buildCacheKey;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WeaveProperties.COLUMN_PROJECTION_CACHE_ENABLED)
    @ConditionalOnBean({CacheOperation.class, Serializer.class})
    public ColumnProjectionCache columnProjectionCache(
            CacheOperation cacheOperation,
            Serializer serializer,
            ColumnProjectionCacheKeyGenerator keyGenerator) {
        ColumnProjectionCacheManager columnProjectionCacheManager =
                new ColumnProjectionCacheManager(cacheOperation, serializer);
        WeaveProperties.Cache cache = weaveProperties.getCache();
        if (cache.getColumnProjectionPrefix() != null) {
            columnProjectionCacheManager.setPrefix(cache.getColumnProjectionPrefix());
        }
        if (cache.getTtlSeconds() != null) {
            AbstractCacheManager.setDefaultTtlSeconds(cache.getTtlSeconds());
        }
        if (cache.getMaxJitterSeconds() != null) {
            AbstractCacheManager.setMaxJitterSeconds(cache.getMaxJitterSeconds());
        }
        if (cache.getJitterRatio() != null) {
            AbstractCacheManager.setJitterRatio(cache.getJitterRatio());
        }
        if (cache.getTtlByTable() != null) {
            cache.getTtlByTable().forEach(columnProjectionCacheManager::registerTtl);
        }
        columnProjectionCacheManager.setKeyGenerator(keyGenerator);
        return columnProjectionCacheManager;
    }



    @Bean
    @ConditionalOnMissingBean
    public RecordCacheKeyGenerator recordCacheKeyGenerator() {
        return RecordCacheManager::buildCacheKey;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WeaveProperties.RECORD_CACHE_ENABLED)
    @ConditionalOnBean({CacheOperation.class, Serializer.class})
    public RecordCache recordCache(CacheOperation cacheOperation,
                                   Serializer serializer,
                                   RecordCacheKeyGenerator keyGenerator) {
        RecordCacheManager recordCacheManager = new RecordCacheManager(cacheOperation, serializer);
        WeaveProperties.Cache cache = weaveProperties.getCache();
        if (cache.getRecordPrefix() != null) {
            recordCacheManager.setPrefix(cache.getRecordPrefix());
        }
        if (cache.getTtlSeconds() != null) {
            AbstractCacheManager.setDefaultTtlSeconds(cache.getTtlSeconds());
        }
        if (cache.getMaxJitterSeconds() != null) {
            AbstractCacheManager.setMaxJitterSeconds(cache.getMaxJitterSeconds());
        }
        if (cache.getJitterRatio() != null) {
            AbstractCacheManager.setJitterRatio(cache.getJitterRatio());
        }
        if (cache.getTtlByClassName() != null) {
            cache.getTtlByClassName().forEach(recordCacheManager::registerTtl);
        }
        recordCacheManager.setKeyGenerator(keyGenerator);
        return recordCacheManager;
    }

}
