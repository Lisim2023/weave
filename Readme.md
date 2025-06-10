Weave
==========
Weave 是一款轻量级的 Java 开发工具库，
旨在通过注解驱动的方式来简化常见的数据处理任务。
主要包括字典数据转换以及跨表数据引用两种功能。
它能够显著减少手动编写繁琐 SQL 的工作量，同时提高代码的可维护性和可读性。


## 注解介绍

- ### `@Dict` 字典注解
`@Dict`注解用于完成数据字典的值与展示文本之间的双向转换。

使用该注解可以轻松地将数据库中存储的编码值转换为可读性更强的文本表示，并且反之亦然。

| 参数          |  必填  | 说明                      |
|---------------|:----:|-------------------------|
| `code`        |  ✅   | 字典标识码                   |
| `targetField` | ❌    | 目标属性名（默认值为 `原属性名 + Text`） |

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


- ### 其他注解

  - #### `@Cascade` 注解
    用于处理对象间的递归或级联关系。例如，菜单项可能包含子菜单项。支持集合类型。

  - #### `@ColumnBinding` 注解
    `@ColumnBinding`注解是`@Ref`注解的辅助注解，用于在`bindings`数组中显式声明列与属性的映射

  - #### `@Weave` 注解
    可用于定义切点，结合 AOP 实现统一的数据处理逻辑。



待完成
