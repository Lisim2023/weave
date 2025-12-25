package cn.filaura.weave.cache.dict;


@FunctionalInterface
public interface DictCacheKeyGenerator {

    String generate(String prefix, String dictCode);
}
