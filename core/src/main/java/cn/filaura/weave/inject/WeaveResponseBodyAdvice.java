package cn.filaura.weave.inject;


import cn.filaura.weave.dict.DictHelper;
import cn.filaura.weave.exception.PojoAccessException;
import cn.filaura.weave.ref.ServiceRefHelper;
import cn.filaura.weave.ref.TableRefHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@ControllerAdvice
public class WeaveResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired(required = false)
    private TableRefHelper tableRefHelper;

    @Autowired(required = false)
    private ServiceRefHelper serviceRefHelper;

    @Autowired(required = false)
    private DictHelper dictHelper;

    private final PropertyGetter propertyGetter = new PropertyGetter();

    private final List<String> wrapped = new ArrayList<>();

    private final List<String> targets = new ArrayList<>();

    public WeaveResponseBodyAdvice() {
        // 统一封装结果对象
        wrapped.add("data");
        wrapped.add("result");
        wrapped.add("results");

        // mybatis-plus分页对象
        targets.add("records");
        // PageHelper分页对象
        targets.add("list");
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getContainingClass().isAnnotationPresent(RestController.class) ||
                returnType.hasMethodAnnotation(ResponseBody.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        inject(body);
        return body;
    }

    void inject(Object object) {
        if (object == null) {
            return;
        }

        // ruo-yi
        if (object instanceof Map) {
            inject(((Map<?, ?>) object).get("data"));
            return;
        }

        for (String fieldName : wrapped) {
            Object property = propertyGetter.tryGetPropertyValue(object, fieldName);
            if (property != null) {
                inject(property);
                return;
            }
        }

        for (String target : targets) {
            Object property = propertyGetter.tryGetPropertyValue(object, target);
            if (property != null) {
                doInject(property);
                return;
            }
        }

        doInject(object);
    }


    void doInject(Object pojos) {
        if (serviceRefHelper != null) {
            serviceRefHelper.populateServiceReferences(pojos);
        }
        if (tableRefHelper != null) {
            tableRefHelper.populateTableReferences(pojos);
        }
        if (dictHelper != null) {
            dictHelper.populateDictText(pojos);
        }
    }

    private static class PropertyGetter {

        // 缓存类属性描述符
        private final ConcurrentMap<Class<?>, ConcurrentMap<String, PropertyDescriptor>> classCache
                = new ConcurrentHashMap<>();

        private static final PropertyDescriptor NULL_DESCRIPTOR = createNullDescriptor();

        /**
         * 尝试获取属性值，如果属性不存在则返回null，不抛异常
         *
         * @param pojo 目标JavaBean对象，不能为null
         * @param name 属性名称，不能为空
         * @return 属性的当前值，如果属性不存在则返回null
         */
        public Object tryGetPropertyValue(Object pojo, String name) {
            if (pojo == null || name == null || name.trim().isEmpty()) {
                return null;
            }

            try {
                PropertyDescriptor pd = getPropertyDescriptor(pojo.getClass(), name);
                if (pd == NULL_DESCRIPTOR) {
                    return null;
                }

                Method readMethod = pd.getReadMethod();
                if (readMethod == null) {
                    return null;
                }

                return readMethod.invoke(pojo);
            } catch (Exception e) {
                return null;
            }
        }

        private static PropertyDescriptor createNullDescriptor() {
            // 创建一个特殊的属性描述符，用于表示不存在的属性
            try {
                return new PropertyDescriptor("propertyName", AbsentPropertyDescriptor.class);
            } catch (IntrospectionException e) {
                throw new PojoAccessException(e);
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
    }

    private static class AbsentPropertyDescriptor {
        private String propertyName;

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }
    }

}
