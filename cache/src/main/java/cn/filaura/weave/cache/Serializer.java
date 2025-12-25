package cn.filaura.weave.cache;


import java.util.Map;


/**
 * 序列化接口
 *
 * @see JacksonSerializer
 */
public interface Serializer {

    /**
     * 将数据序列化为字符串
     *
     * @param data 待序列化的Map对象
     * @return 序列化后的字符串
     * @throws SerializationException 序列化过程中出现异常时，封装并抛出此异常，包含错误详情信息
     */
    String serialize(Object data) throws SerializationException;

    /**
     * 将字符串反序列化为Map类型
     *
     * @param data 待反序列化的字符串
     * @return 反序列化后的Map对象
     * @throws SerializationException 序列化过程中出现异常时，封装并抛出此异常，包含错误详情信息
     */
    Map<String, Object> deSerialize(String data) throws SerializationException;

    /**
     * 将字符串反序列化为指定的类型
     * @param data 待反序列化的字符串
     * @param tClass 目标类型
     * @return 反序列化后的数据对象
     * @param <T> 类型泛型
     * @throws SerializationException 序列化过程中出现异常时，封装并抛出此异常
     */
    <T> T deSerialize(String data, Class<T> tClass) throws SerializationException;

}
