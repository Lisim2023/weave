package cn.filaura.weave.ref;


import cn.filaura.weave.annotation.TableRef;

import java.util.Collection;
import java.util.Map;

/**
 * 表引用数据提供器接口
 * <p>为 {@link TableRef} 注解提供数据。</p>
 *
 * <p>自动配置规则：
 * <p>将自定义实现注册为Spring Bean，会优先使用。
 * <p>否则根据是否启用缓存，选用下列实现之一：
 * <ul>
 *   <li>{@link DirectTableRefDataProvider} - 直接查询，不缓存</li>
 *   <li>{@link CachingTableRefDataProvider} - 带缓存的查询</li>
 * </ul>
 *
 * @see TableRef
 * @see TableRefHelper
 */
public interface TableRefDataProvider {

    /**
     * 获取引用数据
     *
     * @param dbQueryMap 查询条件
     * @return 查询结果映射，键与参数的键一致，值为对应的查询结果
     */
    Map<String, TableResult> getReferenceData(Map<String, TableQuery> dbQueryMap);



    /**
     * 数据获取器接口
     * <p>实现此接口并注册为 Spring Bean，系统会自动发现并注入到 {@link TableRefDataProvider} 中。
     */
    interface DataFetcher {

        /**
         * 根据查询条件和ID列表获取引用数据
         *
         * @param tableQuery 查询条件封装对象
         * @param ids 需要查询的数据ID集合
         * @return 数据映射，其中键为ID的字符串形式，值为对应数据记录的字段映射（字段名 → 字段值）
         */
        Map<String, Map<String, Object>> fetchReferenceData(TableQuery tableQuery, Collection<Object> ids);
    }

}
