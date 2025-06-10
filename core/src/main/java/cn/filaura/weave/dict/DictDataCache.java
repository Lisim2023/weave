package cn.filaura.weave.dict;

import java.util.*;

/**
 * 字典数据缓存接口
 */
public interface DictDataCache {

    /**
     * 缓存字典数据
     *
     * @param dictInfos 待缓存的字典信息对象集合
     */
    void cacheDict(Collection<DictInfo> dictInfos);


    /**
     * 加载字典数据
     *
     * @param dictCodes 字典编码集合
     * @return 对应的字典信息列表
     */
    List<DictInfo> loadDict(Collection<String> dictCodes);

}
