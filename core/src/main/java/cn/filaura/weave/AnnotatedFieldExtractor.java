package cn.filaura.weave;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotatedFieldExtractor {

    private final Class<? extends Annotation> annotationType;
    private final Map<Class<?>, Field[]> cache = new ConcurrentHashMap<>();



    public AnnotatedFieldExtractor(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }



    public Field[] getAnnotatedFields(Class<?> clazz) {
        Field[] fields = cache.get(clazz);
        if (fields == null) {
            fields = getAllFields(clazz)
                    .stream()
                    .filter(field -> field.getAnnotation(annotationType) != null)
                    .toArray(Field[]::new);
            cache.put(clazz, fields);
        }
        return fields;
    }

    private List<Field> getAllFields(Class<?> clazz){
        List<Field> allFields = new ArrayList<>();
        while(clazz != null && clazz != Object.class){
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return allFields;
    }

}
