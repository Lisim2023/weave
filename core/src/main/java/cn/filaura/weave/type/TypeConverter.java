package cn.filaura.weave.type;

import cn.filaura.weave.exception.ConvertException;

/**
 * 类型转换器接口，用于POJO对象中属性的类型转换。
 *
 * <p>此接口主要用于将POJO对象的属性值从一种类型转换为另一种类型。
 * <p><strong>不</strong>用于完整POJO对象的转换。
 *
 */
public interface TypeConverter {

    /**
     * 将源对象转换为指定目标类型的实例，主要用于POJO属性的类型转换。
     *
     * <p>此方法设计用于转换单个属性值（如字符串到整数、字符串到日期等），
     * 而不是转换完整的POJO对象。</p>
     *
     * @param <T>        目标类型的类型参数
     * @param source     要转换的源对象，通常为字符串或基本类型的包装对象
     * @param targetType 目标类型的{@link Class}对象，通常为基本类型的包装类或简单类型
     * @return           转换后的目标类型实例，如果源对象为{@code null}则返回{@code null}
     * @throws ConvertException 如果转换失败或目标类型不支持时抛出
     * @throws IllegalArgumentException 如果目标类型为{@code null}时抛出
     */
    <T> T convert(Object source, Class<T> targetType) throws ConvertException;

}
