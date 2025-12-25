package cn.filaura.weave;

import cn.filaura.weave.annotation.Cascade;
import cn.filaura.weave.exception.PojoAccessException;
import cn.filaura.weave.type.TypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractWeaver 单元测试")
class AbstractWeaverTest {

    @Mock
    private PojoAccessor mockPojoAccessor;

    @Mock
    private TypeConverter mockTypeConverter;

    private TestWeaver weaver;

    // 测试用的内部类
    static class TestPojo {
        private String name;
        private int age;

        public TestPojo(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    static class CascadedPojo {
        @Cascade
        private TestPojo child;

        @Cascade
        private List<TestPojo> children;

        private String normalField;
    }

    // 用于测试的具体实现类
    static class TestWeaver extends AbstractWeaver {
        // 可以添加一些辅助方法用于测试
    }

    @BeforeEach
    void setUp() {
        weaver = new TestWeaver();
        weaver.setPojoAccessor(mockPojoAccessor);
        weaver.setTypeConverter(mockTypeConverter);
    }

    @Test
    @DisplayName("应该正确收集单个POJO的类类型")
    void gatherClassTypes_SinglePojo() {
        // 准备
        TestPojo pojo = new TestPojo("test", 25);

        // 执行
        Set<Class<?>> result = weaver.gatherClassTypes(pojo);

        // 验证
        assertEquals(1, result.size());
        assertTrue(result.contains(TestPojo.class));
    }

    @Test
    @DisplayName("应该正确收集集合中POJO的类类型")
    void gatherClassTypes_Collection() {
        // 准备
        List<TestPojo> pojos = Arrays.asList(
                new TestPojo("test1", 25),
                new TestPojo("test2", 30)
        );

        // 执行
        Set<Class<?>> result = weaver.gatherClassTypes(pojos);

        // 验证
        assertEquals(1, result.size());
        assertTrue(result.contains(TestPojo.class));
    }

    @Test
    @DisplayName("应该正确处理空值输入")
    void gatherClassTypes_NullInput() {
        // 执行
        Set<Class<?>> result = weaver.gatherClassTypes(null);

        // 验证
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("应该将null转换为空字符串数组")
    void toStringArray_NullInput() {
        // 执行
        String[] result = weaver.toStringArray(null);

        // 验证
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("应该将集合转换为字符串数组")
    void toStringArray_Collection() {
        // 准备
        List<Object> collection = Arrays.asList(1, "test", 3.14);

        // 执行
        String[] result = weaver.toStringArray(collection);

        // 验证
        assertArrayEquals(new String[]{"1", "test", "3.14"}, result);
    }

    @Test
    @DisplayName("应该将数组转换为字符串数组")
    void toStringArray_Array() {
        // 准备
        Object[] array = new Object[]{1, "test", 3.14};

        // 执行
        String[] result = weaver.toStringArray(array);

        // 验证
        assertArrayEquals(new String[]{"1", "test", "3.14"}, result);
    }

    @Test
    @DisplayName("应该将流转换为Set集合")
    void convertStreamToCollection_Set() {
        // 准备
        Stream<String> stream = Stream.of("a", "b", "c");

        // 执行
        Collection<?> result = weaver.convertStreamToCollection(stream, Set.class);

        // 验证
        assertInstanceOf(Set.class, result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList("a", "b", "c")));
    }

    @Test
    @DisplayName("应该将流转换为List集合")
    void convertStreamToCollection_List() {
        // 准备
        Stream<String> stream = Stream.of("a", "b", "c");

        // 执行
        Collection<?> result = weaver.convertStreamToCollection(stream, List.class);

        // 验证
        assertInstanceOf(List.class, result);
        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    @Test
    @DisplayName("应该在不转换类型的情况下写入属性")
    void writeRawProperty_WithoutConversion() throws Exception {
        // 准备
        TestPojo pojo = new TestPojo("original", 25);
        String propertyName = "name";
        Object newValue = "newValue";

        // 执行
        weaver.writeRawProperty(pojo, propertyName, newValue);

        // 验证
        verify(mockPojoAccessor).setPropertyValue(pojo, propertyName, newValue);
        verify(mockTypeConverter, never()).convert(any(), any());
    }

    @Test
    @DisplayName("应该在类型不匹配时进行类型转换")
    void writeConvertedProperty_WithConversion() throws Exception {
        // 准备
        TestPojo pojo = new TestPojo("original", 25);
        String propertyName = "age";
        Object stringValue = "30";
        Integer convertedValue = 30;

        doReturn(Integer.class).when(mockPojoAccessor)
                .getPropertyType(TestPojo.class, propertyName);
        when(mockTypeConverter.convert(stringValue, Integer.class))
                .thenReturn(convertedValue);

        // 执行
        weaver.writeConvertedProperty(pojo, propertyName, stringValue);

        // 验证
        verify(mockTypeConverter).convert(stringValue, Integer.class);
        verify(mockPojoAccessor).setPropertyValue(pojo, propertyName, convertedValue);
    }

    @Test
    @DisplayName("应该在属性不存在时扩展可扩展对象")
    void writeProperty_ExtensibleObject() {
        // 准备
        PropertyExtensible extensiblePojo = mock(PropertyExtensible.class);
        String propertyName = "dynamicProperty";
        Object value = "dynamicValue";

        when(mockPojoAccessor.getPropertyType(any(), eq(propertyName)))
                .thenThrow(new PojoAccessException("Property not found"));

        // 执行
        weaver.writeConvertedProperty(extensiblePojo, propertyName, value);

        // 验证
        verify(extensiblePojo).extendProperty(propertyName, value);
    }

    @Test
    @DisplayName("应该正确处理嵌套的级联对象")
    void recursive_WithCascadeFields() throws Exception {
        // 准备
        CascadedPojo parent = new CascadedPojo();
        TestPojo child = new TestPojo("child", 10);
        List<TestPojo> children = Arrays.asList(
                new TestPojo("child1", 1),
                new TestPojo("child2", 2)
        );

        parent.child = child;
        parent.children = children;

        // 使用反射设置级联字段的值
        Field childField = CascadedPojo.class.getDeclaredField("child");
        Field childrenField = CascadedPojo.class.getDeclaredField("children");

        when(mockPojoAccessor.getPropertyValue(parent, "child")).thenReturn(child);
        when(mockPojoAccessor.getPropertyValue(parent, "children")).thenReturn(children);

        // 收集所有处理的对象
        List<Object> processedObjects = new ArrayList<>();

        // 执行
        weaver.recursive(parent, processedObjects::add);

        // 验证 - 应该处理父对象和所有级联的子对象
        assertEquals(4, processedObjects.size());
        assertTrue(processedObjects.contains(parent));
        assertTrue(processedObjects.contains(child));
        assertTrue(processedObjects.containsAll(children));
    }

    @Test
    @DisplayName("应该正确获取和设置PojoAccessor")
    void getterSetter_PojoAccessor() {
        // 准备
        PojoAccessor newAccessor = mock(PojoAccessor.class);

        // 执行 & 验证
        weaver.setPojoAccessor(newAccessor);
        assertEquals(newAccessor, weaver.getPojoAccessor());
    }

    @Test
    @DisplayName("应该正确获取和设置TypeConverter")
    void getterSetter_TypeConverter() {
        // 准备
        TypeConverter newConverter = mock(TypeConverter.class);

        // 执行 & 验证
        weaver.setTypeConverter(newConverter);
        assertEquals(newConverter, weaver.getTypeConverter());
    }

    @Test
    @DisplayName("应该在遇到不支持的类型时抛出异常")
    void toStringArray_UnsupportedType() {
        // 准备
        Object unsupportedObject = new Object();

        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weaver.toStringArray(unsupportedObject)
        );

        assertTrue(exception.getMessage().contains("Unsupported field value type"));
    }

    @Test
    @DisplayName("应该在遇到不支持的集合类型时抛出异常")
    void convertStreamToCollection_UnsupportedType() {
        // 准备
        Stream<Object> stream = Stream.of("a", "b");

        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weaver.convertStreamToCollection(stream, Map.class)
        );

        assertTrue(exception.getMessage().contains("Unsupported target type"));
    }
}
