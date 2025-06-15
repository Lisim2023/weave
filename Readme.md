Weave
==========
Weave 是一款轻量级的 Java 开发工具库，
旨在通过注解驱动的方式来简化常见的数据处理任务。
主要包括字典翻译以及跨表数据引用两种功能。
它能够显著减少手动编写跨表查询/字典翻译的SQL的工作量，同时提高代码的可维护性和可读性。


## 注解介绍

- ### `@Dict` 字典注解
`@Dict`注解用于完成数据字典的值与展示文本之间的双向翻译。

使用该注解可以轻松地将数据库中存储的编码值转换为可读性更强的文本表示，并且反之亦然。

| 参数          | 必填 | 说明                      |
|---------------|:--:|-------------------------|
| `code`        | ✅  | 字典标识码                   |
| `targetField` | ❌  | 目标属性名（默认值为 `原属性名 + Text`） |

使用示例：
```java
// 使用targetField参数显式指定目标属性名
@Dict(code = "user_status", targetField = "statusLabel")
private String status;
private String statusLabel;

// 省略targetField参数，默认与“genderText”属性关联
@Dict(code = "gender")
private Integer gender;
@Excel(name = "性别")
private String genderText;
```


- ### `@Ref` 引用注解
`@Ref`注解用于通过外键从关联表引用数据并填充到当前对象。

| 参数          | 必填 | 说明         |
|---------------|:----:|------------|
| `table`       | ✅   | 关联表名       |
| `key`         | ❌   | 主键名        |
| `columns`     | ⚠️   | 需引用的列集合    |
| `bindings`    | ⚠️   | 列名-属性名映射关系 |
| `targetBean`  | ⚠️   | 目标对象（自动属性映射） |

⚠️注意：需要至少指定一种列名与属性名的映射方式。

使用示例：
- ##### 方式1：使用`bindings`参数显式指定列与属性的映射关系
```java
@Ref(
        table = "sys_role",
        bindings = {
                @ColumnBinding(column = "name", targetField = "roleName"),
                @ColumnBinding(column = "level", targetField = "roleLevel")
        }
)
private Long roleId;
private String roleName;
private Integer roleLevel;
```

- ##### 方式2：使用`columns`参数指定列名，自动推断对应的属性名，格式为：`原属性名 + Ref + 列名首字母大写`
```java
@Ref(table = "sys_role", columns = {"name", "level"})
private Long roleId;
private String roleIdRefName;  // 对应name列
private Integer roleIdRefLevel; // 对应level列
```

- ##### 方式3：使用`targetBean`参数指定另一对象，自动映射该对象的同名属性
```java
@Ref(table = "sys_user", targetBean = "user")
private Long userId;
private User user;  // 自动注入User对象的name、avatar等同名属性
```
若关联表字段与目标对象属性命名不一致，可配合`bindings`参数手动绑定。

- ### 其他注解

  - #### `@Cascade` 注解
    用于处理对象间的递归或级联关系。例如，菜单项可能包含子菜单项。标注在需要递归处理的属性上即可，支持集合类型。例如：

  ```java
  public class Menu {
      @Cascade
      private List<Menu> children;  // 自动递归处理子菜单
  }
  ```

  - #### `@ColumnBinding` 注解
    `@ColumnBinding`注解是`@Ref`注解的辅助注解，用于在`bindings`数组中显式声明列与属性的映射

  - #### `@Weave` 注解
    可用于定义切点，结合 AOP 实现统一的数据处理逻辑。



## 快速开始

### 1. 引入依赖
> 当前版本： v1.0.0

核心依赖：
```xml
<dependency>
    <groupId>cn.filaura</groupId>
    <artifactId>weave</artifactId>
    <version>1.0.0</version>
</dependency>
```
Redis缓存支持（已依赖核心组件）：
```xml
<dependency>
    <groupId>cn.filaura</groupId>
    <artifactId>weave-cache-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```


