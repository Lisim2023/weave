package cn.filaura.weave.cache.ref;


@FunctionalInterface
public interface ColumnProjectionCacheKeyGenerator {

    String generateKey(String prefix, String table, String keyColumn, String id);

}
