package cn.filaura.weave.dict;

import cn.filaura.weave.AbstractWeaver;
import cn.filaura.weave.AnnotatedFieldExtractor;
import cn.filaura.weave.annotation.Dict;

import java.lang.reflect.Field;
import java.util.*;


public abstract class AbstractDictWeaver extends AbstractWeaver {

    protected static final AnnotatedFieldExtractor DICT_FIELD_EXTRACTOR
            = new AnnotatedFieldExtractor(Dict.class);

    /** 分隔符 */
    protected static String delimiter = ",";
    /** 属性名后缀 */
    protected static String textFieldSuffix = "Text";

    public static String buildMapKey(Dict dict) {
        return dict.code();
    }

    public List<String> collectDictCodes(Object pojos) {
        Set<String> dictCodes = new HashSet<>();
        Set<Class<?>> classes = gatherClassTypes(pojos);
        for (Class<?> clazz : classes) {
            for (Field field : DICT_FIELD_EXTRACTOR.getAnnotatedFields(clazz)) {
                Dict dict = field.getAnnotation(Dict.class);
                dictCodes.add(dict.code());
            }
        }
        return new ArrayList<>(dictCodes);
    }

    protected String getTextFieldName(Dict dict, String valueFieldName) {
        String target = dict.targetField();
        if (target == null || target.isEmpty()) {
            return valueFieldName + textFieldSuffix;
        }
        return target;
    }



    public static String getDelimiter() {
        return delimiter;
    }

    public static void setDelimiter(String delimiter) {
        AbstractDictWeaver.delimiter = delimiter;
    }

    public static String getTextFieldSuffix() {
        return textFieldSuffix;
    }

    public static void setTextFieldSuffix(String textFieldSuffix) {
        AbstractDictWeaver.textFieldSuffix = textFieldSuffix;
    }
}
