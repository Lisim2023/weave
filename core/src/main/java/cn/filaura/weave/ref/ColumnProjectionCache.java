package cn.filaura.weave.ref;

import java.util.List;
import java.util.Map;


public interface ColumnProjectionCache {

    Map<String, Map<String, Object>> loadProjections(String table,
                                                     String keyColumn,
                                                     List<String> ids);

    void putProjections(String table,
                        String keyColumn,
                        Map<String, Map<String, Object>> recordMap);

    void removeProjections(String table,
                           String keyColumn,
                           List<String> ids);

    void removeProjection(String table,
                          String keyColumn,
                          String id);

}
