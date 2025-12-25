package cn.filaura.weave.ref;


import cn.filaura.weave.annotation.Mapping;
import cn.filaura.weave.annotation.ServiceRef;
import cn.filaura.weave.annotation.ServiceRefs;
import cn.filaura.weave.exception.ReferenceDataNotFoundException;

import java.util.Map;
import java.util.function.Consumer;

public class ServiceRefWeaver extends AbstractReferenceWeaver {

    public void collectForeignKeyValues(Object pojos, Map<String, ServiceQuery> serviceQueryMap) {
        recursive(pojos, pojo -> {
            Class<?> beanClass = pojo.getClass();
            traverseServiceMappings(beanClass, recordFieldMapping ->
                    collectForeignKeyValue(pojo, recordFieldMapping, serviceQueryMap)
            );
        });
    }

    public void weave(Object pojos, Map<String, ServiceResult> serviceResultMap) {
        recursive(pojos, pojo -> {
            Class<?> beanClass = pojo.getClass();
            traverseServiceMappings(beanClass, recordFieldMapping ->
                    weaveSingleObject(pojo, recordFieldMapping, serviceResultMap)
            );
        });
    }

    private void collectForeignKeyValue(Object pojo,
                                        ServiceRef serviceRef,
                                        Map<String, ServiceQuery> serviceQueryMap) {
        // 构建查询信息对象
        String mapKey = buildMapKey(serviceRef);
        ServiceQuery serviceQuery = serviceQueryMap.get(mapKey);
        if (serviceQuery == null) {
            serviceQuery = new ServiceQuery();
            serviceQuery.setService(serviceRef.service());
            serviceQuery.setMethod(getMethodName(serviceRef));
            serviceQuery.setKeyField(getResultKeyField(serviceRef));
            serviceQueryMap.put(mapKey, serviceQuery);
        }

        for (Mapping mapping : serviceRef.mappings()) {
            // 获取外键值
            Object foreignKeyValue = pojoAccessor.getPropertyValue(pojo, mapping.refField());
            // 将外键值添加到ids集合中
            if (foreignKeyValue != null) {
                serviceQuery.getIds().add(foreignKeyValue);
            }
        }
    }

    private void weaveSingleObject(Object pojo,
                                   ServiceRef serviceRef,
                                   Map<String, ServiceResult> serviceResultMap) {
        // 获取查询结果对象
        String mapKey = buildMapKey(serviceRef);
        ServiceResult serviceResult = serviceResultMap.get(mapKey);

        for (Mapping mapping : serviceRef.mappings()) {
            Object foreignKeyValue = pojoAccessor.getPropertyValue(pojo, mapping.refField());
            if (foreignKeyValue == null) {
                continue;
            }

            if (serviceResult == null || serviceResult.getResults() == null) {
                if (serviceRef.ignoreMissing()) {
                    continue;
                }
                throw new ReferenceDataNotFoundException(
                        String.format("Result is null for %s.%s",
                                getServiceName(serviceRef), getMethodName(serviceRef)));
            }

            // 从查询结果中获取对应的数据对象
            String valueString = String.valueOf(foreignKeyValue);
            Object record = serviceResult.getResults().get(valueString);
            if (record == null) {
                if (serviceRef.ignoreMissing()) {
                    continue;
                }
                throw new ReferenceDataNotFoundException(
                        String.format("Key %s not found in %s.%s",
                                valueString, getServiceName(serviceRef), getMethodName(serviceRef))
                );
            }
            // 根据mapping配置将结果对象的属性映射到当前对象
            Object value = pojoAccessor.getPropertyValue(record, mapping.from());
            if (value != null) {
                writeConvertedProperty(pojo, mapping.to(), value);
            }
        }
    }

    private String getResultKeyField(ServiceRef serviceRef) {
        if (serviceRef.resultKeyField() != null && !serviceRef.resultKeyField().isEmpty()) {
            return serviceRef.resultKeyField();
        }
        if (globalPrimaryKey != null && !globalPrimaryKey.isEmpty()) {
            return globalPrimaryKey;
        }
        return serviceRef.mappings()[0].refField();
    }

    private String getMethodName(ServiceRef serviceRef) {
        if (serviceRef.method() != null && !serviceRef.method().isEmpty()) {
            return serviceRef.method();
        }
        return globalMethodName;
    }

    private String getServiceName(ServiceRef serviceRef) {
        return serviceRef.service().getSimpleName();
    }

    private String buildMapKey(ServiceRef serviceRef) {
        return ServiceRefProcessor.buildMapKey(
                serviceRef.service(), getMethodName(serviceRef));
    }

    private void traverseServiceMappings(Class<?> clazz, Consumer<ServiceRef> mappingHandler) {
        traverseAnnotations(clazz,
                ServiceRef.class,
                ServiceRefs.class,
                ServiceRefs::value,
                mappingHandler);
    }

}
