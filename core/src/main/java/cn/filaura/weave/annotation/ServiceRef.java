package cn.filaura.weave.annotation;


import java.lang.annotation.*;


/**
 * 服务引用注解 - 用于通过调用指定服务方法查询数据并映射到当前对象
 *
 * <p>通过调用指定的服务方法，将返回结果对象中的属性值分别映射到当前对象的对应属性上。</p>
 *
 * <p>示例：
 * <pre>{@code
 * // 通过用户服务获取用户名并映射到订单DTO的字段中
 * @ServiceRef(
 *     service = UserService.class,
 *     method = "getUsersByIds",
 *     resultKeyField = "id",
 *     mappings = {
 *         @Mapping(refField = "userId", from = "name", to = "userName"),
 *     }
 * )
 * public class OrderDTO {
 *     private Long userId;
 *     private String userName;
 * }
 * }</pre>
 */
@Repeatable(ServiceRefs.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceRef {

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
     * <p>该方法应该接收一个主键值List作为参数，返回对应的数据对象List。</p>
     * <p>如果未指定，将使用全局方法名，默认为"listByIds"，可在配置文件中自定义。
     *
     * @return 服务方法名称
     */
    String method() default "";

    /**
     * 结果对象中的主键属性名
     *
     * <p>用于通过外键值与结果对象中的该属性值进行匹配，
     * <p>如果未指定，将使用全局主键名，默认为"id"，可在配置文件中自定义。
     * <p>如果全局主键名也为空，将使用{@link ServiceRef#mappings()}中的第一个外键名。
     *
     * @return 主键属性名称，默认为空
     */
    String resultKeyField() default "";

    /**
     * 源对象与目标对象的属性映射关系
     *
     * <p>定义如何将服务返回结果对象中的属性值映射到当前对象的属性上。</p>
     *
     * @return 属性映射关系数组
     */
    Mapping[] mappings();

    /**
     * 是否忽略缺失的数据
     *
     * <p>当设置为true时，如果查询不到对应数据，不会抛出异常。</p>
     *
     * @return 是否忽略缺失数据，默认为false
     */
    boolean ignoreMissing() default false;

}
