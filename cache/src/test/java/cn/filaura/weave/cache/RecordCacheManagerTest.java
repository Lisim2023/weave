package cn.filaura.weave.cache;

import cn.filaura.weave.cache.ref.RecordCacheKeyGenerator;
import cn.filaura.weave.cache.ref.RecordCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordCacheManagerTest {

    @Mock
    private CacheOperation cacheOperation;

    @Mock
    private Serializer serializer;

    private RecordCacheManager cacheManager;

    // 测试用的记录类
    static class User {
        private String id;
        private String name;
        private int age;

        public User() {}

        public User(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return age == user.age &&
                    Objects.equals(id, user.id) &&
                    Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, age);
        }
    }

    @BeforeEach
    void setUp() {
        cacheManager = new RecordCacheManager(cacheOperation, serializer);
    }

    @Test
    void testBuildCacheKey() {
        // Arrange
        String prefix = "test:prefix";
        Class<User> userClass = User.class;
        String id = "123";

        // Act
        String key = RecordCacheManager.buildCacheKey(prefix, userClass, id);

        // Assert
        assertEquals("test:prefix:User:123", key);
    }

    @Test
    void testLoadRecords() {
        // Arrange
        List<String> ids = Arrays.asList("1", "2");
        Class<User> userClass = User.class;

        User user1 = new User("1", "Alice", 30);
        User user2 = new User("2", "Bob", 25);

        Map<String, String> cacheResponse = new HashMap<>();
        cacheResponse.put("weave:record:User:1", "serialized-user1");
        cacheResponse.put("weave:record:User:2", "serialized-user2");

//        when(cacheOperation.multiGet(Arrays.asList("weave:record:User:1", "weave:record:User:2")))
        when(cacheOperation.multiGet(anyList()))
                .thenReturn(cacheResponse);
        when(serializer.deSerialize("serialized-user1", User.class))
                .thenReturn(user1);
        when(serializer.deSerialize("serialized-user2", User.class))
                .thenReturn(user2);

        // Act
        Map<String, User> result = cacheManager.loadRecords(ids, userClass);

        // Assert
        assertEquals(2, result.size());
        assertEquals(user1, result.get("1"));
        assertEquals(user2, result.get("2"));
    }

    @Test
    void testLoadRecords_EmptyResult() {
        // Arrange
        List<String> ids = Arrays.asList("1", "2");
        Class<User> userClass = User.class;

        when(cacheOperation.multiGet(anyList())).thenReturn(new HashMap<>());

        // Act
        Map<String, User> result = cacheManager.loadRecords(ids, userClass);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testPutRecords() {
        // Arrange
        Map<String, User> userMap = new HashMap<>();
        userMap.put("1", new User("1", "Alice", 30));
        userMap.put("2", new User("2", "Bob", 25));

        cacheManager.setDefaultTtlSeconds(7200L);

        when(serializer.serialize(any(User.class))).thenReturn("serialized-user");

        // Act
        cacheManager.putRecords(userMap, User.class);

        // Assert
        verify(cacheOperation).multiSet(
                argThat(map -> map.size() == 2 &&
                        map.containsKey("weave:record:User:1") &&
                        map.containsKey("weave:record:User:2")),
                anyLong());
    }

    @Test
    void testPutRecords_WithClassSpecificTtl() throws Exception {
        // Arrange
        Map<String, User> userMap = new HashMap<>();
        userMap.put("1", new User("1", "Alice", 30));

        // 设置classTtl
        cacheManager.registerTtl(User.class.getName(), 900L); // 15分钟

        when(serializer.serialize(any(User.class))).thenReturn("serialized-user");

        // Act
        cacheManager.putRecords(userMap, User.class);

        // Assert
        verify(cacheOperation).multiSet(anyMap(), anyLong());
    }

    @Test
    void testPutRecords_NullValues() {
        // Arrange
        Map<String, User> userMap = new HashMap<>();
        userMap.put("1", null);
        userMap.put("2", new User("2", "Bob", 25));

        cacheManager.setDefaultTtlSeconds(7200L);

        when(serializer.serialize(any(User.class))).thenReturn("serialized-user");

        // Act
        cacheManager.putRecords(userMap, User.class);

        // Assert - 只有非null的值应该被缓存
        verify(cacheOperation).multiSet(
                argThat(map -> map.size() == 1 && map.containsKey("weave:record:User:2")),
                anyLong());
    }

    @Test
    void testRemoveRecords() {
        // Arrange
        List<String> ids = Arrays.asList("1", "2");

        // Act
        cacheManager.removeRecords(ids, User.class);

        // Assert
        verify(cacheOperation).multiRemove(Arrays.asList(
                "weave:record:User:1",
                "weave:record:User:2"
        ));
    }

    @Test
    void testRemoveRecord() {
        // Arrange
        String id = "1";

        // Act
        cacheManager.removeRecord(id, User.class);

        // Assert
        verify(cacheOperation).remove("weave:record:User:1");
    }

    @Test
    void testCustomKeyGenerator() {
        // Arrange
        RecordCacheKeyGenerator customGenerator = new RecordCacheKeyGenerator() {
            @Override
            public String generateKey(String prefix, Class<?> recordType, String id) {
                return prefix + "-" + recordType.getSimpleName().toLowerCase() + "-" + id;
            }
        };

        cacheManager.setKeyGenerator(customGenerator);
        cacheManager.setPrefix("mycache");

        // Act
        cacheManager.removeRecord("123", User.class);

        // Assert
        verify(cacheOperation).remove("mycache-user-123");
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
