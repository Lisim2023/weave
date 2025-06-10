package cn.filaura.weave.cache;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 使用jackson完成数据序列化
 *
 * @see Serializer
 */
public class JacksonSerializer implements Serializer {

    private ObjectMapper objectMapper;



    public JacksonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    public JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }



    @Override
    public String serialize(Map<String, ?> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> deSerialize(String string) {
        if (string == null){
            return null;
        }
        try {
            return objectMapper.readValue(string, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e){
            throw new SerializationException(e.getMessage(), e);
        }
    }



    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
