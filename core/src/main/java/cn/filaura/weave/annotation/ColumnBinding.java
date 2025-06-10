package cn.filaura.weave.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>用于{@link Ref#bindings()}参数，用来指定列与属性的映射关系。
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnBinding {

    /**
     * 数据表中的列名
     * @return 列名
     */
    String column();

    /**
     * 对象的属性名
     * @return 属性名
     */
    String targetField();
}
