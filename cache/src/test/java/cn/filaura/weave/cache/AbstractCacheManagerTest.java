package cn.filaura.weave.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractCacheManagerTest {

    @Mock
    private CacheOperation cacheOperation;

    @Mock
    private Serializer serializer;

    private TestCacheManager testCacheManager;

    // 测试用的具体实现类
    static class TestCacheManager extends AbstractCacheManager {
        public TestCacheManager(CacheOperation cacheOperation, Serializer serializer) {
            super(cacheOperation, serializer);
        }
    }

    @BeforeEach
    void setUp() {
        testCacheManager = new TestCacheManager(cacheOperation, serializer);
        // 重置静态配置
        AbstractCacheManager.setJitterRatio(0.1);
        AbstractCacheManager.setMaxJitterSeconds(300);
    }

    @Test
    void testMultiGet_EmptyKeys() {
        // Arrange
        List<String> emptyKeys = Collections.emptyList();

        // Act
        Map<String, Map<String, Object>> result = testCacheManager.multiGet(emptyKeys,
                key -> "cache:" + key);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMultiGet_WithKeys() {
        // Arrange
        List<String> originalKeys = Arrays.asList("key1", "key2");
        Map<String, String> cacheResponse = new HashMap<>();
        cacheResponse.put("cache:key1", "{\"field\":\"value1\"}");
        cacheResponse.put("cache:key2", "{\"field\":\"value2\"}");

        Map<String, Object> map1 = new HashMap<>();
        map1.put("field", "value1");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("field", "value2");

        when(cacheOperation.multiGet(Arrays.asList("cache:key1", "cache:key2")))
                .thenReturn(cacheResponse);
        when(serializer.deSerialize("{\"field\":\"value1\"}"))
                .thenReturn(map1);
        when(serializer.deSerialize("{\"field\":\"value2\"}"))
                .thenReturn(map2);

        // Act
        Map<String, Map<String, Object>> result = testCacheManager.multiGet(
                originalKeys, key -> "cache:" + key);

        // Assert
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1").get("field"));
        assertEquals("value2", result.get("key2").get("field"));
    }

    @Test
    void testMultiSet_WithTtl() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        data.put("key1", map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2);
        data.put("key2", map2);

        when(serializer.serialize(map1)).thenReturn("{\"id\":1}");
        when(serializer.serialize(map2)).thenReturn("{\"id\":2}");

        // Act
        testCacheManager.multiSet(data, 3600L, key -> "cache:" + key);

        // Assert
        Map<String, String> expectedCacheMap = new HashMap<>();
        expectedCacheMap.put("cache:key1", "{\"id\":1}");
        expectedCacheMap.put("cache:key2", "{\"id\":2}");

        verify(cacheOperation).multiSet(eq(expectedCacheMap), anyLong());
    }

    @Test
    void testMultiSet_NullTtl() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        data.put("key1", map);

        when(serializer.serialize(map)).thenReturn("{\"id\":1}");

        // Act
        testCacheManager.multiSet(data, key -> "cache:" + key);

        // Assert
        Map<String, String> expected = new HashMap<>();
        expected.put("cache:key1", "{\"id\":1}");
        verify(cacheOperation).multiSet(expected);
    }

    @Test
    void testMultiSet_EmptyMap() {
        // Act
        testCacheManager.multiSet(new HashMap<>(), 3600L, key -> "cache:" + key);

        // Assert
        verify(cacheOperation, never()).multiSet(anyMap(), anyLong());
    }

    @Test
    void testMultiRemove() {
        // Arrange
        List<String> keys = Arrays.asList("key1", "key2");

        // Act
        testCacheManager.multiRemove(keys, key -> "cache:" + key);

        // Assert
        verify(cacheOperation).multiRemove(Arrays.asList("cache:key1", "cache:key2"));
    }

    @Test
    void testRemove() {
        // Act
        testCacheManager.remove("key1", key -> "cache:" + key);

        // Assert
        verify(cacheOperation).remove("cache:key1");
    }

    @Test
    void testAddRandomJitter() throws Exception {
        // 使用反射调用私有方法
        Method method = AbstractCacheManager.class.getDeclaredMethod("addRandomJitter", long.class);
        method.setAccessible(true);

        long ttl = 1000L;

        // 测试多次，确保结果在范围内
        for (int i = 0; i < 100; i++) {
            long result = (long) method.invoke(testCacheManager, ttl);

            assertTrue(result >= ttl, "Result should be >= original TTL");
            assertTrue(result <= ttl + 100, "Result should be <= original TTL + 100 (10% of 1000)");
        }
    }

    @Test
    void testAddRandomJitter_ZeroJitterRatio() throws Exception {
        // Arrange
        AbstractCacheManager.setJitterRatio(0.0);
        Method method = AbstractCacheManager.class.getDeclaredMethod("addRandomJitter", long.class);
        method.setAccessible(true);

        long ttl = 1000L;

        // Act & Assert
        for (int i = 0; i < 10; i++) {
            long result = (long) method.invoke(testCacheManager, ttl);
            assertEquals(ttl, result, "With zero jitter ratio, TTL should remain unchanged");
        }
    }

    @Test
    void testAddRandomJitter_NegativeTtl() throws Exception {
        // Arrange
        Method method = AbstractCacheManager.class.getDeclaredMethod("addRandomJitter", long.class);
        method.setAccessible(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(testCacheManager, -1L);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testJitterConfiguration() {
        // Arrange
        double newRatio = 0.2;
        int newMax = 500;

        // Act
        AbstractCacheManager.setJitterRatio(newRatio);
        AbstractCacheManager.setMaxJitterSeconds(newMax);

        // Assert
        assertEquals(newRatio, AbstractCacheManager.getJitterRatio());
        assertEquals(newMax, AbstractCacheManager.getMaxJitterSeconds());
    }
}