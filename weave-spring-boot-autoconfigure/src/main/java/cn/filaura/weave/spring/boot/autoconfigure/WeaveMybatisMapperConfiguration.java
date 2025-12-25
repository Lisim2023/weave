package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.ref.TableRefDataProvider;
import cn.filaura.weave.ref.MybatisTableRefDataFetcher;
import cn.filaura.weave.ref.TableRefMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeaveProperties.class)
@ConditionalOnClass({TableRefMapper.class, MapperFactoryBean.class, SqlSessionFactory.class})
public class WeaveMybatisMapperConfiguration {

    private final WeaveProperties weaveProperties;

    public WeaveMybatisMapperConfiguration(WeaveProperties weaveProperties) {
        this.weaveProperties = weaveProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WeaveProperties.TABLE_REFERENCE_ENABLED, matchIfMissing = true)
    @ConditionalOnBean(SqlSessionFactory.class)
    public MapperFactoryBean<TableRefMapper> dbReferenceMapperFactoryBean(
            SqlSessionFactory sqlSessionFactory) {
        MapperFactoryBean<TableRefMapper> factoryBean = new MapperFactoryBean<>();
        factoryBean.setMapperInterface(TableRefMapper.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(TableRefMapper.class)
    public TableRefDataProvider.DataFetcher tableRefDataFetcher (TableRefMapper mapper) {
        MybatisTableRefDataFetcher mybatisTableRefDataFetcher =
                new MybatisTableRefDataFetcher(mapper);
        if (weaveProperties.getRef().getBatchSize() != null) {
            mybatisTableRefDataFetcher.setBatchSize(weaveProperties.getRef().getBatchSize());
        }
        return mybatisTableRefDataFetcher;
    }

}
