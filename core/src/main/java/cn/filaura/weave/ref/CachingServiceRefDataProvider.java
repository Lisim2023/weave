package cn.filaura.weave.ref;

import cn.filaura.weave.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CachingServiceRefDataProvider implements ServiceRefDataProvider {

    private final RecordCache cache;
    private final DataFetcher fetcher;

    public CachingServiceRefDataProvider(RecordCache cache, DataFetcher fetcher) {
        this.cache = cache;
        this.fetcher = fetcher;
    }

    @Override
    public Map<String, ServiceResult> getReferenceData(Map<String, ServiceQuery> serviceQueryMap) {
        Map<String, ServiceResult> serviceResultMap = new HashMap<>(serviceQueryMap.size());
        serviceQueryMap.forEach((key, query) -> {
            Class<?> recordType = fetcher.getRecordTypeForQuery(query);
            List<String> ids = query.getIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            Map<String, Object> recordMap = new HashMap<>(
                    CommonUtils.calculateHashMapCapacity(ids.size()));
            // 1. 从缓存加载数据
            Map<String, ?> cachedData = cache.loadRecords(ids, recordType);
            recordMap.putAll(cachedData);

            // 2. 找出缓存缺失的key
            List<Object> missingIds = query.getIds().stream()
                    .filter(k -> !cachedData.containsKey(String.valueOf(k)))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                // 3. 从Service查询缺失数据
                Map<String, Object> dbData = fetcher.fetchReferenceData(query, missingIds);

                // 4. 缓存新查询到的数据
                cache.putRecords(dbData, recordType);

                // 5. 合并结果（包括缓存和Service查询结果）
                recordMap.putAll(dbData);
            }

            serviceResultMap.put(key, new ServiceResult(query, recordMap));
        });

        return serviceResultMap;
    }

}
