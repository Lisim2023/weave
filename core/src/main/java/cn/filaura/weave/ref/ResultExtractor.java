package cn.filaura.weave.ref;


/**
 * 用于对服务方法的返回结果进行解包
 * <p>若方法返回值被封装为通用结果对象，可通过此方法自定义解包方式。
 * <p>示例：
 * <pre>{@code
 * public class ResultExtractorExample implements ResultExtractor {
 *
 *     @Override
 *     public Object extract(Object result) {
 *         if (result instanceof Result) {
 *             return ((Result) result).getData();
 *         }
 *         return result;
 *     }
 * }
 * }
 * </pre>
 */
@FunctionalInterface
public interface ResultExtractor {

    Object extract(Object result);
}
