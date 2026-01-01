package cn.filaura.weave.ref;


import cn.filaura.weave.exception.DataLoadingException;
import cn.filaura.weave.exception.MethodNotFoundException;
import cn.filaura.weave.exception.ServiceNotFoundException;



import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class SpringBeanMethodInvoker {

    private final ApplicationContext applicationContext;
    private final Map<MethodKey, MethodInfo> methodInfoCache = new ConcurrentHashMap<>();
    private final Map<ServiceKey, Object> serviceInstanceCache = new ConcurrentHashMap<>();

    private ResultExtractor resultExtractor = result -> result;

    public SpringBeanMethodInvoker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }



    public Collection<?> invokeServiceMethod(Class<?> serviceClass,
                                             String methodName,
                                             List<?> ids) {
        return invokeServiceMethod(serviceClass, "", methodName, ids);
    }

    /**
     * 执行服务方法调用
     */
    public Collection<?> invokeServiceMethod(Class<?> serviceClass,
                                             String serviceName,
                                             String methodName,
                                             List<?> ids) {
        // 获取服务实例和方法
        Object serviceInstance = getServiceInstance(serviceClass, serviceName);
        Method method = getMethod(serviceClass, methodName);

        // 执行方法调用
        try {
            Object result = method.invoke(serviceInstance, ids);
            Object data = resultExtractor.extract(result);
            if (data == null) {
                return new ArrayList<>();
            }
            if (data instanceof Collection) {
                return (Collection<?>) data;
            }

            throw new DataLoadingException(
                    "Service method " + methodName + " on class " + serviceClass.getName() +
                    " returned an unsupported result type: " + data.getClass().getName() +
                    ". Expected a Collection.");
        } catch (DataLoadingException e) {
            throw e;
        } catch (Exception e) {
            throw new DataLoadingException(
                    "Failed to invoke method " + methodName +
                            " on service " + serviceClass.getName(), e);
        }
    }

    /**
     * 获取服务实例
     */
    private Object getServiceInstance(Class<?> serviceClass, String serviceName) {
        ServiceKey serviceKey = new ServiceKey(serviceClass, serviceName);

        return serviceInstanceCache.computeIfAbsent(serviceKey, key -> {
            try {
                if (serviceName == null || serviceName.isEmpty()) {
                    return applicationContext.getBean(serviceClass);
                }
                return applicationContext.getBean(serviceName, serviceClass);
            } catch (Exception e) {
                throw new ServiceNotFoundException("Service not found: "
                        + serviceClass.getName(), e);
            }
        });
    }

    /**
     * 获取方法
     */
    public Method getMethod(Class<?> serviceClass, String methodName) {
        return getMethodInfo(serviceClass, methodName).method;
    }

    /**
     * 获取方法返回值的元素类型
     */
    public Class<?> getMethodReturnElementType(Class<?> serviceClass, String methodName) {
        return getMethodInfo(serviceClass, methodName).returnElementType;
    }

    /**
     * 获取方法并缓存
     */
    private MethodInfo getMethodInfo(Class<?> serviceClass, String methodName) {
        MethodKey key = new MethodKey(serviceClass, methodName);
        return methodInfoCache.computeIfAbsent(key, k -> {
            Method method = Arrays.stream(serviceClass.getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getParameterTypes()[0].isAssignableFrom(List.class))
                    .findFirst()
                    .orElseThrow(() -> new MethodNotFoundException(
                            "Method not found: " + methodName + " in " + serviceClass.getName()));
            return new MethodInfo(method);
        });
    }


    public ResultExtractor getResultExtractor() {
        return resultExtractor;
    }

    public void setResultExtractor(ResultExtractor resultExtractor) {
        this.resultExtractor = resultExtractor;
    }

    // 方法键内部类
    static class MethodKey {
        private final Class<?> serviceClass;
        private final String methodName;

        MethodKey(Class<?> serviceClass, String methodName) {
            this.serviceClass = serviceClass;
            this.methodName = methodName;
        }

        // equals, hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodKey)) return false;
            MethodKey that = (MethodKey) o;
            return Objects.equals(serviceClass, that.serviceClass) &&
                    Objects.equals(methodName, that.methodName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceClass, methodName);
        }
    }

    // 方法及相关信息内部类
    static class MethodInfo {
        final Method method;
        final Class<?> returnElementType;

        MethodInfo(Method method) {
            this.method = method;
            this.returnElementType = extractReturnElementType(method);
        }


        private static Class<?> extractReturnElementType(Method method) {
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) returnType).getActualTypeArguments();
                if (args.length > 0 && args[0] instanceof Class<?>) {
                    return (Class<?>) args[0];
                }
            }
            throw new IllegalStateException("Cannot determine return element type for method: "
                    + method.getName());
        }
    }

    // 服务键内部类
    static class ServiceKey {
        private final Class<?> serviceClass;
        private final String serviceName;

        ServiceKey(Class<?> serviceClass, String serviceName) {
            this.serviceClass = serviceClass;
            this.serviceName = serviceName;
        }

        // equals, hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ServiceKey)) return false;
            ServiceKey that = (ServiceKey) o;
            return Objects.equals(serviceClass, that.serviceClass) &&
                    Objects.equals(serviceName, that.serviceName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceClass, serviceName);
        }
    }

}
