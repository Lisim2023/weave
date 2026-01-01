Weave
==========

## 自定义与扩展
### 可选参数
可配置项及其默认值一览：
```yaml
weave:
  # 功能开关
  aspect-enabled: true                    # 是否启用AOP切面
  reverse-aspect-enabled: true            # 是否启用逆向AOP切面
  response-body-advice-enabled: true      # 是否启用ResponseBodyAdvice全局处理

  table-reference-enabled: true           # 是否启用 @TableRef 注解
  service-reference-enabled: true         # 是否启用 @ServiceRef 与 @RecordEmbed 注解

  # 缓存开关（基于 Spring Data Redis）
  dict-cache-enabled: false               # 是否启用字典数据缓存
  record-cache-enabled: false             # 是否启用完整数据记录缓存
  column-projection-cache-enabled: false  # 是否启用列投影缓存
  
  # 字典相关
  dict:
    delimiter: ','                        # 多值分隔符
    text-field-suffix: 'Text'             # 字典文本字段默认后缀

  # 引用相关
  ref:
    global-primary-key: 'id'              # 全局主键字段名
    global-foreign-key-suffix: 'Id'       # 外键属性名默认后缀
    global-method-name: 'listByIds'       # 服务默认方法名
    batch-size: 500                       # 批量查询大小

  # 缓存配置
  cache:
    dict-prefix: 'weave:dict'
    record-prefix: 'weave:record'
    column-projection-prefix: 'weave:column_projection'

    ttl-seconds: 7200                     # 缓存有效期（秒）
    jitter-ratio: 0.1                     # 随机抖动比例（防雪崩，0-1之间）
    max-jitter-seconds: 300               # 最大抖动秒数
```

### 扩展点

通过实现以下接口并注册为 Spring Bean，可深度定制 Weave 行为：
#### 数据源
- [**DictDataProvider**](core/src/main/java/cn/filaura/weave/dict/DictDataProvider.java)：
  为`@Dict`注解提供数据。

- [**TableRefDataProvider**](core/src/main/java/cn/filaura/weave/ref/TableRefDataProvider.java)：
  自定义表查询逻辑，为`@TableRef`注解提供数据。

- [**ServiceRefDataProvider**](core/src/main/java/cn/filaura/weave/ref/ServiceRefDataProvider.java)：
  自定义服务方法调用方式（用于`@ServiceRef`和`@RecordEmbed`注解）。


#### 缓存策略
- [**DictCache**](core/src/main/java/cn/filaura/weave/dict/DictCache.java)

- [**ColumnProjectionCache**](core/src/main/java/cn/filaura/weave/ref/ColumnProjectionCache.java)

- [**RecordCache**](core/src/main/java/cn/filaura/weave/ref/RecordCache.java)


#### 其他
- [**ResultExtractor**](core/src/main/java/cn/filaura/weave/ref/ResultExtractor.java)：
  用于从服务调用返回的封装结果对象中提取实际数据列表。。

- [**TypeConverter**](core/src/main/java/cn/filaura/weave/type/TypeConverter.java)：
  自定义类型转换逻辑。

- [**Serializer**](cache/src/main/java/cn/filaura/weave/cache/Serializer.java)：
  自定义序列化方式，用于缓存。

- [**PojoAccessor**](core/src/main/java/cn/filaura/weave/PojoAccessor.java)：
  自定义属性访问机制。

> 💡 若 POJO 实现 [**PropertyExtensible**](core/src/main/java/cn/filaura/weave/PropertyExtensible.java) 接口，框架可动态注入映射字段（无需提前声明 `xxxName` 等属性）。

