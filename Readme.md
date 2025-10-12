Weave
==========
Weave是一款轻量级的数据关联框架，
支持以注解驱动的方式完成跨表/跨服务数据的智能组装，
通过标注注解即可自动完成字典翻译和跨表引用（替代Join查询），
从而显著减少项目中的样板代码，
同时提高代码的可维护性和可读性。

## 项目特点
- 无缝集成Spring、Mybatis等主流框架
- 天然适配分布式环境，支持跨服务数据关联
- 灵活扩展，支持自定义数据源、缓存、序列化等


## 注解介绍

- ### `@Dict` 字典注解
用于将字典字段的值翻译为对应的文本描述，可逆向操作。

| 参数         | 必填 | 说明                      |
|------------|:--:|-------------------------|
| `code`     | ✅  | 字典标识码                   |
| `property` | ❌  | 目标属性名，默认值为 `原属性名 + Text`|

使用示例：
```java
// 使用property参数显式指定目标属性名
@Dict(code = "user_status", property = "statusLabel")
private String status;
private String statusLabel;

// 省略property参数，默认与“genderText”属性关联
@Dict(code = "gender")
private Integer gender;
private String genderText;
```


- ### `@Ref` 引用注解
用于通过外键从其他表或服务中引用数据并自动填充，替代Join查询。

| 参数         | 必填 | 说明                |
|------------|:----:|-------------------|
| `table`    | ✅   | 关联表名              |
| `key`      | ❌   | 主键名               |
| `mappings` | ⚠️   | 列名-属性名映射关系（需配合`@Mapping`）|
| `mapTo`    | ⚠️   | 映射到指定对象（自动映射同名属性） |

> ⚠️注意：`mappings`与`mapTo`至少需要填写其中一种。

使用示例：
- ##### `mappings`参数：指定列与属性的映射关系
```java
@Ref(
        table = "sys_user",
        mappings = { @Mapping(column = "username", property = "createByUserName") }
)
private Long createBy;
private String createByUserName;
```

- ##### `mapTo`参数：将数据填充到目标对象（支持集合、数组）
```java
@Ref(table = "sys_user", mapTo = "user")
private Long userId;
private User user;  // ← 自动填充 name/phone 等同名字段

@Ref(table = "sys_role", mapTo = "roles")
private List<Long> roleIds;
private List<Role> roles;   // ← 自动填充角色列表
```

- ### 其他注解

  - #### `@Cascade` 级联注解
    用于处理对象间的递归或级联关系，支持集合、数组：
    ```java
    public class Menu {
      @Cascade
      private List<Menu> children;  // 自动递归处理子菜单
    }
    ```


  - #### `@Mapping` 映射注解
    配合 `@Ref` 定义字段映射规则
    ```java
    @Mapping(column = "dept_name", property = "departmentName")
    ```


  - #### `@Weave` 默认切点
    标注于方法，自动对方法返回值执行引用和字典翻译。  
    ```java
    @Weave
    // 自动对方法返回值执行跨表引用和字典翻译
    public List<User> queryUsers() {
    }
    ```


  - #### `@WeaveReverse` 逆向切点
    标注于方法，自动对方法入参执行逆向字典翻译（字典文本到字典值）。  
    ```java
    @WeaveReverse
    public void importUsers(List<User> users) {
        // 自动对方法入参执行逆向字典翻译
        userService.saveBatch(users);
    }
    ```


## 快速开始

### 1. 引入依赖
> 当前版本： v1.1.0

自动配置（需同步引入 `aspectj` ）：
```xml
<dependency>
    <groupId>cn.filaura</groupId>
    <artifactId>weave-spring-boot-autoconfigure</artifactId>
    <version>1.1.0</version>
</dependency>
```
（可选）Redis缓存支持（需同步引入 `spring-data-redis` 和 `jackson` ）：
```xml
<dependency>
    <groupId>cn.filaura</groupId>
    <artifactId>weave-cache-redis</artifactId>
    <version>1.1.0</version>
</dependency>
```


