package cn.filaura.weave.dict;

import cn.filaura.weave.CommonUtils;
import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.exception.DictDataNotFoundException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public class DictValueWeaver extends AbstractDictWeaver {

    public void weaveDictValue(Object pojos, Map<String, DictInfo> dictInfoMap) {
        Map<String, DictInfo> invertedDictInfoMap = invertDictInfo(dictInfoMap);

        recursive(pojos, pojo -> {
            Field[] dictFields = DICT_FIELD_EXTRACTOR.getAnnotatedFields(pojo.getClass());
            for (Field field : dictFields) {
                String currentFieldName = field.getName();
                Dict dict = field.getAnnotation(Dict.class);
                String textFieldName = getTextFieldName(dict, currentFieldName);
                Object textFieldValue = pojoAccessor.getPropertyValue(pojo, textFieldName);
                if (textFieldValue == null) {
                    continue;
                }

                String mapKey = buildMapKey(dict);
                DictInfo dictInfo = invertedDictInfoMap.get(mapKey);
                if (dictInfo == null) {
                    throw new DictDataNotFoundException("DictInfo not found for key: " + mapKey);
                }

                Class<?> targetElementType = getElementType(field);
                Object result = textToValue(dictInfo, textFieldValue, targetElementType);
                pojoAccessor.setPropertyValue(pojo, currentFieldName, result);
            }
        });
    }

    private Object textToValue(DictInfo dictInfo,
                               Object textFieldValue,
                               Class<?> targetElementType) {
        if (textFieldValue == null) return null;
        Class<?> textFieldType = textFieldValue.getClass();
        if (textFieldValue instanceof Collection) {
            String[] dictTexts = toStringArray(textFieldValue);
            Stream<?> dictValueStream = Arrays.stream(dictTexts)
                    .map(text -> lookupDictValueByText(dictInfo, text))
                    .map(value -> typeConverter.convert(value, targetElementType));
            return convertStreamToCollection(dictValueStream, textFieldType);
        }

        if (textFieldType.isArray()) {
            String[] dictTexts = toStringArray(textFieldValue);
            String[] dictValues = lookupDictValueBatch(dictInfo, dictTexts);
            int length = dictValues.length;
            Object valueArray = Array.newInstance(targetElementType, length);
            for (int i = 0; i < length; i++) {
                Object dictValue = typeConverter.convert(dictValues[i], targetElementType);
                Array.set(valueArray, i, dictValue);
            }
            return valueArray;
        }

        if (textFieldValue instanceof String) {
            if (delimiter != null && !delimiter.isEmpty()) {
                String textString = (String) textFieldValue;
                if (textString.contains(delimiter)) {
                    String[] splitTexts = textString.split(delimiter);
                    String[] dictValues = lookupDictValueBatch(dictInfo, splitTexts);
                    return String.join(delimiter, dictValues);
                }
            }
        }

        String dictValue = lookupDictValueByText(dictInfo, String.valueOf(textFieldValue));
        return typeConverter.convert(dictValue, targetElementType);
    }

    private String[] lookupDictValueBatch(DictInfo dictInfo, String[] dictTexts) {
        String[] values = new String[dictTexts.length];
        for (int i = 0; i < dictTexts.length; i++) {
            values[i] = lookupDictValueByText(dictInfo, dictTexts[i]);
        }
        return values;
    }

    private String lookupDictValueByText(DictInfo dictInfo, String dictText) {
        Map<String, String> dictData = dictInfo.getData();
        if (dictData == null) {
            throw new DictDataNotFoundException(
                    "Dictionary data not found for code : " + dictInfo.getCode());
        }

        String resolvedValue = dictData.get(dictText);
        if (resolvedValue == null) {
            if (dictData.containsValue(dictText)) {
                resolvedValue = dictText;
            }else {
                throw new DictDataNotFoundException(String.format(
                        "Dictionary text '%s' not found in dict [code=%s]",
                        dictText, dictInfo.getCode()));
            }
        }
        return resolvedValue;
    }

    private Class<?> getElementType(Field targetField) {
        Class<?> fieldType = targetField.getType();

        // 处理集合类型 - 获取泛型参数类型
        if (Collection.class.isAssignableFrom(fieldType)) {
            Type genericType = targetField.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
            // 如果无法获取泛型类型，返回Object.class
            return Object.class;
        }

        // 处理数组类型 - 获取数组元素类型
        if (fieldType.isArray()) {
            return fieldType.getComponentType();
        }

        // 处理普通对象类型 - 直接返回字段类型
        return fieldType;
    }

    private Map<String, DictInfo> invertDictInfo(Map<String, DictInfo> originalDictInfoMap) {
        Map<String, DictInfo> invertedMap = new HashMap<>();
        originalDictInfoMap.forEach((mapKey, original) -> {
            Map<String, String> invertedData = CommonUtils.invertMap(original.getData());
            DictInfo inverted = new DictInfo(original.getCode(), invertedData);
            invertedMap.put(mapKey, inverted);
        });
        return invertedMap;
    }

}
