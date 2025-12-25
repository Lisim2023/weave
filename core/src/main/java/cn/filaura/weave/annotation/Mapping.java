package cn.filaura.weave.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 源对象与目标对象的属性映射关系
 *
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    /**
     * 外键属性名
     * @return 属性名
     */
    String refField();

    /**
     * 原属性名或列名
     * @return 列名
     */
    String from();

    /**
     * 目标属性名
     * @return 属性名
     */
    String to();

}
