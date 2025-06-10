package cn.filaura.weave.dict;



import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.exception.DictDataNotFoundException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class DictWeaver extends AbstractDictWeaver {



    public DictWeaver(BeanAccessor beanAccessor) {
        super(beanAccessor);
    }



    public void weave(Object beans, Map<String, DictInfo> dataMap) {
        recursive(beans, bean -> {
            Field[] dictFields = DICT_FIELD_EXTRACTOR.getAnnotatedFields(bean.getClass());
            for (Field field : dictFields) {
                String dictFieldValue = getFieldValue(bean, field.getName());
                if (dictFieldValue == null || dictFieldValue.isEmpty()) {
                    continue;
                }

                String dictCode = getDictCode(field);
                String targetFieldName = getTargetFieldName(field);
                String dictText = getDictText(dictFieldValue, dictCode, dataMap);
                beanAccessor.setProperty(bean, targetFieldName, dictText);
            }
        });
    }

    private String getDictText(String dictValue, String code, Map<String, DictInfo> dictData) {
        DictInfo dictInfo = dictData.get(code);
        if (dictInfo == null) {
            throw new DictDataNotFoundException(String.format("Dictionary entry not found with code '%s'", code));
        }

        if (dictValue.contains(delimiter)) {
            String[] texts = Arrays.stream(dictValue.split(delimiter))
                    .map(value -> getText(value, dictInfo))
                    .toArray(String[]::new);
            return String.join(delimiter, texts);
        }

        return getText(dictValue, dictInfo);
    }

    private String getText(String dictValue, DictInfo dictInfo) {
        Map<String, String> data = dictInfo.getData();
        if (data == null) {
            throw new DictDataNotFoundException(String.format("Dictionary entry not found with code '%s'", dictInfo.getCode()));
        }

        String dictText = data.get(dictValue);
        if (dictText == null) {
            throw new DictDataNotFoundException(String.format("Dictionary entry not found for value '%s' with code '%s'", dictValue, dictInfo.getCode()));
        }
        return dictText;
    }

}
