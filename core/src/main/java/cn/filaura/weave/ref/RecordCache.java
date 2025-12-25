package cn.filaura.weave.ref;


import java.util.List;
import java.util.Map;

public interface RecordCache {

    <T> Map<String, T> loadRecords(List<String> ids, Class<T> recordType);

    void putRecords(Map<String, ?> recordMap, Class<?> recordType);

    void removeRecords(List<String> ids, Class<?> recordType);

    void removeRecord(String id, Class<?> recordType);

}
