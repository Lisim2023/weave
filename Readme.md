Weave
==========
Weave 是一个轻量级的 Java 数据关联框架，
通过注解驱动的方式，
自动完成字典翻译、跨表/跨服务引用等常见的数据关联任务，
从而显著减少项目中的样板代码，
同时提高代码的可维护性和可读性。


## 项目特点
- **声明式编程**：通过简单的注解声明关联关系，框架自动完成数据填充。
- **无缝集成**：与 Spring、MyBatis 等主流框架无缝集成。
- **分布式友好**：天然适配分布式环境，兼容 Feign、Dubbo 等远程服务调用。
- **灵活扩展**：支持自定义数据源、缓存、序列化等组件。
- **非侵入性**：业务对象无需继承特定基类，支持动态字段注入。


## 注解介绍

### `@ServiceRef`
**用途**：用于通过外键从指定服务的指定方法获取数据，并将结果的属性映射到当前对象。  
**标注位置**：类。  
**示例**：
```java
@ServiceRef(
        service = UserService.class, 
        mappings = {
             @Mapping(refField = "firstApprover", from = "name", to = "firstApproverName"), 
             @Mapping(refField = "secondApprover", from = "name", to = "secondApproverName")
        }
)
public class ContractDTO {
    private Long firstApprover;
    private String firstApproverName;

    private Long secondApprover;
    private String secondApproverName;
}
```

### `@RecordEmbed`
**用途**：用于通过外键从指定服务的指定方法获取数据，并将完整数据记录嵌入到当前对象。  
**标注位置**：需要嵌入数据的属性（支持集合和数组）。  
**示例**：
```java
public class OrderDTO {
    
    private String userId;

    @RecordEmbed(service = UserService.class)
    private User user;
}
```

### `@TableRef`
**用途**：用于通过外键从关联表查询数据，并将结果中的列值映射到当前对象（只查询必要的列）。
默认支持`MyBatis`系列框架，可通过[扩展](#expansion)适配其他`ORM`   
**标注位置**：类。  
**示例**：
```java
@TableRef(
        table = "sys_user",
        mappings = {
                @Mapping(refField = "createBy", from = "name", to = "createByName"),
                @Mapping(refField = "updateBy", from = "name", to = "updateByName")
        }
)
public class OrderDTO {
    private Long createBy;
    private String createByName;

    private Long updateBy;
    private String updateByName;
}
```

### `@Dict`
**用途**：用于将字典的值（如 1）翻译为对应的描述文本（如 "启用"），或反之将描述文本翻译为字典值。  
**标注位置**：存储字典值的属性（支持集合、数组、分隔符）。  
**示例**：
```java
public class User {
  @Dict(code = "user_status")
  private Integer status;
  private String statusText;
}
```
> ⚠️使用前需实现 [DictDataProvider](core/src/main/java/cn/filaura/weave/dict/DictDataProvider.java) 并注册为`Spring Bean`。  
> 示例参考：[DictDataFetcher.java](example/src/main/java/cn/filaura/weave/example/dict/DictDataFetcher.java)


### `@Cascade`
**用途**：用于处理对象间的递归或级联关系，支持集合、数组。  
**标注位置**：需要级联处理的属性。  
**示例**：
```java
public class Menu {
  @Cascade
  private List<Menu> children;  // 自动递归处理子菜单
}
```  


## 快速开始

### 1. 引入依赖
> 当前版本： v1.2.0
```xml
<dependency>
    <groupId>cn.filaura</groupId>
    <artifactId>weave-spring-boot-starter</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 2. 使用方式
- #### 方式一：全局自动处理
若项目中包含`spring-boot-starter-web`，`Weave` 会自动拦截所有 `@ResponseBody` 返回值，并执行数据装配。  
只用在 POJO类 按需添加注解即可，无需其它操作。
> 可通过参数 weave.response-body-advice-enabled = false 关闭。


- #### 方式二：AOP 精确控制
若项目中包含 `spring-boot-starter-aop`，可在通过在方法上标注切面注解进行装配（目标方法必须被 Spring AOP 代理）。  
`@Weave`：处理**返回值**（正向装配，填充字典文本、关联数据）
```java
@Weave
// 自动填充 statusText="启用", creatorName="张三" 等
public List<User> listUsers() {
  return userMapper.selectAll();
}
```
`@WeaveReverse`：处理**入参**（逆向字典翻译，字典文本 → 字典值）
```java
@WeaveReverse
public void importUsers(List<User> users) {
  // users 中的 statusText="启用" 将被自动转为 status=1
  userMapper.batchInsert(users);
}
```


- #### 方式三：手动调用
根据需要注入相应的`Helper`，调用其方法即可：
```java
@Autowired
private DictHelper dictHelper;
@Autowired
private TableRefHelper tableRefHelper;
@Autowired
private ServiceRefHelper serviceRefHelper;

public void process(List<User> users) {
  dictHelper.populateDictText(users);          // 正向字典翻译
  dictHelper.populateDictValue(users);         // 反向字典解析
  tableRefHelper.populateTableReferences(users);     // 表关联填充
  serviceRefHelper.populateServiceReferences(users); // 服务引用填充
}
```

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
<a id="expansion"></a>

通过实现以下接口并注册为 Spring Bean，可深度定制 Weave 行为：
### 数据源
- **[DictDataProvider](core/src/main/java/cn/filaura/weave/dict/DictDataProvider.java)**  
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

