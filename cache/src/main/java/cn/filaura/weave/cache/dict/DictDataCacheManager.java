package cn.filaura.weave.cache.dict;


import cn.filaura.weave.MapUtils;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.dict.DictDataCache;
import cn.filaura.weave.dict.DictInfo;


import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典缓存管理器
 * <p>为字典模块缓存使用过的数据，也可用于维护字典缓存
 *
 * <p>字典数据以Hash结构缓存，Hash中的字段名为字典编码，值为经过序列化的键值对数据
 *
 * @see DictDataCache
 */
public class DictDataCacheManager implements DictDataCache {

    /** 主缓存键 */
    private String dictStorageKey = "weave:dict";

    private final Serializer serializer;
    private final DictDataCacheOperation dictDataCacheOperation;



    /**
     * 构造函数
     * @param serializer 序列化器
     * @param dictDataCacheOperation 字典缓存处理器
     */
    public DictDataCacheManager(Serializer serializer, DictDataCacheOperation dictDataCacheOperation) {
        this.serializer = serializer;
        this.dictDataCacheOperation = dictDataCacheOperation;
    }



    /**
     * 缓存字典数据
     *
     * @param dictInfo 要缓存的字典信息对象
     */
    public void cacheDict(DictInfo dictInfo) {
        if (dictInfo == null || dictInfo.getCode() == null || dictInfo.getData() == null) {
            return;
        }
        String serialized = serializer.serialize(dictInfo.getData());
        dictDataCacheOperation.cacheDict(dictStorageKey, dictInfo.getCode(), serialized);
    }

    /**
     * 批量缓存字典数据
     *
     * @param dictInfos 字典信息对象集合
     */
    @Override
    public void cacheDict(Collection<DictInfo> dictInfos) {
        if (dictInfos == null || dictInfos.isEmpty()) {
            return;
        }

        Map<String, String> dataMap = new HashMap<>(MapUtils.calculateHashMapCapacity(dictInfos.size()));
        for (DictInfo dictInfo : dictInfos) {
            if (dictInfo != null && dictInfo.getCode() != null && dictInfo.getData() != null) {
                String serialized = serializer.serialize(dictInfo.getData());
                dataMap.put(dictInfo.getCode(), serialized);
            }
        }
        dictDataCacheOperation.cacheDict(dictStorageKey, dataMap);
    }

    /**
     * 根据字典编码加载字典数据
     *
     * @param dictCode 字典编码
     * @return 对应的字典信息对象
     */
    public DictInfo loadDict(String dictCode) {
        if (dictCode == null || dictCode.isEmpty()) {
            return null;
        }
        String serializedData = dictDataCacheOperation.loadDict(dictStorageKey, dictCode);
        if (serializedData == null) {
            return null;
        }

        return new DictInfo(dictCode, serializer.deSerialize(serializedData));
    }

    /**
     * 批量加载多个字典编码对应的字典信息
     *
     * @param dictCodes 字典编码集合
     * @return 字典信息对象列表
     */
    @Override
    public List<DictInfo> loadDict(Collection<String> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> serializedData = dictDataCacheOperation.loadDict(dictStorageKey, dictCodes);
        List<DictInfo> dictInfos = new ArrayList<>();
        int index = 0;
        for (String dictCode : dictCodes) {
            if (index < serializedData.size() && serializedData.get(index) != null) {
                dictInfos.add(new DictInfo(dictCode, serializer.deSerialize(serializedData.get(index))));
            }
            index ++;
        }
        return dictInfos;
    }

    /**
     * 加载所有已缓存的字典信息
     *
     * @return 所有字典信息的列表
     */
    public List<DictInfo> loadAllDict() {
        Map<String, String> allSerializedData = dictDataCacheOperation.loadAllDict(dictStorageKey);
        if (allSerializedData == null || allSerializedData.isEmpty()) {
            return Collections.emptyList();
        }

        return allSerializedData.entrySet().stream()
                .map(entry -> {
                    Map<String, String> data = serializer.deSerialize(entry.getValue());
                    return new DictInfo(entry.getKey(), data);
                })
                .collect(Collectors.toList());
    }

    /**
     * 移除指定字典编码的缓存
     *
     * @param dictCode 字典编码
     * @return 被移除的缓存数量
     */
    public long removeDict(String dictCode) {
        if (dictCode == null || dictCode.isEmpty()) {
            return 0;
        }
        return dictDataCacheOperation.removeDict(dictStorageKey, dictCode);
    }

    /**
     * 批量移除多个字典编码的缓存
     *
     * @param dictCodes 字典编码集合
     * @return 被移除的缓存总数
     */
    public long removeDict(Collection<String> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return 0;
        }
        return dictDataCacheOperation.removeDict(dictStorageKey, dictCodes);
    }

    /**
     * 移除所有字典缓存
     *
     * @return 被移除的缓存总数
     */
    public long removeAllDict() {
        return dictDataCacheOperation.removeAllDict(dictStorageKey);
    }



    /**
     * 获取字典缓存的主键
     * @return 当前设置的主键
     */
    public String getDictStorageKey() {
        return dictStorageKey;
    }

    /**
     * 设置字典缓存的主键
     * @param dictStorageKey 新的主键
     */
    public void setDictStorageKey(String dictStorageKey) {
        this.dictStorageKey = dictStorageKey;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public DictDataCacheOperation getDictCacheHandler() {
        return dictDataCacheOperation;
    }

}
