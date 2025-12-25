package cn.filaura.weave.cache;

import cn.filaura.weave.cache.dict.DictCacheKeyGenerator;
import cn.filaura.weave.cache.dict.DictCacheManager;
import cn.filaura.weave.dict.DictInfo;
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
class DictCacheManagerTest {

    @Mock
    private CacheOperation cacheOperation;

    @Mock
    private Serializer serializer;

    private DictCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new DictCacheManager(cacheOperation, serializer);
    }

    @Test
    void testBuildCacheKey() {
        // Arrange
        String prefix = "test:prefix";
        String dictCode = "GENDER";

        // Act
        String key = DictCacheManager.buildCacheKey(prefix, dictCode);

        // Assert
        assertEquals("test:prefix:GENDER", key);
    }

    @Test
    void testPutDict_Success() throws SerializationException {
        // Arrange
        Map<String, DictInfo> dictInfoMap = new HashMap<>();

        // 创建性别字典
        Map<String, String> genderData = new HashMap<>();
        genderData.put("M", "男");
        genderData.put("F", "女");
        DictInfo genderDict = new DictInfo("GENDER", genderData);

        // 创建状态字典
        Map<String, String> statusData = new HashMap<>();
        statusData.put("1", "启用");
        statusData.put("0", "禁用");
        DictInfo statusDict = new DictInfo("STATUS", statusData);

        dictInfoMap.put("GENDER", genderDict);
        dictInfoMap.put("STATUS", statusDict);

        when(serializer.serialize(genderDict)).thenReturn("serialized-gender");
        when(serializer.serialize(statusDict)).thenReturn("serialized-status");

        // Act
        cacheManager.putDict(dictInfoMap);

        // Assert
        Map<String, String> expectedCacheMap = new HashMap<>();
        expectedCacheMap.put("weave:dict:GENDER", "serialized-gender");
        expectedCacheMap.put("weave:dict:STATUS", "serialized-status");

        verify(cacheOperation).multiSet(expectedCacheMap);
    }

    @Test
    void testPutDict_EmptyMap() {
        // Act
        cacheManager.putDict(new HashMap<>());

        // Assert
        verify(cacheOperation, never()).multiSet(anyMap());
    }

    @Test
    void testPutDict_NullValuesInMap() throws SerializationException {
        // Arrange
        Map<String, DictInfo> dictInfoMap = new HashMap<>();
        dictInfoMap.put("GENDER", null);

        Map<String, String> statusData = new HashMap<>();
        statusData.put("1", "启用");
        DictInfo statusDict = new DictInfo("STATUS", statusData);
        dictInfoMap.put("STATUS", statusDict);

        when(serializer.serialize(statusDict)).thenReturn("serialized-status");

        // Act
        cacheManager.putDict(dictInfoMap);

        // Assert - 只有非null的值应该被缓存
        verify(cacheOperation).multiSet(
                argThat(map -> map.size() == 1 && map.containsKey("weave:dict:STATUS")));
    }

    @Test
    void testPutDict_NullDataInDictInfo() throws SerializationException {
        // Arrange
        Map<String, DictInfo> dictInfoMap = new HashMap<>();
        DictInfo dictWithNullData = new DictInfo("GENDER", null);
        dictInfoMap.put("GENDER", dictWithNullData);

        when(serializer.serialize(dictWithNullData)).thenReturn("serialized-null-data");

        // Act
        cacheManager.putDict(dictInfoMap);

        // Assert - 即使data为null，DictInfo对象本身也应该被序列化
        verify(cacheOperation).multiSet(
                Collections.singletonMap("weave:dict:GENDER", "serialized-null-data"));
    }

    @Test
    void testPutDict_SerializerException() throws SerializationException {
        // Arrange
        Map<String, DictInfo> dictInfoMap = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        data.put("M", "男");
        DictInfo genderDict = new DictInfo("GENDER", data);
        dictInfoMap.put("GENDER", genderDict);

        when(serializer.serialize(genderDict)).thenThrow(new SerializationException("Serialization failed"));

        // Act & Assert
        assertThrows(SerializationException.class, () -> {
            cacheManager.putDict(dictInfoMap);
        });

        // 确保没有调用缓存操作
        verify(cacheOperation, never()).multiSet(anyMap());
    }

    @Test
    void testLoadDict_Success() throws SerializationException {
        // Arrange
        List<String> dictCodes = Arrays.asList("GENDER", "STATUS");

        // 准备字典数据
        Map<String, String> genderData = new HashMap<>();
        genderData.put("M", "男");
        genderData.put("F", "女");
        DictInfo genderDict = new DictInfo("GENDER", genderData);

        Map<String, String> statusData = new HashMap<>();
        statusData.put("1", "启用");
        statusData.put("0", "禁用");
        DictInfo statusDict = new DictInfo("STATUS", statusData);

        // 模拟缓存响应
        Map<String, String> cacheResponse = new HashMap<>();
        cacheResponse.put("weave:dict:GENDER", "serialized-gender");
        cacheResponse.put("weave:dict:STATUS", "serialized-status");

        when(cacheOperation.multiGet(Arrays.asList("weave:dict:GENDER", "weave:dict:STATUS")))
                .thenReturn(cacheResponse);
        when(serializer.deSerialize("serialized-gender", DictInfo.class))
                .thenReturn(genderDict);
        when(serializer.deSerialize("serialized-status", DictInfo.class))
                .thenReturn(statusDict);

        // Act
        Map<String, DictInfo> result = cacheManager.loadDict(dictCodes);

        // Assert
        assertEquals(2, result.size());
        assertEquals(genderDict, result.get("GENDER"));
        assertEquals(statusDict, result.get("STATUS"));
        assertEquals(2, result.get("GENDER").getData().size());
        assertEquals("男", result.get("GENDER").getData().get("M"));
    }

    @Test
    void testLoadDict_PartialKeysExist() throws SerializationException {
        // Arrange
        List<String> dictCodes = Arrays.asList("GENDER", "STATUS", "NOT_EXIST");

        // 只有部分字典存在
        Map<String, String> genderData = new HashMap<>();
        genderData.put("M", "男");
        DictInfo genderDict = new DictInfo("GENDER", genderData);

        Map<String, String> statusData = new HashMap<>();
        statusData.put("1", "启用");
        DictInfo statusDict = new DictInfo("STATUS", statusData);

        Map<String, String> cacheResponse = new HashMap<>();
        cacheResponse.put("weave:dict:GENDER", "serialized-gender");
        cacheResponse.put("weave:dict:STATUS", "serialized-status");
        // NOT_EXIST 不存在

//        when(cacheOperation.multiGet(Arrays.asList("weave:dict:GENDER", "weave:dict:STATUS", "weave:dict:NOT_EXIST")))
        when(cacheOperation.multiGet(anyList()))
                .thenReturn(cacheResponse);
        when(serializer.deSerialize("serialized-gender", DictInfo.class))
                .thenReturn(genderDict);
        when(serializer.deSerialize("serialized-status", DictInfo.class))
                .thenReturn(statusDict);

        // Act
        Map<String, DictInfo> result = cacheManager.loadDict(dictCodes);

        // Assert
        assertEquals(2, result.size()); // 只返回存在的两个
        assertTrue(result.containsKey("GENDER"));
        assertTrue(result.containsKey("STATUS"));
        assertFalse(result.containsKey("NOT_EXIST"));
    }

    @Test
    void testLoadDict_EmptyList() {
        // Act
        Map<String, DictInfo> result = cacheManager.loadDict(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cacheOperation, never()).multiGet(anyList());
    }

    @Test
    void testLoadDict_NullList() {
        // Act
        Map<String, DictInfo> result = cacheManager.loadDict(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cacheOperation, never()).multiGet(anyList());
    }

    @Test
    void testLoadDict_DeserializeException() throws SerializationException {
        // Arrange
        List<String> dictCodes = Collections.singletonList("GENDER");

        Map<String, String> cacheResponse = new HashMap<>();
        cacheResponse.put("weave:dict:GENDER", "invalid-serialized-data");

        when(cacheOperation.multiGet(Collections.singletonList("weave:dict:GENDER")))
                .thenReturn(cacheResponse);
        when(serializer.deSerialize("invalid-serialized-data", DictInfo.class))
                .thenThrow(new SerializationException("Deserialization failed"));

        // Act & Assert
        assertThrows(SerializationException.class, () -> {
            cacheManager.loadDict(dictCodes);
        });
    }

    @Test
    void testRemoveDict() {
        // Arrange
        List<String> dictCodes = Arrays.asList("GENDER", "STATUS");

        // Act
        cacheManager.removeDict(dictCodes);

        // Assert
        verify(cacheOperation).multiRemove(Arrays.asList(
                "weave:dict:GENDER",
                "weave:dict:STATUS"
        ));
    }

    @Test
    void testRemoveDict_EmptyList() {
        // Act
        cacheManager.removeDict(new ArrayList<>());

        // Assert
        verify(cacheOperation, never()).multiRemove(anyList());
    }

    @Test
    void testRemoveDict_NullList() {
        // Act
        cacheManager.removeDict((List<String>) null);

        // Assert
        verify(cacheOperation, never()).multiRemove(anyList());
    }

    @Test
    void testRemoveSingleDict() {
        // Arrange
        String dictCode = "GENDER";

        // Act
        cacheManager.removeDict(dictCode);

        // Assert
        verify(cacheOperation).remove("weave:dict:GENDER");
    }

    @Test
    void testRemoveSingleDict_NullCode() {
        // Act
        cacheManager.removeDict((String) null);

        // Assert
        verify(cacheOperation, never()).remove(anyString());
    }

    @Test
    void testCustomPrefix() throws SerializationException {
        // Arrange
        cacheManager.setPrefix("myapp:dict");

        Map<String, DictInfo> dictInfoMap = new HashMap<>();
        DictInfo dict = new DictInfo("GENDER");
        dictInfoMap.put("GENDER", dict);

        when(serializer.serialize(dict)).thenReturn("serialized");

        // Act
        cacheManager.putDict(dictInfoMap);

        // Assert
        verify(cacheOperation).multiSet(
                Collections.singletonMap("myapp:dict:GENDER", "serialized"));
    }

    @Test
    void testCustomKeyGenerator() throws SerializationException {
        // Arrange
        DictCacheKeyGenerator customGenerator = new DictCacheKeyGenerator() {
            @Override
            public String generate(String prefix, String dictCode) {
                return prefix + "/dicts/" + dictCode.toLowerCase();
            }
        };

        cacheManager.setKeyGenerator(customGenerator);
        cacheManager.setPrefix("myapp");

        DictInfo dict = new DictInfo("GENDER");
        Map<String, DictInfo> dictMap = new HashMap<>();
        dictMap.put("GENDER", dict);

        when(serializer.serialize(dict)).thenReturn("serialized");

        // Act
        cacheManager.putDict(dictMap);

        // Assert
        verify(cacheOperation).multiSet(
                Collections.singletonMap("myapp/dicts/gender", "serialized"));
    }

    @Test
    void testSetAndGetPrefix() {
        // Arrange
        String newPrefix = "custom:prefix";

        // Act
        cacheManager.setPrefix(newPrefix);
        String prefix = cacheManager.getPrefix();

        // Assert
        assertEquals(newPrefix, prefix);
    }

    @Test
    void testSetAndGetKeyGenerator() {
        // Arrange
        DictCacheKeyGenerator originalGenerator = cacheManager.getKeyGenerator();

        // Act & Assert - 验证默认生成器
        assertNotNull(originalGenerator);
        assertEquals("test:prefix:CODE", originalGenerator.generate("test:prefix", "CODE"));

        // 测试设置新的生成器
        DictCacheKeyGenerator newGenerator = new DictCacheKeyGenerator() {
            @Override
            public String generate(String prefix, String dictCode) {
                return prefix + "-" + dictCode;
            }
        };

        cacheManager.setKeyGenerator(newGenerator);
        assertSame(newGenerator, cacheManager.getKeyGenerator());
    }

    @Test
    void testDefaultConstructorValues() {
        // Act & Assert
        assertEquals("weave:dict", cacheManager.getPrefix());
        assertNotNull(cacheManager.getKeyGenerator());

        // 验证默认键生成器是否正确
        String key = cacheManager.getKeyGenerator().generate("test", "CODE");
        assertEquals("test:CODE", key);
    }

    @Test
    void testGenerateCacheKey_Method() throws Exception {
        // 使用反射测试私有方法buildCacheKey
        Method method = DictCacheManager.class.getDeclaredMethod("generateCacheKey", String.class);
        method.setAccessible(true);

        // Arrange
        String dictCode = "GENDER";

        // Act
        String key = (String) method.invoke(cacheManager, dictCode);

        // Assert
        assertEquals("weave:dict:GENDER", key);
    }

    @Test
    void testConcurrentPutDict() throws InterruptedException, SerializationException {
        // Arrange
        final DictCacheManager manager = new DictCacheManager(cacheOperation, serializer);
        final Map<String, DictInfo> dictMap = new HashMap<>();
        dictMap.put("GENDER", new DictInfo("GENDER"));

        when(serializer.serialize(any(DictInfo.class))).thenReturn("serialized");

        // 创建多个线程同时调用cacheDict
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    manager.putDict(dictMap);
                } catch (Exception e) {
                    fail("Should not throw exception in concurrent access");
                }
            });
        }

        // Act
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - 验证方法被调用了10次
        verify(cacheOperation, times(10)).multiSet(anyMap());
    }

    @Test
    void testDictInfoConstructors() {
        // 测试无参构造函数
        DictInfo dict1 = new DictInfo();
        assertNull(dict1.getCode());
        assertNull(dict1.getData());

        // 测试单参数构造函数
        DictInfo dict2 = new DictInfo("GENDER");
        assertEquals("GENDER", dict2.getCode());
        assertNotNull(dict2.getData());
        assertTrue(dict2.getData().isEmpty());

        // 测试双参数构造函数
        Map<String, String> data = new HashMap<>();
        data.put("M", "男");
        DictInfo dict3 = new DictInfo("GENDER", data);
        assertEquals("GENDER", dict3.getCode());
        assertSame(data, dict3.getData());
    }

    @Test
    void testDictInfoSettersAndGetters() {
        // Arrange
        DictInfo dict = new DictInfo();
        Map<String, String> data = new HashMap<>();
        data.put("M", "男");

        // Act
        dict.setCode("GENDER");
        dict.setData(data);

        // Assert
        assertEquals("GENDER", dict.getCode());
        assertSame(data, dict.getData());
        assertEquals("男", dict.getData().get("M"));
    }
}