### 2. 初始化与数据源配置
Weave 提供了两个核心助手类字典助手`DictHelper`和引用助手`RefHelper`，分别作为字典和引用功能的操作入口。
`DictHelper`与`RefHelper`完全解耦，可根据需求**单独配置和使用**

#### 初始化步骤简要如下：
- #### 实现数据源接口（需自行实现）
  - ##### 字典数据源接口`DictDataSource`: 根据字典标识码查询字典数据
  - ##### 引用数据源接口`RefDataSource`: 根据表名、列名、主键名与主键值查询关联记录


- #### 使用数据源接口实例初始化对应的助手类：
```java
// 示例伪代码
DictDataSource dictDataSource = new MyDictDataSource();
DictHelper dictHelper = new DictHelper(dictDataSource);

RefDataSource refDataSource = new MyRefDataSource();
RefHelper refHelper = new RefHelper(refDataSource);
```
> **完整的接口实现与配置流程可参考示例项目：[weave-example](./example)**


### 3. 集成与使用
调用助手方法，传入数据对象即可（支持集合类型）。

推荐定义切面，在切面中统一处理，例如：
```java
@Aspect
@Component
public class WeaveAspect {
  
    @Resource
    private RefHelper refHelper;

    @Resource
    private DictHelper dictHelper;
    
    @Pointcut("@annotation(cn.filaura.weave.annotation.Weave)")
    public void weave() {
    }
    
    @AfterReturning(value = "weave()", returning = "result")
    public void afterReturning(Object result) {
        // 填充引用数据
        refHelper.populateRefData(result);
        // 填充字典文本
        dictHelper.populateDictText(result);
    }
}
```
上述切面会在指定方法执行完毕后，自动对方法的返回值进行数据填充处理。接下来在需要处理的目标方法上添加`@Weave`注解即可。



## 扩展与定制

简易组件图：
```
XxxHelper
│
├── XxxDataProvider（数据提供接口）
│   ├── DirectDataSourceXxxDataProvider（直连数据源策略）
│   │   └── XxxDataSource（数据源接口）
│   └── CacheFirstXxxDataProvider（缓存优先策略）
│       ├── XxxDataCache（缓存接口）
│       └── XxxDataSource（数据源接口）
│
└── BeanAccessor (属性访问接口)
    └── ConvertUtil（类型转换工具类）
        └── Convert<T>（类型转换接口）
```


- #### 多数据源支持
如果项目涉及多个数据源，可通过自定义实现 `DictDataProvider` 或 `RefDataProvider` 接口，灵活控制如何从不同数据源中获取数据，从而实现多数据源的调度与管理。

和数据源接口一样，你也可以直接使用自己实现的 DataProvider 接口来初始化助手类，例如：
```java
// 示例伪代码
DictDataProvider dictDataProvider = new MyDictDataProvider();
DictHelper dictHelper = new DictHelper(dictDataProvider);

RefDataProvider refDataProvider = new MyRefDataProvider();
RefHelper refHelper = new RefHelper(refDataProvider);
```


- #### 自定义属性访问器
通过实现`BeanAccessor`接口，可以自定义属性的获取与设置行为，例如使用 Map 存储对象属性、动态生成字段等。

初始化助手类时，将 `BeanAccessor` 实例作为参数传入构造方法即可替换默认实现，例如：
```java
// 示例伪代码
DictDataSource dataSource = new MyDictDataSource();
BeanAccessor beanAccessor = new MyBeanAccessor();

DictHelper dictHelper = new DictHelper(dataSource, beanAccessor);
```


- #### 自定义类型转换器
为目标类型实现`Convert<T>`接口，并在程序启动或初始化阶段，调用`ConvertUtil.register()`方法进行注册：
```java
// 示例伪代码
ConvertUtil.register(MyType.class, new MyTypeConverter());
```
重复注册相同类型会覆盖之前的转换器。