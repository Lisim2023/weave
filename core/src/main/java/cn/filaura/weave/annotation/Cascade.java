package cn.filaura.weave.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 级联注解
 * <p>用于处理对象间的递归或级联关系
 * <p>标记在需要递归处理的属性上即可
 * <p>示例：
 * <pre>{@code
 * public class Menu {
 *     @Cascade
 *     private List<Menu> children;  // 自动递归处理子菜单
 * }
 * }</pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cascade {
}
