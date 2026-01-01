Weave
==========
Weave 是一个轻量级、高性能的 Java 数据关联框架，
通过注解驱动的方式，
自动完成字典翻译、跨表/跨服务引用等常见的数据关联任务，
从而显著减少项目中的样板代码，
同时提高代码的可维护性和可读性。


## 应用场景
Weave 可以自动处理下列常见数据关联需求，不需要编写关联查询，也不需要手动赋值：
- **外键转展示文本**  
   根据业务对象中的外键字段（如 `userId`），自动查询并填充对应的展示字段（如 `userName`）。
- **外键转完整对象**  
   根据外键字段（如 `userId`），自动查询并嵌入完整的关联对象（如 `User` 实例）。
- **字典码转描述文本**  
   将业务对象中的字典码（如 `0`、`1`）自动翻译为对应的文本描述（如 “男”、“女”），以便展示。
- **描述文本转字典码（反向操作）**  
   根据业务对象中的文本描述，自动反译为字典码，以便持久化存储。
- **树型结构解析**  
   支持递归处理具有父子级联关系的树型结构数据（如组织架构树、菜单树等）。


## 项目特点
- **声明式编程**：通过简单的注解声明关联关系，框架自动完成数据填充。
- **无缝集成**：与 Spring、MyBatis 等主流框架无缝集成。
- **分布式友好**：天然适配分布式环境，兼容 Feign、Dubbo 等远程服务代理。
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
默认支持`MyBatis`系列框架，可通过[扩展](./Custom.md#扩展点)适配其他`ORM`   
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


## 更多内容
- [自定义配置与扩展指南](./Custom.md)
