package cn.filaura.weave;

import cn.filaura.weave.exception.PojoAccessException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedPojoAccessor implements PojoAccessor {

    // 缓存类属性描述符
    private final ConcurrentMap<Class<?>, ConcurrentMap<String, PropertyDescriptor>> classCache
            = new ConcurrentHashMap<>();

    // 统一的空属性描述符
    private static final PropertyDescriptor NULL_DESCRIPTOR = createNullDescriptor();


    @Override
    public Object getPropertyValue(Object pojo, String name) throws PojoAccessException {
        if (pojo == null) {
            throw new PojoAccessException("Pojo object cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new PojoAccessException("Property name cannot be null or empty");
        }

        try {
            PropertyDescriptor pd = getPropertyDescriptor(pojo.getClass(), name);
            if (pd == NULL_DESCRIPTOR) {
                throw new PojoAccessException("Property '" + name + "' does not exist in class "
                        + pojo.getClass().getName());
            }

            Method readMethod = pd.getReadMethod();
            if (readMethod == null) {
                throw new PojoAccessException("Property '" + name + "' is not readable in class "
                        + pojo.getClass().getName());
            }

            return readMethod.invoke(pojo);
        } catch (PojoAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new PojoAccessException("Failed to get property '" + name + "' value", e);
        }
    }

    @Override
    public Class<?> getPropertyType(Class<?> pojoClass, String name) throws PojoAccessException {
        if (pojoClass == null) {
            throw new PojoAccessException("Pojo class cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new PojoAccessException("Property name cannot be null or empty");
        }

        PropertyDescriptor pd = getPropertyDescriptor(pojoClass, name);
        if (pd == NULL_DESCRIPTOR) {
            throw new PojoAccessException("Property '" + name + "' does not exist in class "
                    + pojoClass.getName());
        }

        return pd.getPropertyType();
    }

    @Override
    public void setPropertyValue(Object pojo, String name, Object value)
            throws PojoAccessException {

        if (pojo == null) {
            throw new PojoAccessException("Pojo object cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new PojoAccessException("Property name cannot be null or empty");
        }

        try {
            PropertyDescriptor pd = getPropertyDescriptor(pojo.getClass(), name);
            if (pd == NULL_DESCRIPTOR) {
                throw new PojoAccessException("Property '" + name + "' does not exist in class "
                        + pojo.getClass().getName());
            }

            Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                throw new PojoAccessException("Property '" + name + "' is not writable in class "
                        + pojo.getClass().getName());
            }

            writeMethod.invoke(pojo, value);
        } catch (PojoAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new PojoAccessException("Failed to set property '" + name + "' value", e);
        }
    }

    /**
     * 获取属性描述符，如果不存在则缓存NULL_DESCRIPTOR
     */
    private PropertyDescriptor getPropertyDescriptor(Class<?> pojoClass, String name) {
        // 获取类的属性缓存
        ConcurrentMap<String, PropertyDescriptor> propertyCache = classCache.computeIfAbsent(
                pojoClass, k -> new ConcurrentHashMap<>());

        // 先从缓存中查找
        return propertyCache.computeIfAbsent(name, k -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(pojoClass);
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

                for (PropertyDescriptor pd : pds) {
                    if (name.equals(pd.getName())) {
                        return pd;
                    }
                }

                // 属性不存在，缓存空描述符
                return NULL_DESCRIPTOR;
            } catch (Exception e) {
                // 发生异常时也缓存空描述符
                return NULL_DESCRIPTOR;
            }
        });
    }

    /**
     * 创建统一的空属性描述符
     */
    public static PropertyDescriptor createNullDescriptor() {
        // 创建一个特殊的属性描述符，用于表示不存在的属性
        try {
            return new PropertyDescriptor("propertyName",
                    AbsentPropertyDescriptor.class);
        } catch (IntrospectionException e) {
            throw new PojoAccessException(e);
        }
    }

    /**
     * 用于创建空的属性描述符
     */
    private static class AbsentPropertyDescriptor {
        private String propertyName;

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        classCache.clear();
    }

    /**
     * 获取缓存统计信息
     */
    public CacheStats getCacheStats() {
        int classCount = classCache.size();
        int totalProperties = classCache.values().stream()
                .mapToInt(ConcurrentMap::size)
                .sum();

        return new CacheStats(classCount, totalProperties);
    }

    /**
     * 缓存统计信息类
     */
    public static class CacheStats {
        private final int cachedClassCount;
        private final int totalCachedProperties;

        public CacheStats(int cachedClassCount, int totalCachedProperties) {
            this.cachedClassCount = cachedClassCount;
            this.totalCachedProperties = totalCachedProperties;
        }

        public int getCachedClassCount() {
            return cachedClassCount;
        }

        public int getTotalCachedProperties() {
            return totalCachedProperties;
        }

        @Override
        public String toString() {
            return String.format("CacheStats{classCount=%d, propertyCount=%d}",
                    cachedClassCount, totalCachedProperties);
        }
    }
}
