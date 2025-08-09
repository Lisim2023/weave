package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.cache.JacksonSerializer;
import cn.filaura.weave.cache.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson序列化器配置类
 * <p>提供基于Jackson的序列化器，仅在检测到Jackson相关依赖才会激活。
 */
@Configuration
@ConditionalOnClass({JacksonSerializer.class, ObjectMapper.class})
public class SerializerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Serializer serializer(ObjectMapper objectMapper) {
        return new JacksonSerializer(objectMapper);
    }
}
