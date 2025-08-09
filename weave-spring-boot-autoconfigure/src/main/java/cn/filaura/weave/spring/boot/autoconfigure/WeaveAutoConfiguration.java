package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.PropertyDescriptorBeanAccessor;
import cn.filaura.weave.dict.*;
import cn.filaura.weave.ref.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;


/**
 * Weave自动配置类
 * <p>
 * 负责Weave框架所有核心组件的自动装配，主要包括：
 * <ul>
 *   <li>字典助手/引用助手（{@link DictHelper}/{@link RefHelper}）</li>
 *   <li>数据关联切面（{@link WeaveAspect}）</li>
 *   <li>逆向数据关联切面（{@link WeaveReverseAspect}）</li>
 * </ul>
 *
 */
@Configuration
@EnableConfigurationProperties(WeaveProperties.class)
@Import(CacheConfiguration.class)
public class WeaveAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WeaveAutoConfiguration.class);

    private final WeaveProperties weaveProperties;

    public WeaveAutoConfiguration(WeaveProperties weaveProperties) {
        this.weaveProperties = weaveProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanAccessor propertyDescriptorBeanAccessor() {
        return new PropertyDescriptorBeanAccessor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RefDataSource.class, RefDataCache.class})
    public RefDataProvider cacheFirstRefDataProvider(RefDataSource refDataSource, RefDataCache refDataCache) {
        return new CacheFirstRefDataProvider(refDataSource, refDataCache);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RefDataSource.class)
    public RefDataProvider directDataSourceRefDataProvider(RefDataSource refDataSource) {
        return new DirectDataSourceRefDataProvider(refDataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RefDataProvider.class)
    public RefHelper refHelper(RefDataProvider refDataProvider, BeanAccessor beanAccessor) {
        RefHelper refHelper = new RefHelper(refDataProvider);
        if (weaveProperties.getRef().getNullDisplayText() != null) {
            refHelper.setNullDisplayText(weaveProperties.getRef().getNullDisplayText());
        }
        if (weaveProperties.getRef().getGlobalPrimaryKey() != null) {
            refHelper.setGlobalPrimaryKey(weaveProperties.getRef().getGlobalPrimaryKey());
        }
        refHelper.setBeanAccessor(beanAccessor);
        return refHelper;
    }



    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DictDataSource.class, DictDataCache.class})
    public DictDataProvider cacheFirstDictDataProvider(DictDataSource dictDataSource, DictDataCache dictDataCache) {
        return new CacheFirstDictDataProvider(dictDataSource, dictDataCache);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DictDataSource.class)
    public DictDataProvider directDataSourceDictDataProvider(DictDataSource dictDataSource) {
        return new DirectDataSourceDictDataProvider(dictDataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DictDataProvider.class)
    public DictHelper dictHelper(DictDataProvider dictDataProvider, BeanAccessor beanAccessor) {
        DictHelper dictHelper = new DictHelper(dictDataProvider);
        if (weaveProperties.getDict().getFieldNameSuffix() != null) {
            dictHelper.setFieldNameSuffix(weaveProperties.getDict().getFieldNameSuffix());
        }
        if (weaveProperties.getDict().getDelimiter() != null) {
            dictHelper.setDelimiter(weaveProperties.getDict().getDelimiter());
        }
        dictHelper.setBeanAccessor(beanAccessor);
        return dictHelper;
    }

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    @ConditionalOnProperty(name = WeaveProperties.WEAVE_PREFIX + ".disable-weave-aspect",
            havingValue = "false", matchIfMissing = true)
    @Conditional(OnDictHelperOrRefHelperCondition.class)
    public WeaveAspect weaveAspect() {
        log.info("Creating WeaveAspect - data weaving is now active");
        return new WeaveAspect();
    }

    // 组合条件类
    static class OnDictHelperOrRefHelperCondition extends AnyNestedCondition {
        OnDictHelperOrRefHelperCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(DictHelper.class)
        static class OnDictHelperExists {}

        @ConditionalOnBean(RefHelper.class)
        static class OnRefHelperExists {}
    }

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    @ConditionalOnProperty(name = WeaveProperties.WEAVE_PREFIX + ".disable-weave-reverse-aspect",
            havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(DictHelper.class)
    public WeaveReverseAspect weaveReverseAspect() {
        log.info("Creating WeaveReverseAspect - data reverse weaving is now active");
        return new WeaveReverseAspect();
    }

}
