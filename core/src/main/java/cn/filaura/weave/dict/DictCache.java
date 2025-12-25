package cn.filaura.weave.dict;


import java.util.List;
import java.util.Map;

public interface DictCache {

    void putDict(Map<String, DictInfo> dictInfoMap);

    Map<String, DictInfo> loadDict(List<String> dictCodes);

    void removeDict(List<String> dictCodes);

    void removeDict(String dictCode);

}
