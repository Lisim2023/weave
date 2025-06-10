package cn.filaura.weave.type;

import cn.filaura.weave.exception.ConvertException;

/**
 * 类型转换器接口
 * 将String类型的数据转换为类型T指定的目标类型
 * @param <T> 目标类型
 */
@FunctionalInterface
public interface Converter<T> {

    /**
     * 将字符串转换为T类型
     *
     * @param source 待转换的字符串
     * @return 转换后的对象
     */
    T convert(String source) throws ConvertException;
}
