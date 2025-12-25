package cn.filaura.weave.dict;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalMemoryDictCache implements DictCache {

    private final ConcurrentHashMap<String, DictInfo> cache = new ConcurrentHashMap<>();

    @Override
    public void putDict(Map<String, DictInfo> dictInfoMap) {
        dictInfoMap.forEach((code, info) -> {
            if (info != null && info.getCode() != null && info.getData() != null) {
                cache.put(code, info);
            }
        });
    }

    @Override
    public Map<String, DictInfo> loadDict(List<String> dictCodes) {
        Map<String, DictInfo> result = new HashMap<>();
        for (String code : dictCodes) {
            DictInfo dictInfo = cache.get(code);
            if (dictInfo != null) {
                result.put(code, dictInfo);
            }
        }
        return result;
    }

    @Override
    public void removeDict(List<String> dictCodes) {
        for (String code : dictCodes) {
            cache.remove(code);
        }
    }

    @Override
    public void removeDict(String dictCode) {
        cache.remove(dictCode);
    }

}
