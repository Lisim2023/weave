package cn.filaura.weave.dict;



import cn.filaura.weave.MapUtils;

import java.util.*;

/**
 * 带缓存的字典数据提供
 * <p>缓存优先，缓存未命中再查询数据源，缓存从数据源获取的数据
 *
 * @see DictDataProvider
 */
public class CacheFirstDictDataProvider implements DictDataProvider {

    private final DictDataSource dictDataSource;
    private final DictDataCache dictDataCache;



    /**
     * @param dictDataSource 字典数据源
     * @param dictDataCache 字典缓存
     */
    public CacheFirstDictDataProvider(DictDataSource dictDataSource, DictDataCache dictDataCache) {
        this.dictDataSource = dictDataSource;
        this.dictDataCache = dictDataCache;
    }



    @Override
    public Map<String, DictInfo> getDictData(Collection<String> dictCodes) {
        Map<String, DictInfo> result = new HashMap<>(MapUtils.calculateHashMapCapacity(dictCodes.size()));
        List<DictInfo> cacheData = dictDataCache.loadDict(dictCodes);
        result.putAll(DictInfo.toCodeMap(cacheData));

        List<String> missingCodes = new ArrayList<>();
        for (String dictCode : dictCodes) {
            if (result.get(dictCode) == null) {
                missingCodes.add(dictCode);
            }
        }

        if (!missingCodes.isEmpty()) {
            List<DictInfo> newData = dictDataSource.queryDictData(missingCodes);
            dictDataCache.cacheDict(newData);
            result.putAll(DictInfo.toCodeMap(newData));
        }

        return result;
    }

}
