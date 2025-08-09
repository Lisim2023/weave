package cn.filaura.weave.ref;


import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.MapUtils;
import cn.filaura.weave.PropertyDescriptorBeanAccessor;

import java.util.*;

/**
 * 直接从引用数据源查询数据
 *
 * @see RefDataProvider
 */
public class DirectDataSourceRefDataProvider implements RefDataProvider {

    private final BeanAccessor beanAccessor = new PropertyDescriptorBeanAccessor();

    private RefDataSource refDataSource;



    public DirectDataSourceRefDataProvider(RefDataSource refDataSource) {
        this.refDataSource = refDataSource;
    }



    @Override
    public void getRefData(Collection<RefInfo> refInfos) {
        for (RefInfo query : refInfos) {
            List<?> records = refDataSource.queryRefData(query.getTable(), query.getColumns(), query.getKey(), query.getKeyValues());
            query.setResults(parseRecords(records, query.getKey()));
        }
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

    public RefDataSource getRefDataSource() {
        return refDataSource;
    }

    public void setRefDataSource(RefDataSource refDataSource) {
        this.refDataSource = refDataSource;
    }
}
