package cn.filaura.weave.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 数据字典注解
 *
 * <p>用于实现字典值与字典显示文本的双向转换。
 * <p>典型应用场景：将数据库存储的字典值（如："0"/"1"）转换为对应的描述文本（如："男"/"女"），用于展示，
 * 或者反向将字典文本转换为字典值，用于数据导入。
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 字典编码
     *
     * <p>用于标识字典类型。例如：gender 表示性别字典。
     * @return 字典编码
     */
    String code();

    /**
     * 目标属性名（可选）
     *
     * <p>指定另一属性用于存储字典描述文本。如果未指定，则根据规则生成（原属性名+后缀）。
     * @return 目标属性名称
     */
    String property() default "";

}
