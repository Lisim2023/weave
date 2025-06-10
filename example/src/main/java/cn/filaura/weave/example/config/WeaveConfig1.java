package cn.filaura.weave.example.config;

import cn.filaura.weave.dict.*;
import cn.filaura.weave.ref.*;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 单数据源配置，直接从数据源接口获取数据
 */
@Configuration
public class WeaveConfig1 {

    @Resource(name = "refDataSource1")
    private RefDataSource refDataSource;

    @Resource(name = "dictDataSourceImpl")
    private DictDataSource dictDataSource;


    @Bean
    public RefHelper refHelper() {
        return new RefHelper(refDataSource);
    }

    @Bean
    public DictHelper dictHelper() {
        return new DictHelper(dictDataSource);
    }

}
