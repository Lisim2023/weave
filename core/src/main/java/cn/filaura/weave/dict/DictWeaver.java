package cn.filaura.weave.dict;



import cn.filaura.weave.AbstractWeaver;
import cn.filaura.weave.AnnotatedFieldExtractor;
import cn.filaura.weave.MapUtils;
import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.exception.DictDataNotFoundException;
import cn.filaura.weave.type.ConvertUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DictWeaver extends AbstractWeaver {

    protected static final AnnotatedFieldExtractor DICT_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Dict.class);

    /** 分隔符 */
    protected String delimiter = ",";

    /** 属性名后缀 */
    protected String fieldNameSuffix = "Text";



    public Set<String> collectDictCodes(Object beans) {
        Set<String> dictCodes = new LinkedHashSet<>();
        Set<Class<?>> classSet = gatherClassTypes(beans);
        for (Class<?> clazz : classSet) {
            for (Field field : DICT_FIELD_EXTRACTOR.getAnnotatedFields(clazz)) {
                String dictCode = getDictCode(field);
                if (dictCode != null) {
                    dictCodes.add(dictCode);
                }
            }
        }
        return dictCodes;
    }

    public void populateDictText(Object beans, Map<String, DictInfo> dataMap) {
        recursive(beans, bean -> {
            Field[] dictFields = DICT_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : dictFields) {
                String dictCode = getDictCode(field);
                DictInfo dictInfo = getDictInfoOrThrow(dataMap, dictCode);

                String dictFieldValue = getFieldValue(bean, field.getName());
                if (dictFieldValue == null || dictFieldValue.isEmpty()) {
                    continue;
                }

                String targetFieldName = getTargetFieldName(field);
                String dictText = translate(dictFieldValue, dictInfo, this::resolveDictText);

                beanAccessor.setProperty(bean, targetFieldName, dictText);
            }
        });
    }

    public void populateDictValue(Object beans, Map<String, DictInfo> dataMap) {
        Map<String, DictInfo> reversedMap = createReversedDictInfoMap(dataMap);

        recursive(beans, bean -> {
            Field[] dictFields = DICT_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : dictFields) {
                String dictCode = getDictCode(field);
                DictInfo dictInfo = getDictInfoOrThrow(reversedMap, dictCode);

                String targetFieldName = getTargetFieldName(field);
                String targetFieldValue = getFieldValue(bean, targetFieldName);
                if (targetFieldValue == null || targetFieldValue.isEmpty()) {
                    continue;
                }

                String dictValue = translate(targetFieldValue, dictInfo, this::resolveDictValue);
                Class<?> propertyType = beanAccessor.getPropertyType(bean, field.getName());
                Object convert = ConvertUtil.convert(dictValue, propertyType);
                beanAccessor.setProperty(bean, field.getName(), convert);
            }
        });
    }

    private String translate(String inputValue, DictInfo dictInfo, BiFunction<DictInfo, String, String> resolver) {
        if (inputValue.contains(delimiter)) {
            return Arrays.stream(inputValue.split(delimiter))
                    .map(value -> resolver.apply(dictInfo, value))
                    .collect(Collectors.joining(delimiter));
        }

        return resolver.apply(dictInfo, inputValue);
    }

    private Map<String, DictInfo> createReversedDictInfoMap(Map<String, DictInfo> dictInfoMap) {
        Map<String, DictInfo> reversedMap = new HashMap<>();
        dictInfoMap.forEach((code, dictInfo) -> {
            Map<String, String> reversedData = MapUtils.invertMap(dictInfo.getData());
            reversedMap.put(code, new DictInfo(code, reversedData));
        });
        return reversedMap;
    }

    private DictInfo getDictInfoOrThrow(Map<String, DictInfo> dictInfoMap, String dictCode) {
        DictInfo dictInfo = dictInfoMap.get(dictCode);
        if (dictInfo == null) {
            throw new DictDataNotFoundException(
                    "Dictionary metadata missing for code: " + dictCode);
        }
        return dictInfo;
    }

    private String resolveDictText(DictInfo dictInfo, String dictValue) {
        Map<String, String> data = dictInfo.getData();
        if (data == null) {
            throw new DictDataNotFoundException(
                    "Dictionary data not found for code : " + dictInfo.getCode());
        }

        String dictText = data.get(dictValue);
        if (dictText == null) {
            throw new DictDataNotFoundException(String.format(
                    "Dictionary value '%s' not found in dict [code=%s]",
                    dictValue, dictInfo.getCode()));
        }
        return dictText;
    }

    private String resolveDictValue(DictInfo dictInfo, String dictText) {
        Map<String, String> data = dictInfo.getData();
        if (data == null) {
            throw new DictDataNotFoundException(
                    "Dictionary data not found for code : " + dictInfo.getCode());
        }

        String value = data.get(dictText);
        if (value == null) {
            if (data.containsValue(dictText)) {
                value = dictText;
            }else {
                throw new DictDataNotFoundException(String.format(
                        "Dictionary text '%s' not found in dict [code=%s]",
                        dictText, dictInfo.getCode()));
            }
        }
        return value;
    }

    private String getTargetFieldName(Field dictField) {
        Dict dict = dictField.getAnnotation(Dict.class);
        String target = dict.property();
        if (target == null || target.isEmpty()) {
            return dictField.getName() + fieldNameSuffix;
        }
        return target;
    }

    private String getDictCode(Field dictField) {
        if (dictField == null) {
            return null;
        }
        return dictField.getAnnotation(Dict.class).code();
    }



    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getFieldNameSuffix() {
        return fieldNameSuffix;
    }

    public void setFieldNameSuffix(String fieldNameSuffix) {
        this.fieldNameSuffix = fieldNameSuffix;
    }

}
