package cn.filaura.weave;

import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.type.ConvertUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用属性描述符完成对象属性操作
 *
 * @see BeanAccessor
 */
public class PropertyDescriptorBeanAccessor implements BeanAccessor {

    private final Map<Class<?>, Map<String, PropertyDescriptor>> propertyDescriptorCache = new ConcurrentHashMap<>();



    @Override
    public Object getProperty(Object bean, String name, GetMode mode) throws BeanAccessException {
        if (bean == null) {
            throw new BeanAccessException("Bean cannot be null");
        }

        Class<?> beanClass = bean.getClass();
        try {
            PropertyDescriptor pd = getPropertyDescriptor(beanClass, name);
            if (pd == null || pd.getReadMethod() == null) {
                throw new BeanAccessException("No readable property '" + name + "' in class " + beanClass.getName());
            }

            Method getter = pd.getReadMethod();
            Object result = getter.invoke(bean);
            if (GetMode.PRESERVE_NULL.equals(mode) || result != null) {
                return result;
            }

            Method setter = pd.getWriteMethod();
            if (setter == null) {
                throw new BeanAccessException("No writable property '" + name + "' in class " + beanClass.getName());
            }

            try {
                Class<?> parameterType = setter.getParameterTypes()[0];
                result = parameterType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new BeanAccessException("Cannot initialize property '" + name + "' in class " + beanClass.getName(), e);
            }

            setter.invoke(bean, result);
            return result;
        } catch (Exception e) {
            throw new BeanAccessException("Error reading property '" + name + "' in class " + beanClass.getName(), e);
        }
    }

    @Override
    public void setProperty(Object bean, String name, String value, SetMode mode) throws BeanAccessException {
        if (bean == null) {
            throw new BeanAccessException("Bean cannot be null");
        }

        Class<?> beanClass = bean.getClass();
        try {
            PropertyDescriptor pd = getPropertyDescriptor(beanClass, name);
            if (pd == null) {
                if (SetMode.ENFORCE_EXISTING.equals(mode)) {
                    throw new BeanAccessException("Property '" + name + "' not found in class " + beanClass.getName());
                }
                return;
            }

            Method setter = pd.getWriteMethod();
            if (setter == null) {
                throw new BeanAccessException("No writable property '" + name + "' in class " + beanClass.getName());
            }

            Class<?> parameterType = setter.getParameterTypes()[0];
            Object converted = ConvertUtil.convert(value, parameterType);
            setter.invoke(bean, converted);
        } catch (Exception e) {
            throw new BeanAccessException("Error setting property '" + name + "' in class " + beanClass.getName(), e);
        }
    }

    private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String name) {
        Map<String, PropertyDescriptor> descriptorMap =
                propertyDescriptorCache.computeIfAbsent(beanClass, clazz -> new ConcurrentHashMap<>());
        PropertyDescriptor pd = descriptorMap.get(name);
        if (pd != null) {
            return pd;
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            for (PropertyDescriptor candidate : beanInfo.getPropertyDescriptors()) {
                if (candidate.getName().equals(name)) {
                    pd = candidate;
                    descriptorMap.put(name, pd);
                    return pd;
                }
            }
        } catch (IntrospectionException e) {
            throw new BeanAccessException("Introspection failed for: " + beanClass, e);
        }

        return null;
    }

}