### 2. 配置数据源
需要实现数据源接口为对应的模块提供数据，实现以下接口并配置为 Spring Bean即可：  
- ##### 字典数据源：[DictDataSource](/core/src/main/java/cn/filaura/weave/dict/DictDataSource.java)
    ```java
    // 示例伪代码
    @Component
    public class CustomDictDataSource implements DictDataSource {
        @Override
        public List<DictInfo> queryDictData(Collection<String> dictCodes) {
            List<DictInfo> dictInfos = new ArrayList<>();
            for (String dictCode : dictCodes) {
                // 从数据库/配置中心/远程服务获取字典数据
                Map<String, String> dictItems = dictService.findByCode(dictCode);
                // 封装成要求的格式（一个标识码对应一组键值对Map）
                dictInfos.add(new DictInfo(dictCode, dictItems));
            }
            return dictInfos;
        }
    }
    ```
  完整的实现流程可参考：[DictDataSourceImpl](/example/src/main/java/cn/filaura/weave/example/dict/DictDataSourceImpl.java)


- ##### 引用数据源：[RefDataSource](/core/src/main/java/cn/filaura/weave/ref/RefDataSource.java)

    ```java
    // 示例伪代码
    @Component
    public class CustomRefDataSource implements RefDataSource {

        private Map<String, Service> serviceRouter = new HashMap<>();
    
        @Override
        public List<?> queryRefData(String table, Collection<String> columns, String key, Collection<String> values) {
            // 将主键值转换为正确的类型并排序
            List<Long> ids = values.stream().map(Long::valueOf).sorted().toList();
            // 按表名路由到对应服务
            Service service = serviceRouter.get(table);
            // 查询结果并返回
            return service.listByIds(ids);
        }
    }
    ```
  完整的实现流程可参考：[RefDataSource2](/example/src/main/java/cn/filaura/weave/example/ref/RefDataSource2.java)  
  也可以用动态SQL实现，返回List<Map<String, Object>>即可。


### 3. 标注注解
在实体类中按需标注 `@Dict`、`@Ref` 等字段注解，
同时在目标方法上按需添加`@Weave`、`@WeaveReverse`切面注解即可
（详见前文介绍）。


## 可选参数
```yaml
weave:
  # 功能开关
  disable-weave-aspect: false          # 是否禁用 @Weave 切面
  disable-weave-reverse-aspect: false  # 是否禁用 @WeaveReverse 切面

  # 字典配置
  dict:
    delimiter: ','                  # 多值分隔符
    field-name-suffix: 'Text'       # 生成字段后缀

  # 引用配置
  ref:
    global-primary-key: 'id'        # 全局主键名
    null-display-text: 'null'       # 空值显示文本

  # 缓存配置
  cache:
    dict-storage-key: "weave:dict"  # 字典缓存键
    ref-storage-prefix: 'weave:ref' # 引用缓存前缀
    ref-global-ttl: 86400           # 全局缓存时间(秒)
    ref-random-ttl-offset: 300      # 随机过期时间偏移量(防雪崩)
    # 表级缓存时间
    ref-table-ttl:
      # 示例
      sys_user: 3600    # 用户表缓存1小时
      sys_role: 86400   # 角色表缓存1天
      sys_dept: 1800    # 部门表缓存30分钟
```

## 扩展
- #### 多数据源支持 
  实现以下接口以支持多数据源调度：  
  字典：[DictDataProvider](/core/src/main/java/cn/filaura/weave/dict/DictDataProvider.java)  
  引用：[RefDataProvider](/core/src/main/java/cn/filaura/weave/ref/RefDataProvider.java)  
  配置为SpringBean后自动生效。


- #### 自定义类型转换器
  处理特殊类型：
  ```java
  ConvertUtil.register(MyType.class, new MyTypeConverter());
  ```
  详见[ConvertUtil](/core/src/main/java/cn/filaura/weave/type/ConvertUtil.java)。


- #### 自定义序列化器
  用于缓存中对象的序列化/反序列化。  
  实现[Serializer](/cache/src/main/java/cn/filaura/weave/cache/Serializer.java)接口，并配置为SpringBean


- #### 自定义缓存操作
  非 Redis 缓存（如 Caffeine、Memcached），可实现以下接口：  
  字典缓存操作：[DictDataCacheOperation](/cache/src/main/java/cn/filaura/weave/cache/dict/DictDataCacheOperation.java)  
  引用缓存操作：[RefDataCacheOperation](/cache/src/main/java/cn/filaura/weave/cache/ref/RefDataCacheOperation.java)  
  配置为 SpringBean 后将自动接管缓存逻辑。
