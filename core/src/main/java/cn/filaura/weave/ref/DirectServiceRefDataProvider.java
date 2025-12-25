package cn.filaura.weave.ref;



import java.util.*;

public class DirectServiceRefDataProvider implements ServiceRefDataProvider {

    private final DataFetcher fetcher;

    public DirectServiceRefDataProvider(DataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public Map<String, ServiceResult> getReferenceData(Map<String, ServiceQuery> serviceQueryMap) {
        Map<String, ServiceResult> serviceResultMap = new HashMap<>();
        serviceQueryMap.forEach((key, query) -> {
            Map<String, Object> recordMap = fetcher.fetchReferenceData(query, query.getIds());
            serviceResultMap.put(key, new ServiceResult(query, recordMap));
        });
        return serviceResultMap;
    }

}
