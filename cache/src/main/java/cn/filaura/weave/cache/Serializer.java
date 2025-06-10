package cn.filaura.weave.cache;



import java.util.Map;



/**
 * 序列化接口
 *
 * @see JacksonSerializer
 */
public interface Serializer {

    /**
     * 将Map类型数据序列化为字符串
     *
     * @param data 待序列化的Map对象
     * @return 序列化后的字符串
     * @throws SerializationException 序列化过程中出现异常时，封装并抛出此异常，包含错误详情信息
     */
    String serialize(Map<String, ?> data) throws SerializationException;

    /**
     * 将字符串反序列化为Map类型
     *
     * @param data 待反序列化的字符串
     * @return 反序列化后的Map对象
     * @throws SerializationException 序列化过程中出现异常时，封装并抛出此异常，包含错误详情信息
     */
    Map<String, String> deSerialize(String data) throws SerializationException;
}
