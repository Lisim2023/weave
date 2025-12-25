package cn.filaura.weave.cache.dict;


import cn.filaura.weave.cache.AbstractCacheManager;
import cn.filaura.weave.cache.CacheOperation;
import cn.filaura.weave.cache.Serializer;
import cn.filaura.weave.dict.DictCache;
import cn.filaura.weave.dict.DictInfo;

import java.util.List;
import java.util.Map;

public class DictCacheManager extends AbstractCacheManager implements DictCache {

    private static final String DEFAULT_PREFIX = "weave:dict";

    private String prefix = DEFAULT_PREFIX;

    private DictCacheKeyGenerator keyGenerator = DictCacheManager::buildCacheKey;

    public DictCacheManager(CacheOperation cacheOperation, Serializer serializer) {
        super(cacheOperation, serializer);
    }

    public static String buildCacheKey(String prefix, String dictCode) {
        return prefix + ":" + dictCode;
    }

    @Override
    public void putDict(Map<String, DictInfo> dictInfoMap) {
        multiSet(dictInfoMap, this::generateCacheKey);
    }

    @Override
    public Map<String, DictInfo> loadDict(List<String> dictCodes) {
        return multiGet(dictCodes, DictInfo.class, this::generateCacheKey);
    }

    @Override
    public void removeDict(List<String> dictCodes) {
        multiRemove(dictCodes, this::generateCacheKey);
    }

    @Override
    public void removeDict(String dictCode) {
        remove(dictCode, this::generateCacheKey);
    }


    private String generateCacheKey(String originalKey) {
        return keyGenerator.generate(prefix, originalKey);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public DictCacheKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(DictCacheKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
}
