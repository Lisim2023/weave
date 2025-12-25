package cn.filaura.weave.ref;



import cn.filaura.weave.AnnotatedFieldExtractor;
import cn.filaura.weave.annotation.RecordEmbed;
import cn.filaura.weave.exception.ReferenceDataNotFoundException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordEmbedWeaver extends AbstractReferenceWeaver {

    private static final AnnotatedFieldExtractor RECORD_EMBED_FIELD_EXTRACTOR =
            new AnnotatedFieldExtractor(RecordEmbed.class);

    public Map<String, ServiceQuery> collectReferenceInfo(Object pojos) {
        Map<String, ServiceQuery> serviceQueryMap = new HashMap<>();
        collectForeignKeyValues(pojos, serviceQueryMap);
        removeIncompleteRefQuery(serviceQueryMap, ServiceQuery::getIds);
        return serviceQueryMap;
    }

    public void collectForeignKeyValues(Object pojos, Map<String, ServiceQuery> serviceQueryMap) {
        recursive(pojos, pojo -> {
            Class<?> beanClass = pojo.getClass();
            Field[] fields = RECORD_EMBED_FIELD_EXTRACTOR.getAnnotatedFields(beanClass);
            for (Field field : fields) {
                collectForeignKeyValue(pojo, field, serviceQueryMap);
            }
        });
    }

    public void weave(Object pojos, Map<String, ServiceResult> serviceResultMap) {
        recursive(pojos, pojo -> {
            Class<?> beanClass = pojo.getClass();
            Field[] fields = RECORD_EMBED_FIELD_EXTRACTOR.getAnnotatedFields(beanClass);
            for (Field field : fields) {
                weaveSingleObject(pojo, field, serviceResultMap);
            }
        });
    }

    private void collectForeignKeyValue(Object pojo,
                                        Field field,
                                        Map<String, ServiceQuery> serviceQueryMap) {
        RecordEmbed recordEmbed = field.getAnnotation(RecordEmbed.class);
        String refField = getRefField(recordEmbed, field.getName());

        // 获取外键字段值
        Object foreignKeyValue = pojoAccessor.getPropertyValue(pojo, refField);
        if (foreignKeyValue == null) {
            return;
        }

        // 构建MapKey
        String mapKey = buildMapKey(recordEmbed);
        ServiceQuery serviceQuery = serviceQueryMap.get(mapKey);
        if (serviceQuery == null) {
            serviceQuery = new ServiceQuery();
            serviceQuery.setService(recordEmbed.service());
            serviceQuery.setMethod(getMethodName(recordEmbed));
            serviceQuery.setKeyField(getResultKeyField(recordEmbed, field.getName()));
            serviceQueryMap.put(mapKey, serviceQuery);
        }

        // 收集外键值
        if (foreignKeyValue instanceof Collection) {
            // 处理集合类型的外键
            serviceQuery.getIds().addAll((Collection<?>) foreignKeyValue);
        } else if (foreignKeyValue.getClass().isArray()) {
            // 处理数组类型的外键
            int length = Array.getLength(foreignKeyValue);
            for (int i = 0; i < length; i++) {
                serviceQuery.getIds().add(Array.get(foreignKeyValue, i));
            }
        } else {
            // 单值外键
            serviceQuery.getIds().add(foreignKeyValue);
        }
    }

    private void weaveSingleObject(Object pojo,
                                   Field field,
                                   Map<String, ServiceResult> serviceResultMap) {
        RecordEmbed recordEmbed = field.getAnnotation(RecordEmbed.class);
        String refField = getRefField(recordEmbed, field.getName());

        // 获取外键值
        Object foreignKeyValue = pojoAccessor.getPropertyValue(pojo, refField);
        if (foreignKeyValue == null) {
            return;
        }

        // 构建MapKey
        String key = buildMapKey(recordEmbed);
        ServiceResult serviceResult = serviceResultMap.get(key);
        if (serviceResult == null || serviceResult.getResults() == null) {
            if (recordEmbed.ignoreMissing()) {
                return;
            }
            throw new ReferenceDataNotFoundException(
                    String.format("Result is null for %s.%s",
                            getServiceName(recordEmbed), getMethodName(recordEmbed)));
        }

        // 根据外键值从查询结果中获取对应的数据
        Object resultData = extractResultValue(
                recordEmbed, serviceResult, foreignKeyValue, field.getType());

        // 将结果设置到目标字段
        if (resultData != null) {
            writeRawProperty(pojo, field.getName(), resultData);
        }
    }

    private Object extractResultValue(RecordEmbed recordEmbed,
                                      ServiceResult serviceResult,
                                      Object foreignKeyValue,
                                      Class<?> fieldType) {
        if (foreignKeyValue == null) return null;

        if (Collection.class.isAssignableFrom(fieldType)) {
            String[] keys = toStringArray(foreignKeyValue);
            Stream<?> recordStream = Arrays.stream(keys)
                    .map(key -> extractSingleRecord(recordEmbed, serviceResult, key))
                    .filter(Objects::nonNull);
            return convertStreamToCollection(recordStream, fieldType);
        }

        if (fieldType.isArray()) {
            String[] keys = toStringArray(foreignKeyValue);
            List<?> records = Arrays.stream(keys)
                    .map(key -> extractSingleRecord(recordEmbed, serviceResult, key))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (records.isEmpty()) {
                return null;
            }
            Class<?> clazz = records.get(0).getClass();
            int size = records.size();
            Object recordArray = Array.newInstance(clazz, size);
            for (int i = 0; i < size; i++) {
                Array.set(recordArray, i, records.get(i));
            }
            return recordArray;
        }

        return extractSingleRecord(recordEmbed, serviceResult, String.valueOf(foreignKeyValue));
    }

    private Object extractSingleRecord(RecordEmbed recordEmbed,
                                       ServiceResult serviceResult,
                                       String valueString) {
        Object record = serviceResult.getResults().get(valueString);
        if (record == null && !recordEmbed.ignoreMissing()) {
            throw new ReferenceDataNotFoundException(
                    String.format("Key %s not found in %s.%s",
                            valueString, getServiceName(recordEmbed), getMethodName(recordEmbed)));
        }
        return record;
    }

    private String getResultKeyField(RecordEmbed recordEmbed, String fieldName) {
        if (recordEmbed.resultKeyField() != null && !recordEmbed.resultKeyField().isEmpty()) {
            return recordEmbed.resultKeyField();
        }
        if (globalPrimaryKey != null && !globalPrimaryKey.isEmpty()) {
            return globalPrimaryKey;
        }
        return getRefField(recordEmbed, fieldName);
    }

    private String getRefField(RecordEmbed recordEmbed, String fieldName) {
        if (recordEmbed.refField() != null && !recordEmbed.refField().isEmpty()) {
            return recordEmbed.refField();
        }
        return fieldName + globalForeignKeySuffix;
    }

    private String getMethodName(RecordEmbed recordEmbed) {
        if (recordEmbed.method() != null && !recordEmbed.method().isEmpty()) {
            return recordEmbed.method();
        }
        return globalMethodName;
    }

    private String getServiceName(RecordEmbed recordEmbed) {
        return recordEmbed.service().getSimpleName();
    }

    private String buildMapKey(RecordEmbed recordEmbed) {
        return ServiceRefProcessor.buildMapKey(
                recordEmbed.service(), getMethodName(recordEmbed));
    }

}
