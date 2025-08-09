package cn.filaura.weave;

import cn.filaura.weave.exception.BeanAccessException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于PropertyDescriptor的属性访问器实现
 *
 * <p>自动缓存所有使用过的PropertyDescriptor，提高访问性能</p>
 */
public class PropertyDescriptorBeanAccessor implements BeanAccessor {

    // 双级缓存结构：Class -> (属性名 -> PropertyDescriptor)
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> descriptorCache =
            new ConcurrentHashMap<>();



    @Override
    public Object getProperty(Object bean, String name) throws BeanAccessException {
        Method getter = getReadMethodOrThrow(bean, name);
        try {
            return getter.invoke(bean);
        } catch (Exception e) {
            throw new BeanAccessException("Error reading property '"
                    + name + "' in class " + bean.getClass().getName(), e);
        }
    }

    @Override
    public Class<?> getPropertyType(Object bean, String name) throws BeanAccessException {
        PropertyDescriptor pd = getPropertyDescriptorOrThrow(bean, name);
        return pd.getPropertyType();
    }

    @Override
    public Class<?> getCollectionGenericType(Object bean, String name) throws BeanAccessException {
        Method setter = getWriteMethodOrThrow(bean, name);
        try {
            ParameterizedType genericParameterType = (ParameterizedType) setter.getGenericParameterTypes()[0];
            return (Class<?>) genericParameterType.getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new BeanAccessException("Failed to resolve generic type for collection property: '"
                    + name + "' in class " + bean.getClass().getName(), e);
        }
    }

    @Override
    public void setProperty(Object bean, String name, Object value) throws BeanAccessException {
        Method setter = getWriteMethodOrThrow(bean, name);
        try {
            setter.invoke(bean, value);
        } catch (Exception e) {
            throw new BeanAccessException("Error setting property '"
                    + name + "' in class " + bean.getClass().getName(), e);
        }
    }

    private Method getReadMethodOrThrow(Object bean, String propertyName) {
        PropertyDescriptor pd = getPropertyDescriptorOrThrow(bean, propertyName);
        Method getter = pd.getReadMethod();
        if (getter == null) {
            throw new BeanAccessException("No readable property '"
                    + propertyName + "' in class " + bean.getClass().getName());
        }
        return getter;
    }

    private Method getWriteMethodOrThrow(Object bean, String propertyName) {
        PropertyDescriptor pd = getPropertyDescriptorOrThrow(bean, propertyName);
        Method setter = pd.getWriteMethod();
        if (setter == null) {
            throw new BeanAccessException("No writable property '"
                    + propertyName + "' in class " + bean.getClass().getName());
        }
        return setter;
    }

    private PropertyDescriptor getPropertyDescriptorOrThrow(Object bean, String propertyName) {
        if (bean == null) {
            throw new BeanAccessException("Bean cannot be null");
        }

        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) {
            throw new BeanAccessException("Property '"
                    + propertyName + "' not found in class " + bean.getClass().getName());
        }
        return pd;
    }

    private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName)
            throws BeanAccessException {
        Map<String, PropertyDescriptor> descriptorMap =
                descriptorCache.computeIfAbsent(beanClass, clazz -> new ConcurrentHashMap<>());
        PropertyDescriptor pd = descriptorMap.get(propertyName);
        if (pd != null) {
            return pd;
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            for (PropertyDescriptor candidate : beanInfo.getPropertyDescriptors()) {
                if (candidate.getName().equals(propertyName)) {
                    pd = candidate;
                    descriptorMap.put(propertyName, pd);
                    return pd;
                }
            }
        } catch (IntrospectionException e) {
            throw new BeanAccessException("Introspection failed for: " + beanClass, e);
        }

        return null;
    }

}
