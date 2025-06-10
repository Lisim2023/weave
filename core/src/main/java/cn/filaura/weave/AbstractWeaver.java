package cn.filaura.weave;

import cn.filaura.weave.annotation.Cascade;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractWeaver {

    protected static final AnnotatedFieldExtractor CASCADE_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Cascade.class);

    protected final BeanAccessor beanAccessor;



    public AbstractWeaver(BeanAccessor beanAccessor) {
        this.beanAccessor = beanAccessor;
    }



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

        nodeProcessor.accept(beans);
        Field[] fields = CASCADE_FIELD_EXTRACTOR.getAnnotatedFields(beans.getClass());
        for (Field field : fields) {
            recursive(beanAccessor.getProperty(beans, field.getName()), nodeProcessor);
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

}
