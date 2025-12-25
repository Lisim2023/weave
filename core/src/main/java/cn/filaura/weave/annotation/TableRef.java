package cn.filaura.weave.annotation;


import java.lang.annotation.*;


/**
 * 数据库映射注解 - 用于从数据库表中查询数据并映射到当前对象
 *
 * <p>通过指定数据库表名和外键关系，将查询结果中的列值分别映射到当前对象的属性上。</p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 将用户表的姓名和邮箱映射到订单DTO中
 * @TableRef(
 *     table = "sys_user",
 *     keyColumn = "id",
 *     mappings = {
 *         @Mapping(refField = "userId", from = "name", to = "userName"),
 *         @Mapping(refField = "userId", from = "email", to = "userEmail")
 *     }
 * )
 * public class OrderDTO {
 *     private String userId;
 *     private String userName;
 *     private String userEmail;
 * }
 * }</pre>
 */
@Repeatable(TableRefs.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRef {

    /**
     * 数据库表名
     *
     * @return 要查询的数据库表名称
     */
    String table();

    /**
     * 目标表中的关键列名（通常是主键列）
     *
     * <p>该列名将用于与外键字段值进行匹配查询。
     * <p>如果未指定，将使用全局主键名，默认为"id"，可在配置文件中自定义。
     * <p>如果全局主键名也为空，将使用{@link TableRef#mappings()}中的第一个外键名（转换为蛇形命名）。
     *
     * @return 关键列名称，默认为空
     */
    String keyColumn() default "";

    /**
     * 数据库表中的列名与当前对象属性名的映射关系
     *
     * <p>定义如何将查询结果中的列值映射到当前对象的属性上</p>
     *
     * @return 映射关系数组
     */
    Mapping[] mappings();

    /**
     * 是否忽略缺失的数据
     *
     * <p>当设置为true时，如果查询不到对应的数据，会直接跳过，不会抛出异常。</p>
     *
     * @return 是否忽略缺失数据，默认为false
     */
    boolean ignoreMissing() default false;

}
