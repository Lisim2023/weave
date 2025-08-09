package cn.filaura.weave.ref;


import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.MapUtils;
import cn.filaura.weave.PropertyDescriptorBeanAccessor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 带缓存的引用数据提供
 * <p>缓存优先，缓存未命中再查询数据源，缓存从数据源获取的数据
 *
 * @see RefDataProvider
 */
public class CacheFirstRefDataProvider implements RefDataProvider {

    private final BeanAccessor beanAccessor = new PropertyDescriptorBeanAccessor();

    private RefDataSource refDataSource;
    private RefDataCache refDataCache;



    /**
     * @param refDataSource 引用数据源
     * @param refDataCache 引用缓存
     */
    public CacheFirstRefDataProvider(RefDataSource refDataSource, RefDataCache refDataCache) {
        this.refDataSource = refDataSource;
        this.refDataCache = refDataCache;
    }



    @Override
    public void getRefData(Collection<RefInfo> refInfos) {
        if (refInfos == null || refInfos.isEmpty()) {
            return;
        }

        for (RefInfo refInfo : refInfos) {
            processSingleRefInfo(refInfo);
        }
    }

    private void processSingleRefInfo(RefInfo refInfo) {
        String table = refInfo.getTable();
        Set<String> columns = refInfo.getColumns();
        String key = refInfo.getKey();
        Set<String> keyValues = refInfo.getKeyValues();

        // 空值检查
        if (keyValues == null || keyValues.isEmpty()) {
            refInfo.setResults(Collections.emptyMap());
            return;
        }

        Map<String, Object> results = new HashMap<>(MapUtils.calculateHashMapCapacity(keyValues.size()));
        refInfo.setResults(results);
        // 1. 从缓存加载数据
        Map<String, Object> cachedData = loadFromCache(table, columns, key, keyValues);
        results.putAll(cachedData);

        // 2. 找出缓存缺失的key
        Set<String> missingKeys = keyValues.stream()
                .filter(k -> !cachedData.containsKey(k))
                .collect(Collectors.toSet());

        if (!missingKeys.isEmpty()) {
            // 3. 从数据源查询缺失数据
            Map<String, Object> dbData = loadFromDataSource(table, columns, key, missingKeys);

            // 4. 缓存新查询到的数据
            cacheFetchedData(table, columns, key, dbData);

            // 5. 合并结果（包括缓存和数据库查询结果）
            results.putAll(dbData);
        }
    }

    private Map<String, Object> loadFromCache(String table, Set<String> columns,
                                                           String key, Set<String> keyValues) {
        return refDataCache.loadRef(table, columns, key, keyValues).getResults();
    }

    private Map<String, Object> loadFromDataSource(String table, Set<String> columns,
                                                                String key, Set<String> keys) {
        List<?> dbResults = refDataSource.queryRefData(table, columns, key, keys);
        return parseRecords(dbResults, key);
    }

    private Map<String, Object> parseRecords(List<?> records, String keyField) {
        if (records == null) return Collections.emptyMap();

        Map<String, Object> resultMap = new HashMap<>(MapUtils.calculateHashMapCapacity(records.size()));
        for (Object record : records) {
            Object keyValue;
            if (record instanceof Map) {
                Map<String, Object> recordMap = (Map<String, Object>) record;
                keyValue = recordMap.get(keyField);
            }else {
                keyValue = beanAccessor.getProperty(record, keyField);
            }

            if (keyValue != null) {
                resultMap.put(keyValue.toString(), record);
            }
        }
        return resultMap;
    }

    private void cacheFetchedData(String table, Set<String> columns,
                                  String key, Map<String, Object> records) {
        if (!records.isEmpty()) {
            RefInfo refInfo = new RefInfo(table, key, records);
            refDataCache.cacheRef(refInfo);
        }
    }



    public RefDataSource getRefDataSource() {
        return refDataSource;
    }

    public void setRefDataSource(RefDataSource refDataSource) {
        this.refDataSource = refDataSource;
    }

    public RefDataCache getRefCache() {
        return refDataCache;
    }

    public void setRefCache(RefDataCache refDataCache) {
        this.refDataCache = refDataCache;
    }

}
