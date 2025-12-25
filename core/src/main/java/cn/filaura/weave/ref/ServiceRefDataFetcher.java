package cn.filaura.weave.ref;


import cn.filaura.weave.CachedPojoAccessor;
import cn.filaura.weave.CommonUtils;
import cn.filaura.weave.PojoAccessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceRefDataFetcher implements ServiceRefDataProvider.DataFetcher {

    private PojoAccessor pojoAccessor;
    private final SpringBeanMethodInvoker methodInvoker;

    private int batchSize = 500;

    public ServiceRefDataFetcher(SpringBeanMethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
        this.pojoAccessor = new CachedPojoAccessor();
    }

    public ServiceRefDataFetcher(SpringBeanMethodInvoker methodInvoker, PojoAccessor pojoAccessor) {
        this.methodInvoker = methodInvoker;
        this.pojoAccessor = pojoAccessor;
    }


    @Override
    public Map<String, Object> fetchReferenceData(ServiceQuery serviceQuery,
                                                  Collection<Object> ids) {
        Map<String, Object> result = new HashMap<>(
                CommonUtils.calculateHashMapCapacity(ids.size()));
        List<Object> sortedIds = ids.stream()
                .sorted()
                .collect(Collectors.toList());
        Class<?> service = serviceQuery.getService();
        String method = serviceQuery.getMethod();
        String keyField = serviceQuery.getKeyField();
        for (int i = 0; i < sortedIds.size(); i += batchSize) {
            List<Object> batchIds = sortedIds.subList(i, Math.min(i + batchSize, ids.size()));
            Collection<?> batchRecords =
                    methodInvoker.invokeServiceMethod(service, method, batchIds);
            for (Object record : batchRecords) {
                String key = String.valueOf(pojoAccessor.getPropertyValue(record, keyField));
                result.put(key, record);
            }
        }
        return result;
    }

    @Override
    public Class<?> getRecordTypeForQuery(ServiceQuery serviceQuery) {
        return methodInvoker.getMethodReturnElementType(
                serviceQuery.getService(), serviceQuery.getMethod());
    }


    public PojoAccessor getPojoAccessor() {
        return pojoAccessor;
    }

    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        this.pojoAccessor = pojoAccessor;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
