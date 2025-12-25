package cn.filaura.weave.type;

import cn.filaura.weave.exception.ConvertException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DefaultTypeConverter implements TypeConverter {

    private static final Map<Class<?>, Converter<?>> CONVERTERS = new HashMap<>();


    public DefaultTypeConverter() {
        register(String.class, s -> s);
        register(Integer.class, Integer::valueOf);
        register(Long.class, Long::valueOf);
        register(Short.class, Short::valueOf);
        register(Float.class, Float::valueOf);
        register(Double.class, Double::valueOf);
        register(Boolean.class, BooleanConverter::convert);
        register(Date.class, new DateConverter("yyyy-MM-dd'T'HH:mm:ss"));
    }

    /**
     * 为指定类型注册一个转换器。
     *
     * @param targetType 目标类型，即要注册转换器的类型
     * @param converter  转换函数，用于将字符串转换为目标类型
     */
    public <T> void register(Class<T> targetType, Converter<T> converter) throws IllegalArgumentException {
        if (targetType == null || converter == null) {
            throw new IllegalArgumentException("Target type and converter cannot be null");
        }
        CONVERTERS.put(targetType, converter);
    }

    /**
     * 移除指定类型的转换器。
     *
     * @param type 要移除转换器的目标类型
     */
    public void remove(Class<?> type) {
        if (type != null) {
            CONVERTERS.remove(type);
        }
    }

    /**
     * 字符串转指定类型
     * @param source      源字符串
     * @param targetType  目标类型Class对象
     * @return            转换后的对象
     * @throws ConvertException 如果没有为该目标类型注册转换器或转换失败时抛出此异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(Object source, Class<T> targetType) throws ConvertException {
        if (source == null) return null;
        if (targetType == null) {
            throw new ConvertException("target type cannot be null");
        }
        if (source.getClass().equals(targetType)) {
            return (T) source;
        }

        Converter<T> converter = getConverter(targetType);
        if (converter == null) {
            throw new ConvertException("Unsupported conversion type: " + targetType.getName());
        }
        try {
            return converter.convert(source.toString());
        } catch (Exception e) {
            throw new ConvertException("Conversion failed for type: " + targetType.getName(), e);
        }
    }

    /**
     * 获取指定类型的转换器
     * @param targetType 目标类型Class对象
     * @return 转换器
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<T> getConverter(Class<T> targetType) {
        Converter<?> converter = CONVERTERS.get(targetType);
        return converter == null ? null : (Converter<T>) converter;
    }

}
