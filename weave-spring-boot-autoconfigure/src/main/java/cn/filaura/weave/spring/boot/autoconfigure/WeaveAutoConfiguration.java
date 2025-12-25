package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.CachedPojoAccessor;
import cn.filaura.weave.PojoAccessor;
import cn.filaura.weave.dict.*;
import cn.filaura.weave.inject.WeaveAspect;
import cn.filaura.weave.inject.WeaveResponseBodyAdvice;
import cn.filaura.weave.inject.WeaveReverseAspect;
import cn.filaura.weave.ref.*;

import cn.filaura.weave.type.DefaultTypeConverter;
import cn.filaura.weave.type.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;



@Configuration
@EnableConfigurationProperties(WeaveProperties.class)
@AutoConfigureOrder(100)
@Import({WeaveMybatisMapperConfiguration.class, WeaveCacheConfiguration.class})
public class WeaveAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WeaveAutoConfiguration.class);

    private final WeaveProperties weaveProperties;

    public WeaveAutoConfiguration(WeaveProperties weaveProperties) {
        this.weaveProperties = weaveProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public PojoAccessor pojoAccessor() {
        return new CachedPojoAccessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeConverter typeConverter() {
        return new DefaultTypeConverter();
    }



    @Bean
    @ConditionalOnProperty(name = WeaveProperties.TABLE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    @ConditionalOnBean({TableRefDataProvider.DataFetcher.class, ColumnProjectionCache.class})
    public TableRefDataProvider cachingDbRefDataProvider(
            TableRefDataProvider.DataFetcher dataFetcher,
            ColumnProjectionCache columnProjectionCache) {
        return new CachingTableRefDataProvider(columnProjectionCache, dataFetcher);
    }

    @Bean
    @ConditionalOnProperty(name = WeaveProperties.TABLE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    @ConditionalOnBean(TableRefDataProvider.DataFetcher.class)
    public TableRefDataProvider dbRefDataProvider(
            TableRefDataProvider.DataFetcher dataFetcher) {
        return new DirectTableRefDataProvider(dataFetcher);
    }

    @Bean
    @ConditionalOnProperty(name = WeaveProperties.TABLE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    @ConditionalOnBean(TableRefDataProvider.class)
    public TableRefHelper tableRefHelper(TableRefDataProvider tableRefDataProvider,
                                         PojoAccessor pojoAccessor,
                                         TypeConverter typeConverter) {
        TableRefHelper tableRefHelper = new TableRefHelper(tableRefDataProvider);
        tableRefHelper.setPojoAccessor(pojoAccessor);
        tableRefHelper.setTypeConverter(typeConverter);
        if (weaveProperties.getRef().getGlobalPrimaryKey() != null) {
            AbstractReferenceWeaver.setGlobalPrimaryKey(
                    weaveProperties.getRef().getGlobalPrimaryKey());
        }
        return tableRefHelper;
    }



    @Bean
    @ConditionalOnProperty(name = WeaveProperties.SERVICE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    public ResultExtractor resultExtractor() {
        return result -> result;
    }

    @Bean
    @ConditionalOnProperty(name = WeaveProperties.SERVICE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    public ServiceRefDataProvider.DataFetcher serviceReferenceDataFetcher(
            ApplicationContext context,
            ResultExtractor resultExtractor) {
        SpringBeanMethodInvoker methodInvoker = new SpringBeanMethodInvoker(context);
        methodInvoker.setResultExtractor(resultExtractor);
        ServiceRefDataFetcher fetcher = new ServiceRefDataFetcher(methodInvoker);
        if (weaveProperties.getRef().getBatchSize() != null) {
            fetcher.setBatchSize(weaveProperties.getRef().getBatchSize());
        }
        return fetcher;
    }

    @Bean
    @ConditionalOnProperty(name = WeaveProperties.SERVICE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    @ConditionalOnBean({ServiceRefDataProvider.DataFetcher.class, RecordCache.class})
    public ServiceRefDataProvider cachingServiceRefDataProvider(
            ServiceRefDataProvider.DataFetcher fetcher, RecordCache cache) {
        return new CachingServiceRefDataProvider(cache, fetcher);
    }

    @Bean
    @ConditionalOnProperty(name = WeaveProperties.SERVICE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    @ConditionalOnBean(ServiceRefDataProvider.DataFetcher.class)
    public ServiceRefDataProvider serviceRefDataProvider(
            ServiceRefDataProvider.DataFetcher fetcher) {
        return new DirectServiceRefDataProvider(fetcher);
    }

    @Bean
    @ConditionalOnProperty(name = WeaveProperties.SERVICE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnMissingBean
    @ConditionalOnBean(ServiceRefDataProvider.class)
    public ServiceRefHelper serviceRefHelper(ServiceRefDataProvider serviceRefDataProvider,
                                             PojoAccessor pojoAccessor,
                                             TypeConverter typeConverter) {
        ServiceRefHelper serviceRefHelper = new ServiceRefHelper(serviceRefDataProvider);
        serviceRefHelper.setPojoAccessor(pojoAccessor);
        serviceRefHelper.setTypeConverter(typeConverter);
        WeaveProperties.Ref ref = weaveProperties.getRef();
        if (ref.getGlobalPrimaryKey() != null) {
            AbstractReferenceWeaver.setGlobalPrimaryKey(ref.getGlobalPrimaryKey());
        }
        if (ref.getGlobalForeignKeySuffix() != null) {
            AbstractReferenceWeaver.setGlobalForeignKeySuffix(ref.getGlobalForeignKeySuffix());
        }
        if (ref.getGlobalMethodName() != null) {
            AbstractReferenceWeaver.setGlobalMethodName(ref.getGlobalMethodName());
        }
        return serviceRefHelper;
    }



    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DictDataProvider.DataFetcher.class)
    public DictCache localMemoryDictCache() {
        return new LocalMemoryDictCache();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DictDataProvider.DataFetcher.class, DictCache.class})
    public DictDataProvider dictProvider(DictDataProvider.DataFetcher dataFetcher,
                                         DictCache dictCache) {
        return new CachingDictDataProvider(dictCache, dataFetcher);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DictDataProvider.class)
    public DictHelper dictHelper(DictDataProvider dictDataProvider,
                                 PojoAccessor pojoAccessor,
                                 TypeConverter typeConverter) {
        DictHelper dictHelper = new DictHelper(dictDataProvider);
        dictHelper.setPojoAccessor(pojoAccessor);
        dictHelper.setTypeConverter(typeConverter);
        if (weaveProperties.getDict().getDelimiter() != null) {
            AbstractDictWeaver.setDelimiter(weaveProperties.getDict().getDelimiter());
        }
        if (weaveProperties.getDict().getTextFieldSuffix() != null) {
            AbstractDictWeaver.setTextFieldSuffix(weaveProperties.getDict().getTextFieldSuffix());
        }
        return dictHelper;
    }



    @Bean
    @ConditionalOnClass(name =
            "org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice")
    @ConditionalOnProperty(name = WeaveProperties.RESPONSE_BODY_ADVICE_ENABLED,
            matchIfMissing = true)
    @Conditional(OnAnyHelperExistsCondition.class)
    public WeaveResponseBodyAdvice weaveResponseBodyAdvice() {
        log.info("Creating WeaveResponseBodyAdvice - data weaving is now active");
        return new WeaveResponseBodyAdvice();
    }

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    @ConditionalOnProperty(name = WeaveProperties.ASPECT_ENABLED, matchIfMissing = true)
    @Conditional(OnAnyHelperExistsCondition.class)
    public WeaveAspect weaveAspect() {
        log.info("Creating WeaveAspect - data weaving is now active");
        return new WeaveAspect();
    }

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    @ConditionalOnProperty(name = WeaveProperties.REVERSE_ASPECT_ENABLED, matchIfMissing = true)
    @ConditionalOnBean(DictHelper.class)
    public WeaveReverseAspect weaveReverseAspect() {
        log.info("Creating WeaveReverseAspect - data reverse weaving is now active");
        return new WeaveReverseAspect();
    }

    // 组合条件类
    static class OnAnyHelperExistsCondition extends AnyNestedCondition {
        OnAnyHelperExistsCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(DictHelper.class)
        static class OnDictHelperExists {}

        @ConditionalOnBean(TableRefHelper.class)
        static class OnTableRefHelperExists {}

        @ConditionalOnBean(ServiceRefHelper.class)
        static class OnServiceRefHelperExists {}
    }

}
