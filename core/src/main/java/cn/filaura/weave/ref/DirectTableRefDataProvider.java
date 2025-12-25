package cn.filaura.weave.ref;

import java.util.*;

public class DirectTableRefDataProvider implements TableRefDataProvider {

    private DataFetcher dataFetcher;

    public DirectTableRefDataProvider(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @Override
    public Map<String, TableResult> getReferenceData(Map<String, TableQuery> dbQueryMap) {
        Map<String, TableResult> dbResultMap = new HashMap<>();
        dbQueryMap.forEach((key, query) -> {
            Map<String, Map<String, Object>> recordMap =
                    dataFetcher.fetchReferenceData(query, query.getIds());
            dbResultMap.put(key, new TableResult(query, recordMap));
        });
        return dbResultMap;
    }


    public DataFetcher getDataSource() {
        return dataFetcher;
    }

    public void setDataSource(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }
}
