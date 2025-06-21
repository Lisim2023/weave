package cn.filaura.weave.ref;



import cn.filaura.weave.*;
import cn.filaura.weave.annotation.Bind;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.exception.RefDataNotFoundException;


import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefWeaver extends AbstractWeaver {

    protected static final AnnotatedFieldExtractor REF_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Ref.class);

    /** 主键值分隔符 */
    protected String delimiter = ",";

    /** 自动推断目标属性名称时使用的中缀 */
    protected String fieldNameInfix = "Ref";

    /** 当被引用的字段值为null时，展示此字符 */
    protected String nullDisplayText = "null";

    /** 当被引用的数据记录不存在时的处理方式，默认拋出异常 */
    protected MissingReferenceBehavior missingReferenceBehavior = MissingReferenceBehavior.ThrowException;



    public RefWeaver(BeanAccessor beanAccessor) {
        super(beanAccessor);
    }



    public Map<String, RefInfo> collectRefInfo(Object beans) {
        Map<String, RefInfo> refInfoMap = new HashMap<>();
        collectRefMetadata(beans, refInfoMap);
        collectFieldValues(beans, refInfoMap);
        return refInfoMap;
    }

    public void populateRefData(Object beans, Map<String, RefInfo> refInfoMap) {
        recursive(beans, bean -> {
            Field[] refFields = REF_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : refFields) {
                Ref ref = field.getAnnotation(Ref.class);
                String mapKey = buildRefInfoMapKey(ref.table(), ref.key());
                RefInfo refInfo = refInfoMap.get(mapKey);
                if (refInfo == null || refInfo.getResults() == null) {
                    throw new RefDataNotFoundException(dataNotFoundMessage(ref.table(), ref.key()));
                }

                String targetBeanName = ref.targetBean();
                boolean isTargetBeanSpecified = targetBeanName != null && !targetBeanName.isEmpty();
                Object targetObj;
                if (isTargetBeanSpecified) {
                    targetObj = beanAccessor.getProperty(bean, targetBeanName, GetMode.INIT_IF_NULL);
                    if (targetObj == null) {
                        throw new BeanAccessException("Error reading property: "
                                + targetBeanName + " in class " + bean.getClass().getName());
                    }
                }else {
                    targetObj = bean;
                }

                String fieldName = field.getName();
                String fieldValue = getFieldValue(bean, fieldName);
                if (fieldValue == null || fieldValue.isEmpty()) {
                    continue;
                }

                Map<String, String> columnToFieldMap = buildColumnFieldMapping(ref, fieldName);

                boolean isMulti = fieldValue.contains(delimiter);
                if (isMulti) {
                    String[] splitValues = fieldValue.split(delimiter);
                    List<Map<String, Object>> records = getRecords(refInfo, splitValues);

                    Map<String, String> mappedProperties = extractMappedProperties(columnToFieldMap,
                            columnName -> records.stream()
                                    .map(record -> safeToString(record.get(columnName)))
                                    .collect(Collectors.joining(delimiter)));
                    mappedProperties.forEach((k, v) -> {
                        beanAccessor.setProperty(targetObj, k, v, SetMode.ENFORCE_EXISTING);
                    });

                } else {
                    Map<String, Object> record = getRecord(refInfo, fieldValue);
                    if (record.isEmpty()) {
                        continue;
                    }

                    Map<String, String> mappedProperties = extractMappedProperties(columnToFieldMap,
                            columnName -> safeToString(record.get(columnName)));
                    mappedProperties.forEach((k, v) -> {
                        beanAccessor.setProperty(targetObj, k, v, SetMode.ENFORCE_EXISTING);
                    });

                    if (isTargetBeanSpecified) {
                        Map<String, String> unmappedProperties =
                                extractUnmappedProperties(record, columnToFieldMap.keySet());
                        unmappedProperties.forEach((k, v) -> {
                            beanAccessor.setProperty(targetObj, k, v, SetMode.SKIP_IF_ABSENT);
                        });
                    }
                }
            }
        });
    }

    private Map<String, String> extractUnmappedProperties(Map<String, Object> record, Set<String> excludedColumns) {
        Map<String, String> unMapped = new HashMap<>();
        for (String key : record.keySet()) {
            if (!excludedColumns.contains(key)) {
                unMapped.put(key, safeToString(record.get(key)));
            }
        }
        return unMapped;
    }

    private Map<String, String> extractMappedProperties(Map<String, String> columnToFieldMap,
                                                        Function<String, String> valueGetter) {
        Map<String, String> mapped = new HashMap<>();
        for (String columnName : columnToFieldMap.keySet()) {
            String fieldName = columnToFieldMap.get(columnName);
            String key = fieldName == null ? columnName : fieldName;
            String value = valueGetter.apply(columnName);
            mapped.put(key, value);
        }
        return mapped;
    }

    private Map<String, Object> getRecord(RefInfo refInfo, String value) {
        Map<String, Object> record = refInfo.getResults().get(value);
        if (record == null) {
            if (MissingReferenceBehavior.ThrowException.equals(missingReferenceBehavior)) {
                throw new RefDataNotFoundException(dataNotFoundMessage(refInfo.getTable(), refInfo.getKey(), value));
            }
            return new HashMap<>();
        }

        return record;
    }

    private List<Map<String, Object>> getRecords(RefInfo refInfo, String[] values) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (String value : values) {
            maps.add(getRecord(refInfo, value));
        }
        return maps;
    }

    private Map<String, String> buildColumnFieldMapping(Ref ref, String rawName) {
        Map<String, String> map = new HashMap<>();
        for (String column : ref.columns()) {
            String derivedFieldName = rawName + fieldNameInfix + capitalize(column);
            map.put(column, derivedFieldName);
        }
        for (Bind binding : ref.bindings()) {
            map.put(binding.column(), binding.targetField());
        }
        return map;
    }

    private void collectRefMetadata(Object beans, Map<String, RefInfo> refInfoMap) {
        Set<Class<?>> allClasses = gatherClassTypes(beans);
        for (Class<?> aClass : allClasses) {
            for (Field field : REF_FIELD_EXTRACTOR.getAnnotatedFields(aClass)) {
                Ref ref = field.getAnnotation(Ref.class);
                RefInfo refInfo = refInfoMap.computeIfAbsent(
                        buildRefInfoMapKey(ref.table(), ref.key()),
                        n -> new RefInfo(ref.table(), ref.key())
                );
                for (String column : ref.columns()) {
                    refInfo.getColumns().add(column);
                }
                for (Bind binding : ref.bindings()) {
                    refInfo.getColumns().add(binding.column());
                }
            }
        }
    }

    private void collectFieldValues(Object beans, Map<String, RefInfo> refInfoMap) {
        recursive(beans, bean -> {
            for (Field field : REF_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass())) {
                String valueString = getFieldValue(bean, field.getName());
                if (valueString == null || valueString.isEmpty()){
                    continue;
                }

                Ref ref = field.getAnnotation(Ref.class);
                RefInfo refInfo = refInfoMap.computeIfAbsent(
                        buildRefInfoMapKey(ref.table(), ref.key()),
                        n -> new RefInfo(ref.table(), ref.key())
                );

                boolean isMulti = valueString.contains(delimiter);
                if (isMulti) {
                    String[] splitValues = valueString.split(delimiter);
                    refInfo.getKeyValues().addAll(Arrays.asList(splitValues));
                }else {
                    refInfo.getKeyValues().add(valueString);
                }
            }
        });
    }

    private String safeToString(Object object) {
        return object == null ? nullDisplayText : object.toString();
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



    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getFieldNameInfix() {
        return fieldNameInfix;
    }

    public void setFieldNameInfix(String fieldNameInfix) {
        this.fieldNameInfix = fieldNameInfix;
    }

    public String getNullDisplayText() {
        return nullDisplayText;
    }

    public void setNullDisplayText(String nullDisplayText) {
        this.nullDisplayText = nullDisplayText;
    }

    public MissingReferenceBehavior getMissingReferenceBehavior() {
        return missingReferenceBehavior;
    }

    public void setMissingReferenceBehavior(MissingReferenceBehavior missingReferenceBehavior) {
        this.missingReferenceBehavior = missingReferenceBehavior;
    }
}
