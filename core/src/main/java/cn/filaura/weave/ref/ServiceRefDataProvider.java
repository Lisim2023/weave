package cn.filaura.weave.ref;


import cn.filaura.weave.annotation.RecordEmbed;
import cn.filaura.weave.annotation.ServiceRef;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 服务引用数据提供者接口
 * <p>为 {@link ServiceRef}和{@link RecordEmbed} 注解提供数据。</p>
 *
 * <p>自动配置规则：
 * <p>将自定义实现注册为Spring Bean，会优先使用。
 * <p>否则根据是否启用缓存，选用下列实现之一：
 * <ul>
 *   <li>{@link DirectServiceRefDataProvider} - 直接服务调用实现</li>
 *   <li>{@link CachingServiceRefDataProvider} - 带缓存的服务调用实现</li>
 * </ul>
 * @see ServiceRef
 * @see RecordEmbed
 */
 public interface ServiceRefDataProvider {

    /**
     * 获取引用数据
     *
     * @param serviceQueryMap 查询条件
     * @return 查询结果映射，键与参数的键一致，值为对应的查询结果
     */
    Map<String, ServiceResult> getReferenceData(Map<String, ServiceQuery> serviceQueryMap);



    /**
     * 数据获取器接口
     * <p>实现此接口并注册为 Spring Bean，系统会自动发现并注入到 {@link ServiceRefDataProvider} 中。
     */
    interface DataFetcher {

        /**
         * 根据服务查询条件和ID列表获取引用数据
         *
         * @param serviceQuery 查询条件封装对象，包含服务和方法等信息
         * @param ids 需要查询的数据ID列表
         * @return 数据映射，键为ID的字符串表示，值为查询结果对象
         */
        Map<String, Object> fetchReferenceData(ServiceQuery serviceQuery, Collection<Object> ids);

        /**
         * 获取 {@code ServiceQuery} 中指定的方法其返回值的元素类型。
         * <p>该方法返回值应为 {@link Collection}（通常为 {@link List}），此方法返回其泛型的元素类型。</p>
         *
         * @param serviceQuery 查询条件封装对象
         * @return 方法返回值的元素类型，通常为具体的DTO或领域对象类型
         */
        Class<?> getRecordTypeForQuery(ServiceQuery serviceQuery);

    }

}
