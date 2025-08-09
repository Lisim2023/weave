package cn.filaura.weave.spring.boot.autoconfigure;



import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.cache.dict.DictDataCacheManager;
import cn.filaura.weave.cache.dict.DictDataCacheOperation;
import cn.filaura.weave.cache.ref.RefDataCacheManager;
import cn.filaura.weave.cache.ref.RefDataCacheOperation;
import cn.filaura.weave.dict.DictDataCache;
import cn.filaura.weave.ref.RefDataCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 缓存管理自动配置类
 * <p>
 * 负责配置Weave框架中字典数据和引用数据的缓存管理组件，包括：
 * <ul>
 *   <li>字典数据缓存管理器：（{@link DictDataCacheManager}）</li>
 *   <li>引用数据缓存管理器：（{@link RefDataCacheManager}）</li>
 * </ul>
 */
@Configuration
@ConditionalOnClass({DictDataCacheManager.class, RefDataCacheManager.class})
@EnableConfigurationProperties(WeaveProperties.class)
@Import({CacheOperationConfiguration.class, SerializerConfiguration.class})
public class CacheConfiguration {

    private final WeaveProperties weaveProperties;

    public CacheConfiguration(WeaveProperties weaveProperties) {
        this.weaveProperties = weaveProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({Serializer.class, DictDataCacheOperation.class})
    public DictDataCache dictDataCache(Serializer serializer, DictDataCacheOperation dictDataCacheOperation) {
        DictDataCacheManager dictDataCacheManager = new DictDataCacheManager(serializer, dictDataCacheOperation);
        if (weaveProperties.getCache().getDictStorageKey() != null) {
            dictDataCacheManager.setDictStorageKey(weaveProperties.getCache().getDictStorageKey());
        }
        return dictDataCacheManager;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({Serializer.class, RefDataCacheOperation.class})
    public RefDataCache refDataCache(Serializer serializer, RefDataCacheOperation refDataCacheOperation) {
        RefDataCacheManager refDataCacheManager = new RefDataCacheManager(serializer, refDataCacheOperation);
        if (weaveProperties.getCache().getRefStoragePrefix() != null) {
            refDataCacheManager.setPrefix(weaveProperties.getCache().getRefStoragePrefix());
        }
        if (weaveProperties.getCache().getRefGlobalTtl() != null) {
            refDataCacheManager.setGlobalTtl(weaveProperties.getCache().getRefGlobalTtl());
        }
        if (weaveProperties.getCache().getRefTableTtl() != null) {
            weaveProperties.getCache().getRefTableTtl().forEach(refDataCacheManager::setTableTtl);
        }
        return refDataCacheManager;
    }

}
