package cn.filaura.weave.ref;

import cn.filaura.weave.exception.DataLoadingException;
import cn.filaura.weave.exception.MethodNotFoundException;
import cn.filaura.weave.exception.ServiceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringBeanMethodInvokerTest {

    @Mock
    private ApplicationContext applicationContext;

    private SpringBeanMethodInvoker springBeanMethodInvoker;

    // 测试服务接口
    public interface TestService {
        List<String> findByIds(List<Long> ids);
        List<Integer> findByCodes(List<String> codes);
        String singleMethod(List<Object> items); // 返回非Collection类型
        void voidMethod(List<Object> items); // void返回类型
    }

    // 测试服务实现
    public static class TestServiceImpl implements TestService {
        @Override
        public List<String> findByIds(List<Long> ids) {
            return Arrays.asList("item1", "item2");
        }

        @Override
        public List<Integer> findByCodes(List<String> codes) {
            return Arrays.asList(1, 2, 3);
        }

        @Override
        public String singleMethod(List<Object> items) {
            return "single result";
        }

        @Override
        public void voidMethod(List<Object> items) {
            // void方法实现
        }
    }

    @BeforeEach
    void setUp() {
        springBeanMethodInvoker = new SpringBeanMethodInvoker(applicationContext);
    }

    @Test
    void testInvokeMethodServiceMethod_Success() {
        // 准备
        TestService testService = new TestServiceImpl();
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);
        List<Object> keys = Arrays.asList(1L, 2L, 3L);

        // 执行
        Collection<?> result = springBeanMethodInvoker.invokeServiceMethod(
                TestService.class, "findByIds", keys);

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        verify(applicationContext).getBean(TestService.class);
    }

    @Test
    void testInvokeMethodServiceMethod_ServiceNotFound() {
        // 准备
        when(applicationContext.getBean(TestService.class))
                .thenThrow(new RuntimeException("Bean not found"));

        // 执行 & 验证
        ServiceNotFoundException exception = assertThrows(ServiceNotFoundException.class, () ->
                springBeanMethodInvoker.invokeServiceMethod(TestService.class, "findByIds", Collections.emptyList()));

        assertTrue(exception.getMessage().contains("Service not found"));
    }

    @Test
    void testInvokeMethodServiceMethod_MethodNotFound() {
        // 准备
        TestService testService = new TestServiceImpl();
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // 执行 & 验证
        MethodNotFoundException exception = assertThrows(MethodNotFoundException.class, () ->
                springBeanMethodInvoker.invokeServiceMethod(TestService.class, "nonExistentMethod", Collections.emptyList()));

        assertTrue(exception.getMessage().contains("Method not found"));
    }

    @Test
    void testInvokeMethodServiceMethod_NonCollectionReturnType() {
        // 准备
        TestService testService = new TestServiceImpl();
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // 执行 & 验证
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                springBeanMethodInvoker.invokeServiceMethod(TestService.class, "singleMethod", Collections.emptyList()));

        assertNotNull(exception);
    }

    @Test
    void testInvokeMethodServiceMethod_MethodInvocationException() throws Exception {
        // 准备
        TestService testService = mock(TestService.class);
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        List<Long> testKeys = Arrays.asList(1L, 2L);
        when(testService.findByIds(any())).thenThrow(new RuntimeException("Test exception"));

        // 执行 & 验证
        DataLoadingException exception = assertThrows(DataLoadingException.class, () ->
                springBeanMethodInvoker.invokeServiceMethod(TestService.class, "findByIds", testKeys));

        assertTrue(exception.getMessage().contains("Failed to invoke method"));
        assertNotNull(exception.getCause());
    }

    @Test
    void testGetMethodReturnElementType_Success() {
        // 准备
//        TestService testService = new TestServiceImpl();
//        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // 执行
        Class<?> elementType = springBeanMethodInvoker.getMethodReturnElementType(
                TestService.class, "findByIds");

        // 验证
        assertEquals(String.class, elementType);
    }

    @Test
    void testGetMethodReturnElementType_DifferentGenericType() {
        // 准备
//        TestService testService = new TestServiceImpl();
//        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // 执行
        Class<?> elementType = springBeanMethodInvoker.getMethodReturnElementType(
                TestService.class, "findByCodes");

        // 验证
        assertEquals(Integer.class, elementType);
    }

    @Test
    void testGetMethodReturnElementType_NonParameterizedReturnType() {
        // 准备
//        TestService testService = new TestServiceImpl();
//        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // 执行 & 验证
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                springBeanMethodInvoker.getMethodReturnElementType(TestService.class, "singleMethod"));

        assertTrue(exception.getMessage().contains("Cannot determine return element type"));
    }

    @Test
    void testMethodCaching() {
        // 准备
        TestService testService = new TestServiceImpl();
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);
        List<Object> keys = Arrays.asList(1L, 2L);

        // 第一次调用
        springBeanMethodInvoker.invokeServiceMethod(TestService.class, "findByIds", keys);

        // 第二次调用 - 应该使用缓存
        springBeanMethodInvoker.invokeServiceMethod(TestService.class, "findByIds", keys);

        // 验证applicationContext.getBean只被调用一次（服务实例缓存）
        verify(applicationContext, times(1)).getBean(TestService.class);

        // 方法查找也应该被缓存，但我们无法直接验证内部缓存
        // 可以通过性能测试或间接验证
    }

    @Test
    void testServiceInstanceCaching() {
        // 准备
        TestService testService = new TestServiceImpl();
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // 多次调用相同服务
        springBeanMethodInvoker.invokeServiceMethod(TestService.class, "findByIds", Collections.emptyList());
        springBeanMethodInvoker.invokeServiceMethod(TestService.class, "findByCodes", Collections.emptyList());

        // 验证applicationContext.getBean只被调用一次
        verify(applicationContext, times(1)).getBean(TestService.class);
    }

    @Test
    void testMethodKeyEqualsAndHashCode() {
        // 准备
        SpringBeanMethodInvoker.ServiceKey key1 = new SpringBeanMethodInvoker.ServiceKey(TestService.class, "findByIds");
        SpringBeanMethodInvoker.ServiceKey key2 = new SpringBeanMethodInvoker.ServiceKey(TestService.class, "findByIds");
        SpringBeanMethodInvoker.ServiceKey key3 = new SpringBeanMethodInvoker.ServiceKey(TestService.class, "findByCodes");

        // 验证
        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
        assertNotEquals(key1, key3);
        assertNotEquals(key1.hashCode(), key3.hashCode());

        // 自反性
        assertEquals(key1, key1);

        // 与null比较
        assertNotEquals(null, key1);

        // 与不同类型比较
        assertNotEquals("string", key1);
    }

}
