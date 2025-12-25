package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.cache.JacksonSerializer;
import cn.filaura.weave.cache.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass({JacksonSerializer.class, ObjectMapper.class})
public class WeaveJacksonSerializerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ObjectMapper.class)
    public Serializer serializer(ObjectMapper objectMapper) {
        return new JacksonSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public Serializer serializerWithDefaultMapper() {
        return new JacksonSerializer(new ObjectMapper());
    }
}
