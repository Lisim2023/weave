package cn.filaura.weave.annotation;

import java.lang.annotation.*;


/**
 * 对象嵌入注解 - 用于通过服务方法调用查询数据并将完整对象嵌入到当前对象
 *
 * <p>通过调用指定的服务方法，将匹配的完整结果对象嵌入到当前字段中。</p>
 *
 * <p>示例：
 * <pre>{@code
 * // 通过用户服务获取完整的用户对象并嵌入到订单DTO中
 * public class OrderDTO {
 *
 *     private String userId;
 *
 *     @RecordEmbed(
 *          service = UserService.class,
 *          method = "getUsersByIds",
 *          resultKeyField = "id",
 *          refField = "userId"
 *      )
 *     private User user;
 *
 * }
 * }</pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordEmbed {

    /**
     * 服务（接口）类型
     *
     * <p>用于指定查询数据的服务接口类型。</p>
     *
     * @return 服务类型
     */
    Class<?> service();

    /**
     * 服务方法名
     *
     * <p>该方法应该接收一个主键值List作为参数，并返回数据对象List。</p>
     * <p>如果未指定，将使用全局方法名，默认为"listByIds"，可在配置文件中自定义。
     *
     * @return 服务方法名称
     */
    String method() default "";

    /**
     * 结果对象中的主键属性名
     *
     * <p>用于与{@link RecordEmbed#refField()}指定的属性的值进行匹配，
     * <p>如果未指定，将使用全局主键名，默认为"id"，可在配置文件中自定义。
     * <p>如果全局主键名也被置空，将使用{@link RecordEmbed#refField()}指定的名称。
     *
     * @return 主键属性名称，默认为空
     */
    String resultKeyField() default "";

    /**
     * 当前对象中的外键名
     * <p>该字段的值将作为参数传递给服务方法，用于查询关联实体。</p>
     * <p>如果未指定，自动推导为当前标注的属性名 + "Id"，可在配置文件中自定义后缀。
     * @return 外键属性名称，默认为空
     */
    String refField() default "";

    /**
     * 是否忽略缺失的数据
     *
     * <p>当设置为true时，如果查询不到对应数据，不会抛出异常。</p>
     *
     * @return 是否忽略缺失数据，默认为false
     */
    boolean ignoreMissing() default false;

}
