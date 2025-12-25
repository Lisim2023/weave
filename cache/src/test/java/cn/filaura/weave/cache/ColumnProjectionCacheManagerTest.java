package cn.filaura.weave.cache;

import cn.filaura.weave.cache.ref.ColumnProjectionCacheKeyGenerator;
import cn.filaura.weave.cache.ref.ColumnProjectionCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnProjectionCacheManagerTest {

    @Mock
    private CacheOperation cacheOperation;

    @Mock
    private Serializer serializer;

    private ColumnProjectionCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new ColumnProjectionCacheManager(cacheOperation, serializer);
    }

    @Test
    void testBuildCacheKey() {
        // Arrange
        String prefix = "test:prefix";
        String table = "users";
        String keyColumn = "id";
        String id = "123";

        // Act
        String key = ColumnProjectionCacheManager.buildCacheKey(prefix, table, keyColumn, id);

        // Assert
        assertEquals("test:prefix:users:id:123", key);
    }

    @Test
    void testLoadProjections() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        List<String> ids = Arrays.asList("1", "2");

        Map<String, String> cacheResponse = new HashMap<>();
        cacheResponse.put("weave:column_projection:users:id:1", "{\"name\":\"Alice\",\"age\":30}");
        cacheResponse.put("weave:column_projection:users:id:2", "{\"name\":\"Bob\",\"age\":25}");

        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "Alice");
        user1.put("age", 30);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "Bob");
        user2.put("age", 25);

        when(cacheOperation.multiGet(Arrays.asList("weave:column_projection:users:id:1", "weave:column_projection:users:id:2")))
                .thenReturn(cacheResponse);
        when(serializer.deSerialize("{\"name\":\"Alice\",\"age\":30}"))
                .thenReturn(user1);
        when(serializer.deSerialize("{\"name\":\"Bob\",\"age\":25}"))
                .thenReturn(user2);

        // Act
        Map<String, Map<String, Object>> result = cacheManager.loadProjections(table, keyColumn, ids);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Alice", result.get("1").get("name"));
        assertEquals("Bob", result.get("2").get("name"));
    }

    @Test
    void testLoadProjections_EmptyIds() {
        // Act
        Map<String, Map<String, Object>> result = cacheManager.loadProjections("users", "id", new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testPutProjections_WithGlobalTtl() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        Map<String, Map<String, Object>> recordMap = new HashMap<>();

        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "Alice");
        user1.put("age", 30);
        recordMap.put("1", user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "Bob");
        user2.put("age", 25);
        recordMap.put("2", user2);

        cacheManager.setDefaultTtlSeconds(7200L); // 2小时

        when(serializer.serialize(user1)).thenReturn("{\"name\":\"Alice\",\"age\":30}");
        when(serializer.serialize(user2)).thenReturn("{\"name\":\"Bob\",\"age\":25}");

        // Act
        cacheManager.putProjections(table, keyColumn, recordMap);

        // Assert
        Map<String, String> expectedCacheMap = new HashMap<>();
        expectedCacheMap.put("weave:column_projection:users:id:1", "{\"name\":\"Alice\",\"age\":30}");
        expectedCacheMap.put("weave:column_projection:users:id:2", "{\"name\":\"Bob\",\"age\":25}");

        verify(cacheOperation).multiSet(eq(expectedCacheMap), anyLong());
    }

    @Test
    void testPutProjections_WithTableSpecificTtl() throws Exception {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        Map<String, Map<String, Object>> recordMap = new HashMap<>();

        Map<String, Object> user = new HashMap<>();
        user.put("name", "Alice");
        recordMap.put("1", user);

        // 使用反射设置tableTtlMap
        Field tableTtlMapField = ColumnProjectionCacheManager.class.getDeclaredField("ttlByTable");
        tableTtlMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Long> tableTtlMap = (Map<String, Long>) tableTtlMapField.get(cacheManager);
        tableTtlMap.put("users", 1800L); // 30分钟

        when(serializer.serialize(user)).thenReturn("{\"name\":\"Alice\"}");

        // Act
        cacheManager.putProjections(table, keyColumn, recordMap);

        // Assert
        verify(cacheOperation).multiSet(
                argThat(map -> map.containsKey("weave:column_projection:users:id:1")),
                anyLong());
    }

    @Test
    void testPutProjections_NullValueInMap() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        Map<String, Map<String, Object>> recordMap = new HashMap<>();
        recordMap.put("1", null);
        recordMap.put("2", new HashMap<>());

        cacheManager.setDefaultTtlSeconds(7200L);

        when(serializer.serialize(new HashMap<>())).thenReturn("{}");

        // Act
        cacheManager.putProjections(table, keyColumn, recordMap);

        // Assert - 只有非null的值应该被缓存
        verify(cacheOperation).multiSet(
                argThat(map -> map.size() == 1 && map.containsKey("weave:column_projection:users:id:2")),
                anyLong());
    }

    @Test
    void testPutProjections_ZeroTtl() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        Map<String, Map<String, Object>> recordMap = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Alice");
        recordMap.put("1", user);

        cacheManager.setDefaultTtlSeconds(0); // TTL为0

        // Act
        cacheManager.putProjections(table, keyColumn, recordMap);

        // Assert - 当TTL为0时，不应该调用multiSet
        verify(cacheOperation, never()).multiSet(anyMap(), anyLong());
    }

    @Test
    void testPutProjections_NegativeTtl() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        Map<String, Map<String, Object>> recordMap = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Alice");
        recordMap.put("1", user);

        // 使用反射设置负的TTL
        cacheManager.setDefaultTtlSeconds(-1);

        // Act
        cacheManager.putProjections(table, keyColumn, recordMap);

        // Assert - 负TTL不应该缓存
        verify(cacheOperation, never()).multiSet(anyMap(), anyLong());
    }

    @Test
    void testRemoveProjections() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        List<String> ids = Arrays.asList("1", "2");

        // Act
        cacheManager.removeProjections(table, keyColumn, ids);

        // Assert
        verify(cacheOperation).multiRemove(Arrays.asList(
                "weave:column_projection:users:id:1",
                "weave:column_projection:users:id:2"
        ));
    }

    @Test
    void testRemoveProjection() {
        // Arrange
        String table = "users";
        String keyColumn = "id";
        String id = "1";

        // Act
        cacheManager.removeProjection(table, keyColumn, id);

        // Assert
        verify(cacheOperation).remove("weave:column_projection:users:id:1");
    }

    @Test
    void testCustomPrefix() {
        // Arrange
        cacheManager.setPrefix("custom:prefix");
        String table = "users";
        String keyColumn = "id";
        String id = "123";

        // Act
        cacheManager.removeProjection(table, keyColumn, id);

        // Assert
        verify(cacheOperation).remove("custom:prefix:users:id:123");
    }

    @Test
    void testCustomKeyGenerator() {
        // Arrange
        ColumnProjectionCacheKeyGenerator customGenerator = new ColumnProjectionCacheKeyGenerator() {
            @Override
            public String generateKey(String prefix, String table, String keyColumn, String id) {
                return prefix + "/" + table + "/" + keyColumn + "/" + id;
            }
        };

        cacheManager.setKeyGenerator(customGenerator);
        cacheManager.setPrefix("custom");

        // Act
        String key = cacheManager.getKeyGenerator().generateKey(
                cacheManager.getPrefix(), "users", "id", "123");

        // Assert
        assertEquals("custom/users/id/123", key);
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        cacheManager.setPrefix("new:prefix");
        cacheManager.setDefaultTtlSeconds(3600L);

        // Act & Assert
        assertEquals("new:prefix", cacheManager.getPrefix());
        assertEquals(3600L, cacheManager.getDefaultTtlSeconds());
    }
}
