package cn.filaura.weave;

import cn.filaura.weave.annotation.Cascade;
import cn.filaura.weave.exception.PojoAccessException;
import cn.filaura.weave.type.DefaultTypeConverter;
import cn.filaura.weave.type.TypeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractWeaver {

    protected static final AnnotatedFieldExtractor CASCADE_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Cascade.class);

    protected PojoAccessor pojoAccessor = new CachedPojoAccessor();
    protected TypeConverter typeConverter = new DefaultTypeConverter();

    public Set<Class<?>> gatherClassTypes(Object pojos){
        Set<Class<?>> classSet = new HashSet<>();
        recursive(pojos, bean -> classSet.add(bean.getClass()));
        return classSet;
    }

    protected void recursive(Object pojos, Consumer<Object> nodeProcessor) {
        if (pojos == null) {
            return;
        }

        if (pojos instanceof Collection) {
            for (Object pojo : (Collection<?>)pojos) {
                recursive(pojo, nodeProcessor);
            }
            return;
        }

        if (pojos.getClass().isArray()) {
            int length = Array.getLength(pojos);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(pojos, i);
                recursive(element, nodeProcessor);
            }
            return;
        }

        nodeProcessor.accept(pojos);
        Field[] fields = CASCADE_FIELD_EXTRACTOR.getAnnotatedFields(pojos.getClass());
        for (Field field : fields) {
            Object cascade = pojoAccessor.getPropertyValue(pojos, field.getName());
            recursive(cascade, nodeProcessor);
        }
    }

    /**
     * 直接设置属性值，不进行类型转换。
     * 若属性不存在则尝试动态扩展。
     */
    protected void writeRawProperty(Object pojo, String name, Object value) {
        doWriteProperty(pojo, name, value, false);
    }

    /**
     * 设置属性值，值会根据目标属性类型自动转换。
     * 若属性不存在则尝试动态扩展。
     */
    protected void writeConvertedProperty(Object pojo, String name, Object value) {
        doWriteProperty(pojo, name, value, true);
    }

    private void doWriteProperty(Object pojo, String name, Object value, boolean autoConvert) {
        try {
            if (autoConvert) {
                Class<?> targetType = pojoAccessor.getPropertyType(pojo.getClass(), name);
                if (!targetType.isAssignableFrom(value.getClass())) {
                    value = typeConverter.convert(value, targetType);
                }
            }
            pojoAccessor.setPropertyValue(pojo, name, value);
        } catch (PojoAccessException e) {
            if (pojo instanceof PropertyExtensible) {
                ((PropertyExtensible) pojo).extendProperty(name, value);
            } else {
                throw e;
            }
        }
    }

    protected String[] toStringArray(Object fieldValue) {
        if (fieldValue == null) return new String[0];
        if (fieldValue instanceof Collection) {
            return ((Collection<?>) fieldValue).stream()
                    .map(String::valueOf)
                    .toArray(String[]::new);

        } else if (fieldValue.getClass().isArray()) {
            int length = Array.getLength(fieldValue);
            String[] array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = String.valueOf(Array.get(fieldValue, i));
            }
            return array;
        }
        throw new IllegalArgumentException("Unsupported field value type for conversion to String[]: "
                + fieldValue.getClass().getName());
    }

    protected Collection<?> convertStreamToCollection(Stream<?> stream, Class<?> targetType) {
        if (Set.class.isAssignableFrom(targetType)) {
            return stream.collect(Collectors.toSet());
        }
        if (List.class.isAssignableFrom(targetType)) {
            return stream.collect(Collectors.toList());
        }
        if (Collection.class.isAssignableFrom(targetType)) {
            return stream.collect(Collectors.toList());
        }

        throw new IllegalArgumentException(
                String.format("Unsupported target type: %s. Supported types: Set, List, Collection",
                        targetType.getName()));
    }



    public PojoAccessor getPojoAccessor() {
        return pojoAccessor;
    }

    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        this.pojoAccessor = pojoAccessor;
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

}
