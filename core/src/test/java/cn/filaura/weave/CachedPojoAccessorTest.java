package cn.filaura.weave;

import cn.filaura.weave.exception.PojoAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CachedPojoAccessor 单元测试")
class CachedPojoAccessorTest {

    private CachedPojoAccessor accessor;

    @BeforeEach
    void setUp() {
        accessor = new CachedPojoAccessor();
    }

    // 测试数据类
    public static class TestUser {
        private String name;
        private int age;
        private boolean active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        // 只读属性
        public String getReadOnly() {
            return "readonly";
        }

        // 只写属性
        public void setWriteOnly(String value) {
            // 只写属性，没有getter
        }
    }

    @Test
    @DisplayName("getPropertyValue - 正常获取属性值")
    void getPropertyValue_NormalCase() {
        TestUser user = new TestUser();
        user.setName("张三");
        user.setAge(25);
        user.setActive(true);

        assertEquals("张三", accessor.getPropertyValue(user, "name"));
        assertEquals(25, accessor.getPropertyValue(user, "age"));
        assertEquals(true, accessor.getPropertyValue(user, "active"));
    }

    @Test
    @DisplayName("getPropertyValue - pojo为null时抛出异常")
    void getPropertyValue_NullPojo_ThrowsException() {
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(null, "name")
        );
        assertEquals("Pojo object cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("getPropertyValue - 属性名为null时抛出异常")
    void getPropertyValue_NullPropertyName_ThrowsException() {
        TestUser user = new TestUser();

        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(user, null)
        );
        assertEquals("Property name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("getPropertyValue - 属性名为空字符串时抛出异常")
    void getPropertyValue_EmptyPropertyName_ThrowsException() {
        TestUser user = new TestUser();

        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(user, "  ")
        );
        assertEquals("Property name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("getPropertyValue - 属性不存在时抛出异常")
    void getPropertyValue_NonExistentProperty_ThrowsException() {
        TestUser user = new TestUser();

        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(user, "nonExistent")
        );
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("getPropertyValue - 属性不可读时抛出异常")
    void getPropertyValue_UnreadableProperty_ThrowsException() {
        TestUser user = new TestUser();

        // 注意：这里我们的测试类没有真正的只写属性
        // 但我们知道writeOnly属性没有getter，所以应该抛出异常
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(user, "writeOnly")
        );
        assertTrue(exception.getMessage().contains("is not readable"));
    }

    @Test
    @DisplayName("setPropertyValue - 正常设置属性值")
    void setPropertyValue_NormalCase() {
        TestUser user = new TestUser();

        accessor.setPropertyValue(user, "name", "李四");
        accessor.setPropertyValue(user, "age", 30);
        accessor.setPropertyValue(user, "active", false);

        assertEquals("李四", user.getName());
        assertEquals(30, user.getAge());
        assertEquals(false, user.isActive());
    }

    @Test
    @DisplayName("setPropertyValue - pojo为null时抛出异常")
    void setPropertyValue_NullPojo_ThrowsException() {
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.setPropertyValue(null, "name", "value")
        );
        assertEquals("Pojo object cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("setPropertyValue - 属性名为null时抛出异常")
    void setPropertyValue_NullPropertyName_ThrowsException() {
        TestUser user = new TestUser();

        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.setPropertyValue(user, null, "value")
        );
        assertEquals("Property name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("setPropertyValue - 属性名为空字符串时抛出异常")
    void setPropertyValue_EmptyPropertyName_ThrowsException() {
        TestUser user = new TestUser();

        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.setPropertyValue(user, "  ", "value")
        );
        assertEquals("Property name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("setPropertyValue - 属性不存在时抛出异常")
    void setPropertyValue_NonExistentProperty_ThrowsException() {
        TestUser user = new TestUser();

        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.setPropertyValue(user, "nonExistent", "value")
        );
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("setPropertyValue - 属性不可写时抛出异常")
    void setPropertyValue_UnwritableProperty_ThrowsException() {
        TestUser user = new TestUser();

        // readOnly属性只有getter，没有setter
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.setPropertyValue(user, "readOnly", "newValue")
        );
        assertTrue(exception.getMessage().contains("is not writable"));
    }

    @Test
    @DisplayName("getPropertyType - 正常获取属性类型")
    void getPropertyType_NormalCase() {
        assertEquals(String.class, accessor.getPropertyType(TestUser.class, "name"));
        assertEquals(int.class, accessor.getPropertyType(TestUser.class, "age"));
        assertEquals(boolean.class, accessor.getPropertyType(TestUser.class, "active"));
    }

    @Test
    @DisplayName("getPropertyType - pojoClass为null时抛出异常")
    void getPropertyType_NullPojoClass_ThrowsException() {
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyType(null, "name")
        );
        assertEquals("Pojo class cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("getPropertyType - 属性名为null时抛出异常")
    void getPropertyType_NullPropertyName_ThrowsException() {
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyType(TestUser.class, null)
        );
        assertEquals("Property name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("getPropertyType - 属性名为空字符串时抛出异常")
    void getPropertyType_EmptyPropertyName_ThrowsException() {
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyType(TestUser.class, "  ")
        );
        assertEquals("Property name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("getPropertyType - 属性不存在时抛出异常")
    void getPropertyType_NonExistentProperty_ThrowsException() {
        PojoAccessException exception = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyType(TestUser.class, "nonExistent")
        );
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("缓存测试 - 多次访问同一属性应使用缓存")
    void getPropertyValue_CacheTest() {
        TestUser user = new TestUser();
        user.setName("王五");

        // 第一次访问应该正常
        assertEquals("王五", accessor.getPropertyValue(user, "name"));

        // 第二次访问应该使用缓存，不会报错
        assertEquals("王五", accessor.getPropertyValue(user, "name"));

        // 测试另一个对象，相同类型，也应该使用缓存
        TestUser user2 = new TestUser();
        user2.setName("赵六");
        assertEquals("赵六", accessor.getPropertyValue(user2, "name"));
    }

    @Test
    @DisplayName("缓存测试 - 不存在的属性也会被缓存")
    void getPropertyValue_CacheNonExistentProperty() {
        TestUser user = new TestUser();

        // 第一次访问不存在的属性应该抛出异常
        PojoAccessException exception1 = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(user, "nonExistent")
        );

        // 第二次访问同一个不存在的属性应该也抛出异常
        PojoAccessException exception2 = assertThrows(
                PojoAccessException.class,
                () -> accessor.getPropertyValue(user, "nonExistent")
        );

        // 异常信息应该相同
        assertEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    @DisplayName("不同类型对象的缓存隔离")
    void cacheIsolationBetweenDifferentClasses() {
        // 创建两个不同的测试类
        class ClassA {
            private String field;

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }
        }

        class ClassB {
            private String field;

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }
        }

        ClassA a = new ClassA();
        a.setField("A value");

        ClassB b = new ClassB();
        b.setField("B value");

        // 两个不同的类应该分别缓存
        assertEquals("A value", accessor.getPropertyValue(a, "field"));
        assertEquals("B value", accessor.getPropertyValue(b, "field"));
    }


    @Test
    @DisplayName("布尔属性的getter方法命名测试")
    void booleanPropertyGetterTest() {
        // 测试布尔类型的属性，is/get方法都可以工作
        TestUser user = new TestUser();
        user.setActive(true);

        // 使用isActive()方法获取
        assertEquals(true, accessor.getPropertyValue(user, "active"));

        // 设置false
        accessor.setPropertyValue(user, "active", false);
        assertFalse(user.isActive());
    }
}
