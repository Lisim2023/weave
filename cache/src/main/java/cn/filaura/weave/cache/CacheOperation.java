package cn.filaura.weave.cache;

import java.util.List;
import java.util.Map;

public interface CacheOperation {

    void multiSet(Map<String, String> data);

    void multiSet(Map<String, String> data, long seconds);

    Map<String, String> multiGet(List<String> keys);

    void multiRemove(List<String> keys);

    void remove(String key);

}
