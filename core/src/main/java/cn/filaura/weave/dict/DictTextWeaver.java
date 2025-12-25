package cn.filaura.weave.dict;

import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.exception.DictDataNotFoundException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;


public class DictTextWeaver extends AbstractDictWeaver {

    public void weaveDictText(Object pojos, Map<String, DictInfo> dictInfoMap) {
        recursive(pojos, pojo -> {
            Field[] dictFields = DICT_FIELD_EXTRACTOR.getAnnotatedFields(pojo.getClass());
            for (Field field : dictFields) {
                String fieldName = field.getName();
                Object currentFieldValue = pojoAccessor.getPropertyValue(pojo, fieldName);
                if (currentFieldValue == null) {
                    continue;
                }

                Dict dict = field.getAnnotation(Dict.class);
                String mapKey = buildMapKey(dict);
                DictInfo dictInfo = dictInfoMap.get(mapKey);
                if (dictInfo == null) {
                    throw new DictDataNotFoundException("DictInfo not found for key: " + mapKey);
                }

                String textFieldName = getTextFieldName(dict, fieldName);
                Object result = valueToText(currentFieldValue, dictInfo);
                writeRawProperty(pojo, textFieldName, result);
            }
        });
    }

    private Object valueToText(Object fieldValue, DictInfo dictInfo) {
        if (fieldValue == null) return null;
        Class<?> valueType = fieldValue.getClass();
        if (fieldValue instanceof Collection) {
            String[] dictValues = toStringArray(fieldValue);
            Stream<String> dictTextStream = Arrays.stream(dictValues)
                    .map(value -> lookupDictTextByValue(dictInfo, value));
            return convertStreamToCollection(dictTextStream, valueType);
        }

        if (valueType.isArray()) {
            String[] dictValues = toStringArray(fieldValue);
            return lookupDictTextBatch(dictInfo, dictValues);
        }

        if (fieldValue instanceof String) {
            if (delimiter != null && !delimiter.isEmpty()) {
                String valueString = (String) fieldValue;
                if (valueString.contains(delimiter)) {
                    String[] splitValues = valueString.split(delimiter);
                    String[] dictTexts = lookupDictTextBatch(dictInfo, splitValues);
                    return String.join(delimiter, dictTexts);
                }
            }
        }

        return lookupDictTextByValue(dictInfo, String.valueOf(fieldValue));
    }

    private String[] lookupDictTextBatch(DictInfo dictInfo, String[] dictValues) {
        String[] texts = new String[dictValues.length];
        for (int i = 0; i < dictValues.length; i++) {
            texts[i] = lookupDictTextByValue(dictInfo, dictValues[i]);
        }
        return texts;
    }

    private String lookupDictTextByValue(DictInfo dictInfo, String dictValue) {
        Map<String, String> dictData = dictInfo.getData();
        if (dictData == null) {
            throw new DictDataNotFoundException(
                    "Dictionary data not found for code : " + dictInfo.getCode());
        }

        String resolvedText = dictData.get(dictValue);
        if (resolvedText == null) {
            throw new DictDataNotFoundException(String.format(
                    "Dictionary value '%s' not found in dict [code=%s]",
                    dictValue, dictInfo.getCode()));
        }
        return resolvedText;
    }

}
