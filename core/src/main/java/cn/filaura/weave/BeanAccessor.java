package cn.filaura.weave;



import cn.filaura.weave.exception.BeanAccessException;

/**
 * Bean属性访问器接口
 */
public interface BeanAccessor {


    /**
     * 获取对象的属性值
     *
     * @param bean 目标对象
     * @param name 要获取的属性名
     * @param mode 获取模式，控制获取属性时的行为（见 {@link GetMode}）
     * @return 属性值
     * @throws BeanAccessException 当读取属性失败时抛出
     */
    Object getProperty(Object bean, String name, GetMode mode) throws BeanAccessException;

    default Object getProperty(Object bean, String name) throws BeanAccessException {
        return getProperty(bean, name, GetMode.PRESERVE_NULL);
    }

    /**
     * 设置对象的属性值
     * @param bean 目标对象
     * @param name 属性名
     * @param value 属性值
     * @param mode 设置模式，控制设置属性时的行为（见 {@link SetMode}）
     * @throws BeanAccessException 当设置属性失败时抛出
     */
    void setProperty(Object bean, String name, String value, SetMode mode) throws BeanAccessException;

    default void setProperty(Object bean, String name, String value) throws BeanAccessException {
        setProperty(bean, name, value, SetMode.ENFORCE_EXISTING);
    }

}
