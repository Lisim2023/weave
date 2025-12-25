package cn.filaura.weave.cache.ref;


@FunctionalInterface
public interface RecordCacheKeyGenerator {

    String generateKey(String prefix, Class<?> type, String id);

}
