package cn.filaura.weave;


/**
 * 允许对象动态扩展属性的接口。
 *
 * <p>实现了此接口的对象，允许向其写入未预先定义的动态属性。
 *
 * <p>示例：
 * <pre>{@code
 * // 用内置Map的方式实现动态扩展属性
 * public class DynamicEntity implements PropertyExtensible {
 *     private Map<String, Object> extendedProperties = new HashMap<>();
 *
 *     @Override
 *     public void extendProperty(String name, Object value) {
 *         extendedProperties.put(name, value);
 *     }
 * }
 * }</pre>
 *
 */
public interface PropertyExtensible {

    /**
     * 动态扩展一个属性。
     *
     * <p>该方法通常在标准属性访问机制失败后被调用。
     *
     * @param name  属性名称，不应为 {@code null}
     * @param value 属性值，可以为任意对象（包括 {@code null}）
     */
    void extendProperty(String name, Object value);

}
