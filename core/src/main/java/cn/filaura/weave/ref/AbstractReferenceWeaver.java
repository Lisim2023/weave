package cn.filaura.weave.ref;

import cn.filaura.weave.AbstractWeaver;


import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import java.util.function.Consumer;
import java.util.function.Function;

public class AbstractReferenceWeaver extends AbstractWeaver {

    /** 全局主键名 */
    protected static String globalPrimaryKey = "id";
    /** 全局方法名 */
    protected static String globalMethodName = "listByIds";
    /** 全局外键后缀 */
    protected static String globalForeignKeySuffix = "Id";


    protected static <Q> void removeIncompleteRefQuery(Map<String, Q> refQueryMap,
                                                       Function<Q, Collection<?>> valueGetter) {
        Iterator<Map.Entry<String, Q>> iterator = refQueryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Q> entry = iterator.next();
            Q q = entry.getValue();
            Collection<?> values = valueGetter.apply(q);

            // 检查 ids 是否为 null 或空集合
            if (values == null || values.isEmpty()) {
                iterator.remove();  // 安全移除当前条目
            }
        }
    }

    protected static <A extends Annotation, C extends Annotation> void traverseAnnotations(
            Class<?> clazz,
            Class<A> singleAnnotationType,
            Class<C> containerAnnotationType,
            Function<C, A[]> containerExtractor,
            Consumer<A> annotationHandler) {

        C containerAnnotation = clazz.getAnnotation(containerAnnotationType);
        if (containerAnnotation != null) {
            A[] annotations = containerExtractor.apply(containerAnnotation);
            for (A singleAnnotation : annotations) {
                annotationHandler.accept(singleAnnotation);
            }
        } else {
            A singleAnnotation = clazz.getAnnotation(singleAnnotationType);
            if (singleAnnotation != null) {
                annotationHandler.accept(singleAnnotation);
            }
        }
    }


    public static String getGlobalPrimaryKey() {
        return globalPrimaryKey;
    }

    public static void setGlobalPrimaryKey(String globalPrimaryKey) {
        AbstractReferenceWeaver.globalPrimaryKey = globalPrimaryKey;
    }

    public static String getGlobalForeignKeySuffix() {
        return globalForeignKeySuffix;
    }

    public static void setGlobalForeignKeySuffix(String globalForeignKeySuffix) {
        AbstractReferenceWeaver.globalForeignKeySuffix = globalForeignKeySuffix;
    }

    public static String getGlobalMethodName() {
        return globalMethodName;
    }

    public static void setGlobalMethodName(String globalMethodName) {
        AbstractReferenceWeaver.globalMethodName = globalMethodName;
    }

}
