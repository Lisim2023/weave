package cn.filaura.weave.cache.ref;



@FunctionalInterface
public interface CacheKeyGenerator {

    String generateKey(String prefix, String table, String key, String value);
}
