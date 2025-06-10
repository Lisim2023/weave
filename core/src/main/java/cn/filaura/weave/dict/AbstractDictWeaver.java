package cn.filaura.weave.dict;

import cn.filaura.weave.AbstractWeaver;
import cn.filaura.weave.AnnotatedFieldExtractor;
import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.annotation.Dict;

import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractDictWeaver extends AbstractWeaver {

    protected static final AnnotatedFieldExtractor DICT_FIELD_EXTRACTOR = new AnnotatedFieldExtractor(Dict.class);

    /** 分隔符 */
    protected static String delimiter = ",";

    /** 属性名后缀 */
    protected static String fieldNameSuffix = "Text";



    public AbstractDictWeaver(BeanAccessor beanAccessor) {
        super(beanAccessor);
    }



    public String getTargetFieldName(Field dictField) {
        Dict dict = dictField.getAnnotation(Dict.class);
        String target = dict.targetField();
        if (target == null || target.isEmpty()) {
            return dictField.getName() + fieldNameSuffix;
        }
        return target;
    }

    public String getDictCode(Field dictField) {
        if (dictField == null) {
            return null;
        }
        return dictField.getAnnotation(Dict.class).code();
    }

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



    public static String getDelimiter() {
        return delimiter;
    }

    public static void setDelimiter(String delimiter) {
        AbstractDictWeaver.delimiter = delimiter;
    }

    public static String getFieldNameSuffix() {
        return fieldNameSuffix;
    }

    public static void setFieldNameSuffix(String fieldNameSuffix) {
        AbstractDictWeaver.fieldNameSuffix = fieldNameSuffix;
    }
}
