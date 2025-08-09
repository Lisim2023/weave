package cn.filaura.weave;



import cn.filaura.weave.exception.BeanAccessException;

/**
 * 属性访问器接口，用于操作Java对象的属性
 * <p>提供对JavaBean属性的动态访问能力。
 * <p>访问失败时统一拋出 {@code BeanAccessException} 。
 */
public interface BeanAccessor {

    /**
     * 获取指定Bean的属性值
     *
     * @param bean 目标JavaBean对象，不能为null
     * @param name 属性名称，不能为空
     * @return 属性的当前值
     * @throws BeanAccessException 当属性不存在、访问失败或参数非法时抛出
     */
    Object getProperty(Object bean, String name) throws BeanAccessException;

    /**
     * 获取指定属性的类型
     *
     * @param bean 目标JavaBean对象，不能为null
     * @param name 属性名称，不能为空
     * @return 属性的Java类型
     * @throws BeanAccessException 当属性不存在或访问失败时抛出
     */
    Class<?> getPropertyType(Object bean, String name) throws BeanAccessException;

    /**
     * 当属性类型为集合时，获取集合的泛型类型
     * @param bean 目标JavaBean对象，不能为null
     * @param name 属性名称，不能为空
     * @return 泛型类型
     * @throws BeanAccessException 当属性不存在或访问失败时抛出
     */
    Class<?> getCollectionGenericType(Object bean, String name) throws BeanAccessException;

    /**
     * 设置属性值（属性必须存在）
     *
     * @param bean 目标对象
     * @param name 属性名
     * @param value 属性值
     * @throws BeanAccessException 如果属性不存在或设置失败
     */
    void setProperty(Object bean, String name, Object value) throws BeanAccessException;

}
