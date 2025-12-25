package cn.filaura.weave.dict;

import cn.filaura.weave.CommonUtils;

import java.util.*;

public class CachingDictDataProvider implements DictDataProvider {

    private final DictCache cache;
    private final DataFetcher fetcher;

    public CachingDictDataProvider(DictCache cache, DataFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = cache;
    }

    @Override
    public Map<String, DictInfo> getDictData(List<String> dictCodes) {
        Map<String, DictInfo> result = new HashMap<>(
                CommonUtils.calculateHashMapCapacity(dictCodes.size()));

        Map<String, DictInfo> cacheDict = cache.loadDict(dictCodes);
        List<String> missingCodes = new ArrayList<>();
        for (String dictCode : dictCodes) {
            DictInfo dictInfo = cacheDict.get(dictCode);
            if (dictInfo == null) {
                missingCodes.add(dictCode);
            }else {
                result.put(dictCode, dictInfo);
            }
        }

        if (!missingCodes.isEmpty()) {
            Map<String, DictInfo> newData = fetcher.fetchDictData(missingCodes);
            cache.putDict(newData);
            result.putAll(newData);
        }

        return result;
    }
}
