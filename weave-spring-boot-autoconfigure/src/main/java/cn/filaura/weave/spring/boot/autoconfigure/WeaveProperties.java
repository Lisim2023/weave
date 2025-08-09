package cn.filaura.weave.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Weave功能配置属性类
 * <p>
 * 包含字典处理、引用关联和缓存策略的全部配置项。
 * </p>
 */
@ConfigurationProperties(prefix = WeaveProperties.WEAVE_PREFIX)
public class WeaveProperties {

    /** 配置属性统一前缀 */
    public static final String WEAVE_PREFIX = "weave";

    /** 字典相关配置项 */
    private final Dict dict = new Dict();
    /** 引用相关配置项 */
    private final Ref ref = new Ref();
    /** 缓存策略配置项 */
    private final Cache cache = new Cache();

    // Getters and Setters
    public Dict getDict() { return dict; }
    public Ref getRef() { return ref; }
    public Cache getCache() { return cache; }

    /**
     * 字典处理配置组
     */
    public static class Dict {
        /**
         * 字典值分隔符
         */
        private String delimiter;

        /**
         * 字典文本字典默认后缀
         */
        private String fieldNameSuffix;

        // Getters and Setters
        public String getDelimiter() { return delimiter; }
        public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
        public String getFieldNameSuffix() { return fieldNameSuffix; }
        public void setFieldNameSuffix(String fieldNameSuffix) { this.fieldNameSuffix = fieldNameSuffix; }
    }

    /**
     * 引用关联配置组
     */
    public static class Ref {
        /**
         * 全局主键字段名
         */
        private String globalPrimaryKey;
        /**
         * 空值显示文本
         * <p>当关联数据中的列值为null时显示的文本
         */
        private String nullDisplayText;

        // Getters and Setters
        public String getGlobalPrimaryKey() { return globalPrimaryKey; }
        public void setGlobalPrimaryKey(String globalPrimaryKey) { this.globalPrimaryKey = globalPrimaryKey; }
        public String getNullDisplayText() { return nullDisplayText; }
        public void setNullDisplayText(String nullDisplayText) { this.nullDisplayText = nullDisplayText; }
    }

    /**
     * 缓存策略配置组
     */
    public static class Cache {
        /**
         * 字典数据存储键名
         * <p>字典数据在缓存中的全局存储键
         */
        private String dictStorageKey;

        /**
         * 引用数据缓存前缀
         * <p>引用数据在缓存中的键名前缀
         */
        private String refStoragePrefix;
        /**
         * 引用数据全局TTL（秒）
         * <p>所有引用数据缓存的基础生存时间
         */
        private Long refGlobalTtl;
        /**
         * 引用数据TTL随机偏移量（秒）
         * <p>在全局TTL基础上增加的随机偏移范围，用于避免缓存雪崩
         */
        private Integer refRandomTtlOffset;
        /**
         * 表级引用数据TTL配置
         * <p>按表名配置自定义TTL（秒），格式：{表名: TTL}
         */
        private final Map<String, Long> refTableTtl = new ConcurrentHashMap<>();

        // Getters and Setters
        public String getDictStorageKey() { return dictStorageKey; }
        public void setDictStorageKey(String dictStorageKey) { this.dictStorageKey = dictStorageKey; }
        public Long getRefGlobalTtl() { return refGlobalTtl; }
        public void setRefGlobalTtl(Long refGlobalTtl) { this.refGlobalTtl = refGlobalTtl; }
        public String getRefStoragePrefix() { return refStoragePrefix; }
        public void setRefStoragePrefix(String refStoragePrefix) { this.refStoragePrefix = refStoragePrefix; }
        public Integer getRefRandomTtlOffset() { return refRandomTtlOffset; }
        public void setRefRandomTtlOffset(Integer refRandomTtlOffset) { this.refRandomTtlOffset = refRandomTtlOffset; }
        public Map<String, Long> getRefTableTtl() { return refTableTtl; }
    }

}
