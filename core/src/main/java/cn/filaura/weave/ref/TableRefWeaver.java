package cn.filaura.weave.ref;

import cn.filaura.weave.annotation.TableRef;
import cn.filaura.weave.annotation.TableRefs;
import cn.filaura.weave.annotation.Mapping;
import cn.filaura.weave.exception.ReferenceDataNotFoundException;

import java.util.Map;
import java.util.function.Consumer;

public class TableRefWeaver extends AbstractReferenceWeaver {

    public void collectFieldValues(Object pojos, Map<String, TableQuery> dbQueryMap) {
        recursive(pojos, pojo -> {
            Class<?> beanClass = pojo.getClass();
            traverseDbMappings(beanClass,
                    tableRef -> collectForeignKeyValue(pojo, tableRef, dbQueryMap));
        });
    }

    public void weave(Object pojos, Map<String, TableResult> dbResultMap) {
        recursive(pojos, pojo -> {
            Class<?> beanClass = pojo.getClass();
            traverseDbMappings(beanClass,
                    tableRef -> weaveSingleObject(pojo, tableRef, dbResultMap));
        });
    }

    private void weaveSingleObject(Object pojo,
                                   TableRef tableRef,
                                   Map<String, TableResult> dbResultMap) {
        String mapKey = buildMapKey(tableRef);
        TableResult tableResult = dbResultMap.get(mapKey);

        for (Mapping mapping : tableRef.mappings()) {
            Object foreignKeyValue = pojoAccessor.getPropertyValue(pojo, mapping.refField());
            if (foreignKeyValue == null) {
                continue;
            }

            if (tableResult == null || tableResult.getResults() == null) {
                if (tableRef.ignoreMissing()) {
                    continue;
                }
                throw new ReferenceDataNotFoundException(
                        String.format("Result is null for %s.%s", tableRef.table(),
                                tableRef.keyColumn()));
            }

            String valueString = String.valueOf(foreignKeyValue);
            Map<String, Object> recordRow = tableResult.getResults().get(valueString);
            if (recordRow == null) {
                if (tableRef.ignoreMissing()) {
                    continue;
                }
                throw new ReferenceDataNotFoundException(
                        String.format("Key %s not found in %s.%s",
                                valueString, tableRef.table(), tableRef.keyColumn())
                );
            }

            Object columnValue = recordRow.get(mapping.from());
            writeConvertedProperty(pojo, mapping.to(), columnValue);
        }
    }

    private void collectForeignKeyValue(Object pojo,
                                        TableRef tableRef,
                                        Map<String, TableQuery> dbQueryMap) {
        String mapKey = buildMapKey(tableRef);
        TableQuery tableQuery = dbQueryMap.get(mapKey);
        if (tableQuery == null) {
            String key = getKeyColumn(tableRef);
            tableQuery = new TableQuery(tableRef.table(), key);
            dbQueryMap.put(mapKey, tableQuery);

            // 添加需要查询的字段
            for (Mapping mapping : tableRef.mappings()) {
                tableQuery.getColumns().add(mapping.from());
            }
            // 确保主键字段在字段集合中
            tableQuery.getColumns().add(key);
        }

        // 收集外键值
        for (Mapping mapping : tableRef.mappings()) {
            Object foreignKeyValue = pojoAccessor.getPropertyValue(pojo, mapping.refField());
            if (foreignKeyValue != null) {
                tableQuery.getIds().add(foreignKeyValue);
            }
        }
    }

    private String buildMapKey(TableRef tableRef) {
        String key = getKeyColumn(tableRef);
        return TableRefProcessor.buildMapKey(tableRef.table(), key);
    }

    private String getKeyColumn(TableRef tableRef) {
        if (tableRef.keyColumn() != null && !tableRef.keyColumn().isEmpty()) {
            return tableRef.keyColumn();
        }
        if (globalPrimaryKey != null && !globalPrimaryKey.isEmpty()) {
            return globalPrimaryKey;
        }
        return camelToSnake(tableRef.mappings()[0].refField());
    }

    private String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder sb = new StringBuilder(camelCase.length() + 10); // 预估容量，减少扩容

        char[] chars = camelCase.toCharArray();
        sb.append(Character.toLowerCase(chars[0]));

        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                // 检查是否是连续大写字母的一部分（如 XMLParser -> xml_parser）
                // 如果前一个字符是小写，或者后一个字符是小写，则插入下划线
                if (Character.isLowerCase(chars[i - 1]) ||
                        (i + 1 < chars.length && Character.isLowerCase(chars[i + 1]))) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private void traverseDbMappings(Class<?> clazz, Consumer<TableRef> mappingHandler) {
        traverseAnnotations(clazz,
                TableRef.class,
                TableRefs.class,
                TableRefs::value,
                mappingHandler);
    }
}
