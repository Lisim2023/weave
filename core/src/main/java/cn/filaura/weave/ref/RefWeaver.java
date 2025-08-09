package cn.filaura.weave.ref;



import cn.filaura.weave.*;
import cn.filaura.weave.annotation.Mapping;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.exception.RefDataNotFoundException;
import cn.filaura.weave.exception.WeaveException;
import cn.filaura.weave.type.ConvertUtil;


import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefWeaver extends AbstractWeaver {

    protected static final AnnotatedFieldExtractor REF_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Ref.class);

    /** 全局主键名 */
    private String globalPrimaryKey = "id";

    /** 当被引用的字段值为null时，展示此字符 */
    private String nullDisplayText = "null";



    public Map<String, RefInfo> collectRefInfo(Object beans) {
        Map<String, RefInfo> refInfoMap = new HashMap<>();
        collectRefMetadata(beans, refInfoMap);
        collectFieldValues(beans, refInfoMap);
        removeIncompleteRefInfo(refInfoMap);
        return refInfoMap;
    }

    public void populateRefData(Object beans, Map<String, RefInfo> refInfoMap) {
        recursive(beans, bean -> {
            Field[] refFields = REF_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : refFields) {
                Object fieldValue = beanAccessor.getProperty(bean, field.getName());
                if (fieldValue == null) {
                    continue;
                }

                Ref ref = field.getAnnotation(Ref.class);
                String compositeKey = buildRefInfoMapKey(ref);
                RefInfo refInfo = refInfoMap.get(compositeKey);
                if (refInfo == null || refInfo.getResults() == null) {
                    throw new RefDataNotFoundException(dataNotFoundMessage(getTableName(ref), getKeyName(ref)));
                }

                Map<String, Object> results = refInfo.getResults();
                Class<?> fieldType = field.getType();
                if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
                    Collection<String> keyValues = convertToKeyValues(fieldValue);
                    List<Object> records = keyValues.stream()
                            .map(keyValue -> fetchRecord(results, keyValue, ref))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    handleCollectionCase(bean, ref, records);

                } else {
                    String keyValue = String.valueOf(fieldValue);
                    Object record = fetchRecord(results, keyValue, ref);
                    if (record != null) {
                        handleSingleCase(bean, ref, record);
                    }
                }
            }
        });
    }

    private Object fetchRecord(Map<String, Object> results, String keyValue, Ref ref) {
        Object record = results.get(keyValue);
        if (record == null && !ref.ignoreMissing()) {
            throw new RefDataNotFoundException(dataNotFoundMessage(getTableName(ref), getKeyName(ref), keyValue));
        }
        return record;
    }

    private void handleCollectionCase(Object bean, Ref ref, List<Object> records) throws BeanAccessException {
        if (ref.mapTo().isEmpty()) {
            throw new WeaveException("Collection type requires mapTo parameter to be specified");
        }
        Object resultCollection = convertResultsToFieldType(bean, ref, records);
        beanAccessor.setProperty(bean, ref.mapTo(), resultCollection);
    }

    private Object convertResultsToFieldType(Object bean, Ref ref, List<Object> records) {
        if (records.isEmpty()) return null;

        Class<?> targetType = beanAccessor.getPropertyType(bean, ref.mapTo());
        if (targetType.isArray()) {
            Class<?> compType = targetType.getComponentType();
            Object array = Array.newInstance(compType, records.size());
            for (int i = 0; i < records.size(); i++) {
                Object element = adaptRecordToBean(records.get(i), compType, ref);
                Array.set(array, i, element);
            }
            return array;

        } else if (Collection.class.isAssignableFrom(targetType)) {
            Collection<Object> collection = createCollectionInstance(targetType);
            Class<?> genericType = beanAccessor.getCollectionGenericType(bean, ref.mapTo());
            for (Object record : records) {
                Object element = adaptRecordToBean(record, genericType, ref);
                collection.add(element);
            }
            return collection;
        }

        throw new WeaveException("Unsupported collection type: " + targetType);
    }

    private void handleSingleCase(Object bean, Ref ref, Object record) throws BeanAccessException {
        if (record == null) return;

        if (ref.mapTo().isEmpty()) {
            injectRefData(bean, ref, record);
        } else {
            Object target = beanAccessor.getProperty(bean, ref.mapTo());
            if (target == null) {
                Class<?> propertyType = beanAccessor.getPropertyType(bean, ref.mapTo());
                target = adaptRecordToBean(record, propertyType, ref);
                beanAccessor.setProperty(bean, ref.mapTo(), target);
            }else {
                injectRefData(target, ref, record);
            }
        }
    }

    private Object adaptRecordToBean(Object record, Class<?> beanClass, Ref ref) {
        if (record instanceof Map) {
            Object newBean = createBeanInstance(beanClass);
            injectRefData(newBean, ref, record);
            return newBean;
        }else {
            return record;
        }
    }

    private void injectRefData(Object mapTarget, Ref ref, Object record) {
        Map<String, Object> mappedProperties = extractMappedProperties(ref, columnName -> getDataFromRecord(record, columnName));
        if (record instanceof Map) {
            injectPropertiesWithConvert(mapTarget, mappedProperties, false);
            if (!ref.mapTo().isEmpty()) {
                Map<String, Object> unmappedProperties = extractUnmappedProperties((Map<String, Object>) record, mappedProperties.keySet());
                injectPropertiesWithConvert(mapTarget, unmappedProperties, true);
            }
        }else {
            mappedProperties.forEach((name, value) -> {
                beanAccessor.setProperty(mapTarget, name, value);
            });
        }
    }

    private void injectPropertiesWithConvert(Object bean, Map<String, Object> properties, boolean skipIfAbsent) {
        properties.forEach((name, value) -> {
            try {
                Class<?> propertyType = beanAccessor.getPropertyType(bean, name);
                Object convert = ConvertUtil.convert(safeToString(value), propertyType);
                beanAccessor.setProperty(bean, name, convert);
            }catch (BeanAccessException e) {
                if (skipIfAbsent) {
                    return;
                }
                throw e;
            }
        });
    }

    private Object getDataFromRecord(Object record, String columnName) {
        if (record instanceof Map) {
            return ((Map<?, ?>) record).get(columnName);
        }
        return beanAccessor.getProperty(record, columnName);
    }

    // Helper methods
    private Collection<String> convertToKeyValues(Object fieldValue) {
        if (fieldValue == null) return Collections.emptyList();
        if (fieldValue instanceof Collection) {
            return ((Collection<?>) fieldValue).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else if (fieldValue.getClass().isArray()) {
            int length = Array.getLength(fieldValue);
            List<String> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(String.valueOf(Array.get(fieldValue, i)));
            }
            return list;
        }
        throw new WeaveException("Unsupported collection type: " + fieldValue.getClass());
    }

    private Object createBeanInstance(Class<?> beanClass) {
        try {
            Constructor<?> constructor = beanClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new WeaveException("Failed to instantiate object: " + beanClass.getSimpleName() +
                    " requires a public no-argument constructor", e);
        }
    }

    private Collection<Object> createCollectionInstance(Class<?> collectionType) {
        if (List.class.isAssignableFrom(collectionType)) return new ArrayList<>();
        if (Set.class.isAssignableFrom(collectionType)) return new HashSet<>();
        throw new WeaveException("Unsupported collection type: " + collectionType);
    }

    private Map<String, Object> extractUnmappedProperties(Map<String, Object> record,
                                                          Set<String> excludedColumns) {
        Map<String, Object> unMapped = new HashMap<>();
        for (Object key : record.keySet()) {
            String keyString = key.toString();
            if (!excludedColumns.contains(keyString)) {
                unMapped.put(keyString, record.get(keyString));
            }
        }
        return unMapped;
    }

    private Map<String, Object> extractMappedProperties(Ref ref,
                                                        Function<String, Object> valueGetter) {
        Map<String, Object> mapped = new HashMap<>();
        for (Mapping mapping : ref.mappings()) {
            Object value = valueGetter.apply(mapping.column());
            mapped.put(mapping.property(), value);
        }
        return mapped;
    }

    private void collectRefMetadata(Object beans, Map<String, RefInfo> refInfoMap) {
        Set<Class<?>> allClasses = gatherClassTypes(beans);
        for (Class<?> aClass : allClasses) {
            for (Field field : REF_FIELD_EXTRACTOR.getAnnotatedFields(aClass)) {
                Ref ref = field.getAnnotation(Ref.class);
                String table = getTableName(ref);
                String key = getKeyName(ref);
                RefInfo refInfo = refInfoMap.computeIfAbsent(
                        buildRefInfoMapKey(ref),
                        n -> new RefInfo(table, key)
                );
                for (Mapping binding : ref.mappings()) {
                    refInfo.getColumns().add(binding.column());
                }
            }
        }
    }

    private void collectFieldValues(Object beans, Map<String, RefInfo> refInfoMap) {
        recursive(beans, bean -> {
            for (Field field : REF_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass())) {
                Object fieldValue = beanAccessor.getProperty(bean, field.getName());
                if (fieldValue == null) {
                    continue;
                }

                Ref ref = field.getAnnotation(Ref.class);
                String table = getTableName(ref);
                String key = getKeyName(ref);
                RefInfo refInfo = refInfoMap.computeIfAbsent(
                        buildRefInfoMapKey(ref),
                        n -> new RefInfo(table, key)
                );

                if (fieldValue.getClass().isArray()) {  // 处理数组类型
                    int length = Array.getLength(fieldValue);
                    for (int i = 0; i < length; i++) {
                        Object element = Array.get(fieldValue, i);
                        refInfo.getKeyValues().add(String.valueOf(element));
                    }
                } else if (fieldValue instanceof Collection) {  // 处理集合类型
                    Collection<?> collection = (Collection<?>) fieldValue;
                    for (Object element : collection) {
                        refInfo.getKeyValues().add(String.valueOf(element));
                    }
                } else {  // 处理其他类型
                    refInfo.getKeyValues().add(String.valueOf(fieldValue));
                }
            }
        });
    }

    private void removeIncompleteRefInfo(Map<String, RefInfo> refInfoMap) {
        Iterator<Map.Entry<String, RefInfo>> iterator = refInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, RefInfo> entry = iterator.next();
            RefInfo refInfo = entry.getValue();
            Set<String> keyValues = refInfo.getKeyValues();

            // 检查 keyValues 是否为 null 或空集合
            if (keyValues == null || keyValues.isEmpty()) {
                iterator.remove();  // 安全移除当前条目
            }
        }
    }

    private String safeToString(Object object) {
        return object == null ? nullDisplayText : object.toString();
    }

    private String getTableName(Ref ref) {
        return ref.table();
    }

    private String getKeyName(Ref ref) {
        return ref.key().isEmpty() ? globalPrimaryKey : ref.key();
    }

    private String buildRefInfoMapKey(Ref ref) {
        String table = getTableName(ref);
        String key = getKeyName(ref);
        return buildRefInfoMapKey(table, key);
    }

    private String buildRefInfoMapKey(String table, String key) {
        if (key == null || key.isEmpty()) {
            return table;
        }
        return table + "_" + key;
    }

    private String dataNotFoundMessage(String table, String key) {
        return dataNotFoundMessage(table, key, null);
    }

    private String dataNotFoundMessage(String table, String key, String value) {
        String msg = String.format("Data not found in table '%s'", table);
        if (key != null && !key.isEmpty()) {
            msg += String.format(" for column '%s'", key);
        }
        if (value != null) {
            msg += String.format(" with value '%s'", value);
        }
        return msg;
    }



    public String getNullDisplayText() {
        return nullDisplayText;
    }

    public void setNullDisplayText(String nullDisplayText) {
        this.nullDisplayText = nullDisplayText;
    }

    public String getGlobalPrimaryKey() {
        return globalPrimaryKey;
    }

    public void setGlobalPrimaryKey(String globalPrimaryKey) {
        this.globalPrimaryKey = globalPrimaryKey;
    }

}
