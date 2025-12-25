package cn.filaura.weave.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 数据字典注解
 *
 * <p>用于完成字典值与字典显示文本之间的双向转换。
 * <p>示例：
 * <pre>{@code
 * public class User {
 *     @Dict(code = "user_status")
 *     private Integer status;
 *     private String statusText;
 * }
 * }</pre>
 * <p>在数据展示时，可将 {@code status} 属性的值转换为对应的字典文本并赋值给 {@code statusText} 属性；
 * <p>在数据导入时，可反向将 {@code statusText} 属性中的字典文本转换为字典值并赋值到 {@code status} 属性。
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
     * <p>指定另一属性用于保存字典描述文本。
     * <p>如果未指定，自动推导为当前标注的属性名 + "Text"，可在配置文件中自定义后缀。
     * @return 目标属性名称
     */
    String targetField() default "";

}
