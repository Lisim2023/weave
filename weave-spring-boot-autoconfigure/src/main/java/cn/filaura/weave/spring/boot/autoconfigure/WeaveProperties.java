package cn.filaura.weave.spring.boot.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


/**
 * Weave功能配置属性类
 * <p>
 * 包含字典处理、引用关联和缓存策略的全部配置项。
 * </p>
 */
@ConfigurationProperties(prefix = WeaveProperties.PREFIX)
public class WeaveProperties {

    /** 配置属性统一前缀 */
    public static final String PREFIX = "weave";
    public static final String DOT = ".";

    public static final String DICT_CACHE_ENABLED = PREFIX + DOT + "dict-cache-enabled";
    public static final String RECORD_CACHE_ENABLED = PREFIX + DOT + "record-cache-enabled";
    public static final String COLUMN_PROJECTION_CACHE_ENABLED =
            PREFIX + DOT + "column-projection-cache-enabled";

    public static final String TABLE_REFERENCE_ENABLED = PREFIX + DOT + "table-reference-enabled";
    public static final String SERVICE_REFERENCE_ENABLED =
            PREFIX + DOT + "service-reference-enabled";

    public static final String ASPECT_ENABLED = PREFIX + DOT + "aspect-enabled";
    public static final String REVERSE_ASPECT_ENABLED = PREFIX + DOT + "reverse-aspect-enabled";

    public static final String RESPONSE_BODY_ADVICE_ENABLED =
            PREFIX + DOT + "response-body-advice-enabled";

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
        /** 字典值分隔符 */
        private String delimiter;

        /** 字典文本属性名默认后缀 */
        private String textFieldSuffix;

        // Getters and Setters
        public String getDelimiter() { return delimiter; }
        public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
        public String getTextFieldSuffix() {return textFieldSuffix;}
        public void setTextFieldSuffix(String textFieldSuffix) {this.textFieldSuffix = textFieldSuffix;}
    }

    /**
     * 引用关联配置组
     */
    public static class Ref {
        /** 全局主键字段名 */
        private String globalPrimaryKey;
        /** 全局外键名后缀 */
        private String globalForeignKeySuffix;
        /** 全局方法名 */
        private String globalMethodName;
        /** 批量查询上限 */
        private Integer batchSize;

        // Getters and Setters
        public String getGlobalPrimaryKey() { return globalPrimaryKey; }
        public void setGlobalPrimaryKey(String globalPrimaryKey) { this.globalPrimaryKey = globalPrimaryKey; }
        public String getGlobalForeignKeySuffix() {return globalForeignKeySuffix;}
        public void setGlobalForeignKeySuffix(String globalForeignKeySuffix) {this.globalForeignKeySuffix = globalForeignKeySuffix;}
        public Integer getBatchSize() {return batchSize;}
        public void setBatchSize(Integer batchSize) {this.batchSize = batchSize;}
        public String getGlobalMethodName() {return globalMethodName;}
        public void setGlobalMethodName(String globalMethodName) {this.globalMethodName = globalMethodName;}
    }

    /**
     * 缓存策略配置组
     */
    public static class Cache {
        private String dictPrefix;

        private String recordPrefix;
        private Map<String, Long> ttlByClassName;

        private String columnProjectionPrefix;
        private Map<String, Long> ttlByTable;

        private Long ttlSeconds;
        private Double jitterRatio;
        private Integer maxJitterSeconds;

        public String getDictPrefix() {
            return dictPrefix;
        }
        public void setDictPrefix(String dictPrefix) {
            this.dictPrefix = dictPrefix;
        }
        public String getRecordPrefix() {
            return recordPrefix;
        }
        public void setRecordPrefix(String recordPrefix) {
            this.recordPrefix = recordPrefix;
        }
        public String getColumnProjectionPrefix() {
            return columnProjectionPrefix;
        }
        public void setColumnProjectionPrefix(String columnProjectionPrefix) {
            this.columnProjectionPrefix = columnProjectionPrefix;
        }
        public Long getTtlSeconds() {
            return ttlSeconds;
        }
        public void setTtlSeconds(Long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
        public Double getJitterRatio() {
            return jitterRatio;
        }
        public void setJitterRatio(Double jitterRatio) {
            this.jitterRatio = jitterRatio;
        }
        public Integer getMaxJitterSeconds() {
            return maxJitterSeconds;
        }
        public void setMaxJitterSeconds(Integer maxJitterSeconds) {
            this.maxJitterSeconds = maxJitterSeconds;
        }
        public Map<String, Long> getTtlByTable() {
            return ttlByTable;
        }
        public void setTtlByTable(Map<String, Long> ttlByTable) {
            this.ttlByTable = ttlByTable;
        }
        public Map<String, Long> getTtlByClassName() {
            return ttlByClassName;
        }
        public void setTtlByClassName(Map<String, Long> ttlByClassName) {
            this.ttlByClassName = ttlByClassName;
        }
    }

}
