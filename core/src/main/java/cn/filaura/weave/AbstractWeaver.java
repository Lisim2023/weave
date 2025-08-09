package cn.filaura.weave;

import cn.filaura.weave.annotation.Cascade;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractWeaver {

    protected static final AnnotatedFieldExtractor CASCADE_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Cascade.class);

    protected BeanAccessor beanAccessor = new PropertyDescriptorBeanAccessor();



    public Set<Class<?>> gatherClassTypes(Object beans){
        Set<Class<?>> classSet = new HashSet<>();
        recursive(beans, bean -> classSet.add(bean.getClass()));
        return classSet;
    }

    protected void recursive(Object beans, Consumer<Object> nodeProcessor) {
        if (beans == null) {
            return;
        }

        if (beans instanceof Collection) {
            for (Object bean : (Collection<?>)beans) {
                recursive(bean, nodeProcessor);
            }
            return;
        }

        if (beans.getClass().isArray()) {
            int length = Array.getLength(beans);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(beans, i);
                recursive(element, nodeProcessor);
            }
            return;
        }

        nodeProcessor.accept(beans);
        Field[] fields = CASCADE_FIELD_EXTRACTOR.getAnnotatedFields(beans.getClass());
        for (Field field : fields) {
            Object cascade = beanAccessor.getProperty(beans, field.getName());
            recursive(cascade, nodeProcessor);
        }
    }

    protected String getFieldValue(Object bean, String fieldName) {
        Object value = beanAccessor.getProperty(bean, fieldName);
        return value == null ? null : value.toString();
    }

    protected String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }



    public BeanAccessor getBeanAccessor() {
        return beanAccessor;
    }

    public void setBeanAccessor(BeanAccessor beanAccessor) {
        this.beanAccessor = beanAccessor;
    }
}
