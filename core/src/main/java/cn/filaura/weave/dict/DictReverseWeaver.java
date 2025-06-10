package cn.filaura.weave.dict;



import cn.filaura.weave.MapUtils;
import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.exception.DictDataNotFoundException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class DictReverseWeaver extends AbstractDictWeaver {



    public DictReverseWeaver(BeanAccessor beanAccessor) {
        super(beanAccessor);
    }



    public void weave(Object beans, Map<String, DictInfo> data) {
        for (DictInfo dictInfo : data.values()) {
            dictInfo.setData(MapUtils.invertMap(dictInfo.getData()));
        }

        recursive(beans, bean -> {
            Field[] dictFields = DICT_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : dictFields) {
                String targetFieldName = getTargetFieldName(field);
                String targetFieldValue = getFieldValue(bean, targetFieldName);
                if (targetFieldValue == null || targetFieldValue.isEmpty()) {
                    continue;
                }

                String dictCode = getDictCode(field);
                String dictValue = getDictValue(targetFieldValue, dictCode, data);
                beanAccessor.setProperty(bean, field.getName(), dictValue);
            }
        });
    }

    private String getDictValue(String dictText, String code, Map<String, DictInfo> dictData) {
        DictInfo dictInfo = dictData.get(code);
        if (dictInfo == null) {
            throw new DictDataNotFoundException(String.format("Dictionary data not found with code '%s'", code));
        }

        if (dictText.contains(delimiter)) {
            String[] values = Arrays.stream(dictText.split(delimiter))
                    .map(text -> getValue(text, dictInfo))
                    .toArray(String[]::new);
            return String.join(delimiter, values);
        }

        return getValue(dictText, dictInfo);
    }

    private String getValue(String dictText, DictInfo dictInfo) {
        Map<String, String> data = dictInfo.getData();
        if (data == null) {
            throw new DictDataNotFoundException(String.format("Dictionary data not found with code '%s'", dictInfo.getCode()));
        }

        String value = data.get(dictText);
        if (value == null) {
            if (data.containsValue(dictText)) {
                value = dictText;
            }else {
                throw new DictDataNotFoundException(String.format("Dictionary entry not found for value '%s' with code '%s'", dictText, dictInfo.getCode()));
            }
        }
        return value;
    }

}
