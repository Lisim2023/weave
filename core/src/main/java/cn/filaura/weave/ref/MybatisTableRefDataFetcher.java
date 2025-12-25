package cn.filaura.weave.ref;

import cn.filaura.weave.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MybatisTableRefDataFetcher implements TableRefDataProvider.DataFetcher {

    private int batchSize = 500;

    private final TableRefMapper mapper;

    public MybatisTableRefDataFetcher(TableRefMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<String, Map<String, Object>> fetchReferenceData(TableQuery tableQuery,
                                                               Collection<Object> ids) {
        Map<String, Map<String, Object>> result = new HashMap<>(
                CommonUtils.calculateHashMapCapacity(ids.size()));
        List<Object> sortedIds = ids.stream()
                .sorted()
                .collect(Collectors.toList());
        String table = tableQuery.getTable();;
        String keyColumn = tableQuery.getKeyColumn();
        Set<String> columns = tableQuery.getColumns();
        for (int i = 0; i < sortedIds.size(); i += batchSize) {
            List<Object> batchIds = sortedIds.subList(i, Math.min(i + batchSize, ids.size()));
            List<Map<String, Object>> batchRecords =
                    mapper.queryReferenceData(table, columns, keyColumn, batchIds);
            for (Map<String, Object> record : batchRecords) {
                String id = String.valueOf(record.get(keyColumn));
                result.put(id, record);
            }
        }
        return result;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
