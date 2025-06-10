package cn.filaura.weave.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 引用注解
 *
 * <p>用于完成跨表数据引用。
 * <p>标注于对象属性，根据属性值从关联表引用数据。
 * <p>注意：需要至少指定一种列名与属性名的映射方式。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ref {

    /**
     * 表名
     * @return 表名
     */
    String table();

    /**
     * 主键名（可选）。
     *
     * <p>也可以是主键以外的其他唯一键
     * @return 主键名
     */
    String key() default "";

    /**
     * 要引用的列与目标属性的绑定规则数组（可选）。
     *
     * @return 列绑定规则数组
     * @see ColumnBinding
     */
    ColumnBinding[] bindings() default {};

    /**
     * 要引用的列名（可选）。
     *
     * <p>仅指定列名，列名对应的属性名根据规则自动生成（原属性名+中缀+列名）。
     * @return 列名称数组
     */
    String[] columns() default {};

    /**
     * 指定目标JavaBean（可选）。
     *
     * <p>指定一个属性，引用数据将被注入到该属性的对象中
     * <p>当此属性被设置时，会尝试将获取到的所有列的数据映射到目标对象的同名属性
     * @return 目标JavaBean的属性名
     */
    String targetBean() default "";

}

