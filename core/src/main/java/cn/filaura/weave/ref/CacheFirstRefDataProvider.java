package cn.filaura.weave.ref;


import cn.filaura.weave.MapUtils;

import java.util.*;

/**
 * 带缓存的引用数据提供
 * <p>缓存优先，缓存未命中再查询数据源，缓存从数据源获取的数据
 *
 * <p>始终假定数据记录中已经包含所需的全部列，没有对columns进行校验
 * @see RefDataProvider
 */
public class CacheFirstRefDataProvider implements RefDataProvider {

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
        if (refInfos == null) {
            return;
        }

        for (RefInfo query : refInfos) {
            String table = query.getTable();
            String key = query.getKey();
            Set<String> columns = query.getColumns();
            Set<String> values = query.getKeyValues();

            Map<String, Map<String, Object>> results = new HashMap<>(MapUtils.calculateHashMapCapacity(values.size()));
            RefInfo cacheData = refDataCache.loadRef(table, columns, key, values);
            Set<String> missingValues = new HashSet<>();
            if (cacheData == null || cacheData.getResults() == null) {
                missingValues.addAll(values);
            }else {
                results.putAll(cacheData.getResults());
                for (String value : values) {
                    if (results.get(value) == null || results.get(value).isEmpty()) {
                        missingValues.add(value);
                    }
                }
            }

            if (!missingValues.isEmpty()) {
                RefInfo newData = refDataSource.queryRefData(table, columns, key, missingValues);
                if (newData != null) {
                    refDataCache.cacheRef(newData);
                    results.putAll(newData.getResults());
                }
            }
            query.setResults(results);
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
