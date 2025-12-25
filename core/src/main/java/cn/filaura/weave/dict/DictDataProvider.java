package cn.filaura.weave.dict;


import cn.filaura.weave.annotation.Dict;

import java.util.List;
import java.util.Map;

/**
 * 字典数据提供器接口
 *
 * <p>为 {@link Dict} 注解提供字典数据。</p>
 *
 * @see Dict
 */
public interface DictDataProvider {

    /**
     * 获取指定字典编码对应的字典数据
     * @param dictCodes 字典编码列表
     * @return 字典信息，以字典编码作为key
     */
    Map<String, DictInfo> getDictData(List<String> dictCodes);



    /**
     * 字典数据获取器接口
     * <p>当检测到此接口实现时，会自动将其配置到 {@code CachingDictDataProvider} 实现：</p>
     * <ul>
     *   <li>若配置 {@code weave.cache.dict-cache-enabled=false}（默认）：使用本地内存缓存</li>
     *   <li>若配置 {@code weave.cache.dict-cache-enabled=true}：使用 Redis 缓存</li>
     * </ul>
     *
     * @see CachingDictDataProvider
     */
    interface DataFetcher {

        /**
         * 从数据源获取字典数据
         * @param dictCodes 字典编码列表
         * @return 字典信息，以字典编码作为key
         */
        Map<String, DictInfo> fetchDictData(List<String> dictCodes);
    }
}
