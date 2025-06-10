package cn.filaura.weave.ref;



import cn.filaura.weave.*;
import cn.filaura.weave.annotation.ColumnBinding;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.exception.RefDataNotFoundException;


import java.lang.reflect.Field;
import java.util.*;

public class RefWeaver extends AbstractWeaver {

    protected static final AnnotatedFieldExtractor REF_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Ref.class);

    /** 主键值分隔符 */
    protected static String delimiter = ",";

    /** 自动推断目标属性名称时使用的中缀 */
    protected static String fieldNameInfix = "Ref";

    /** 当引用的数据为null时，展示此字符 */
    protected static String nullDisplayText = "";



    public RefWeaver(BeanAccessor beanAccessor) {
        super(beanAccessor);
    }



    public Map<String, RefInfo> collectRefInfo(Object beans) {
        Map<String, RefInfo> refInfoMap = new HashMap<>();
        collectRefParams(beans, refInfoMap);
        collectFieldValues(beans, refInfoMap);
        return refInfoMap;
    }

    public void weave(Object beans, Map<String, RefInfo> refInfoMap) {
        recursive(beans, bean -> {
            Field[] refFields = REF_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : refFields) {
                String fieldName = field.getName();
                String fieldValue = getFieldValue(bean, fieldName);
                if (fieldValue == null || fieldValue.isEmpty()) {
                    continue;
                }

                Ref ref = field.getAnnotation(Ref.class);
                String mapKey = refMapKey(ref.table(), ref.key());
                RefInfo refInfo = refInfoMap.get(mapKey);
                if (refInfo == null || refInfo.getResults() == null) {
                    throw new RefDataNotFoundException(dataNotFoundMessage(ref.table(), ref.key()));
                }

                String targetBeanName = ref.targetBean();
                boolean isTargetBeanSpecified = targetBeanName != null && !targetBeanName.isEmpty();

                Object targetObj = bean;
                if (isTargetBeanSpecified) {
                    targetObj = beanAccessor.getProperty(bean, targetBeanName, GetMode.INIT_IF_NULL);
                    if (targetObj == null) {
                        throw new BeanAccessException("Error reading property: " + targetBeanName + " in class " + bean.getClass().getName());
                    }
                }

                boolean isMulti = fieldValue.contains(delimiter);
                String[] splitValues = isMulti ? fieldValue.split(delimiter) : new String[]{fieldValue};
                List<Map<String, Object>> records = getRecords(refInfo, splitValues);

                Map<String, String> columnBindingMap = bindColumns(ref, fieldName);

                Map<String, String> specifiedColumnMap = mapColumnsSpecified(records, columnBindingMap);
                for (Map.Entry<String, String> specified : specifiedColumnMap.entrySet()) {
                    beanAccessor.setProperty(targetObj, specified.getKey(), specified.getValue(), SetMode.ENFORCE_EXISTING);
                }

                if (isTargetBeanSpecified) {
                    Map<String, String> automaticColumnMap = mapColumnsAutomatic(records, columnBindingMap.keySet());
                    for (Map.Entry<String, String> auto : automaticColumnMap.entrySet()) {
                        beanAccessor.setProperty(targetObj, auto.getKey(), auto.getValue(), SetMode.SKIP_IF_ABSENT);
                    }
                }
            }
        });
    }

    private String aggregateColumnValues(List<Map<String, Object>> dataRecords, String targetColumn) {
        if (dataRecords.size() == 1) {
            return safeToString(dataRecords.get(0).get(targetColumn));
        }

        List<String> columnValues = new ArrayList<>();
        for (Map<String, Object> record : dataRecords) {
            columnValues.add(safeToString(record.get(targetColumn)));
        }
        return String.join(delimiter, columnValues);
    }

    private Map<String, String> mapColumnsAutomatic(List<Map<String, Object>> dataRecords, Set<String> excludedColumns) {
        Map<String, String> unSpecified = new HashMap<>();
        for (String column : dataRecords.get(0).keySet()) {
            if (!excludedColumns.contains(column)) {
                unSpecified.put(column, aggregateColumnValues(dataRecords, column));
            }
        }
        return unSpecified;
    }

    private Map<String, String> mapColumnsSpecified(List<Map<String, Object>> dataRecords, Map<String, String> columnBindings) {
        Map<String, String> specified = new HashMap<>();
        for (String columnName : columnBindings.keySet()) {
            String columnValue = aggregateColumnValues(dataRecords, columnName);
            String fieldName = columnBindings.get(columnName);
            String key = fieldName == null ? columnName : fieldName;
            specified.put(key, columnValue);
        }
        return specified;
    }

    private Map<String, Object> getRecord(RefInfo refInfo, String value) {
        Map<String, Object> record = refInfo.getResults().get(value);
        if (record == null) {
            throw new RefDataNotFoundException(dataNotFoundMessage(refInfo.getTable(), refInfo.getKey(), value));
        }

        return record;
    }

    private List<Map<String, Object>> getRecords(RefInfo refInfo, String[] splitValues) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (String splitValue : splitValues) {
            maps.add(getRecord(refInfo, splitValue));
        }
        return maps;
    }

    private Map<String, String> bindColumns(Ref ref, String rawName) {
        Map<String, String> map = new HashMap<>();
        for (String column : ref.columns()) {
            String fieldName = rawName + fieldNameInfix + capitalize(column);
            map.put(column, fieldName);
        }
        for (ColumnBinding binding : ref.bindings()) {
            map.put(binding.column(), binding.targetField());
        }
        return map;
    }

    private void collectRefParams(Object beans, Map<String, RefInfo> refInfoMap) {
        Set<Class<?>> allClasses = gatherClassTypes(beans);
        for (Class<?> aClass : allClasses) {
            for (Field field : REF_FIELD_EXTRACTOR.getAnnotatedFields(aClass)) {
                Ref ref = field.getAnnotation(Ref.class);
                RefInfo refInfo = refInfoMap.computeIfAbsent(
                        refMapKey(ref.table(), ref.key()),
                        n -> new RefInfo(ref.table(), ref.key())
                );
                for (String column : ref.columns()) {
                    refInfo.getColumns().add(column);
                }
                for (ColumnBinding binding : ref.bindings()) {
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
                        refMapKey(ref.table(), ref.key()),
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

    private String refMapKey(String table, String key) {
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



    public static String getDelimiter() {
        return delimiter;
    }

    public static void setDelimiter(String delimiter) {
        RefWeaver.delimiter = delimiter;
    }

    public static String getFieldNameInfix() {
        return fieldNameInfix;
    }

    public static void setFieldNameInfix(String fieldNameInfix) {
        RefWeaver.fieldNameInfix = fieldNameInfix;
    }

    public static String getNullDisplayText() {
        return nullDisplayText;
    }

    public static void setNullDisplayText(String nullDisplayText) {
        RefWeaver.nullDisplayText = nullDisplayText;
    }
}
