package cn.filaura.weave.ref;

import cn.filaura.weave.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CachingTableRefDataProvider implements TableRefDataProvider {

    private final ColumnProjectionCache cache;
    private final DataFetcher fetcher;

    public CachingTableRefDataProvider(ColumnProjectionCache cache, DataFetcher fetcher) {
        this.cache = cache;
        this.fetcher = fetcher;
    }

    @Override
    public Map<String, TableResult> getReferenceData(Map<String, TableQuery> dbQueryMap) {
        Map<String, TableResult> result = new HashMap<>();
        dbQueryMap.forEach((key, query) -> {
            Map<String, Map<String, Object>> records = queryData(query);
            result.put(key, new TableResult(query, records));
        });

        return result;
    }

    private Map<String, Map<String, Object>> queryData(TableQuery tableQuery) {
        Set<String> requiredColumns = tableQuery.getColumns();
        Set<Object> idSet = tableQuery.getIds();
        // 预分配容量，避免扩容开销
        Map<String, Map<String, Object>> result = new HashMap<>(
                CommonUtils.calculateHashMapCapacity(idSet.size()));

        List<String> idList = new ArrayList<>(idSet.size());
        for (Object id : idSet) {
            idList.add(String.valueOf(id));
        }

        // 从缓存加载数据
        Map<String, Map<String, Object>> cacheData = cache.loadProjections(
                tableQuery.getTable(), tableQuery.getKeyColumn(), idList);

        // 过滤出包含所有必需列的缓存数据
        Map<String, Map<String, Object>> validCacheData = cacheData.entrySet().stream()
                .filter(entry ->
                        containsAllColumns(entry.getValue(), requiredColumns))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 添加有效缓存数据到结果
        result.putAll(validCacheData);

        List<Object> missingIds = idSet.stream()
                .filter(id -> validCacheData.get(String.valueOf(id)) == null)
                .collect(Collectors.toList());

        // 获取缺失的数据
        if (!missingIds.isEmpty()) {
            Map<String, Map<String, Object>> dbData =
                    fetcher.fetchReferenceData(tableQuery, missingIds);

            // 缓存新获取的数据
            cache.putProjections(tableQuery.getTable(), tableQuery.getKeyColumn(), dbData);

            // 添加数据库数据到结果
            result.putAll(dbData);
        }

        return result;
    }

    private boolean containsAllColumns(Map<String, Object> record, Set<String> requiredColumns) {
        // 如果需要的列数大于缓存记录的列数，直接返回false
        if (requiredColumns.size() > record.size()) {
            return false;
        }

        // 检查是否包含所有需要的列
        return record.keySet().containsAll(requiredColumns);
    }
}
