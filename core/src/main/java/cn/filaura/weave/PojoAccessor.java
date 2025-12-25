package cn.filaura.weave;



import cn.filaura.weave.exception.PojoAccessException;

/**
 * 属性访问器接口，用于操作Java对象的属性
 * <p>提供对JavaBean属性的动态访问能力。
 * <p>访问失败时统一拋出 {@code PojoAccessException} 。
 */
public interface PojoAccessor {

    /**
     * 获取指定Bean的属性值
     *
     * @param pojo 目标JavaBean对象，不能为null
     * @param name 属性名称，不能为空
     * @return 属性的当前值
     * @throws PojoAccessException 当属性不存在、访问失败或参数非法时抛出
     */
    Object getPropertyValue(Object pojo, String name) throws PojoAccessException;

    /**
     * 获取指定属性的类型
     *
     * @param pojoClass 目标类，不能为null
     * @param name 属性名称，不能为空
     * @return 属性的Java类型
     * @throws PojoAccessException 当属性不存在或访问失败时抛出
     */
    Class<?> getPropertyType(Class<?> pojoClass, String name) throws PojoAccessException;

    /**
     * 设置属性值（属性必须存在）
     *
     * @param pojo 目标对象
     * @param name 属性名
     * @param value 属性值
     * @throws PojoAccessException 如果属性不存在或设置失败
     */
    void setPropertyValue(Object pojo, String name, Object value) throws PojoAccessException;

}
