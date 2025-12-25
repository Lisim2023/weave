package cn.filaura.weave.ref;

import cn.filaura.weave.entity.TestRole;
import cn.filaura.weave.entity.TestUser_RecordEmbed;
import cn.filaura.weave.exception.ReferenceDataNotFoundException;
import cn.filaura.weave.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("RecordEmbedWeaver 单元测试")
class RecordEmbedWeaverTest {

    private RecordEmbedWeaver recordEmbedWeaver;
    private TestUser_RecordEmbed testUser;

    @BeforeEach
    void setUp() {
        recordEmbedWeaver = new RecordEmbedWeaver();
        testUser = new TestUser_RecordEmbed();
    }

    @Test
    @DisplayName("应该收集单值外键的引用信息")
    void shouldCollectSingleValueForeignKeyReferenceInfo() {
        // 准备
        testUser.setRoleId(100L);

        // 执行
        Map<String, ServiceQuery> result = recordEmbedWeaver.collectReferenceInfo(testUser);

        // 验证
        assertEquals(1, result.size());

        String expectedKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery serviceQuery = result.get(expectedKey);
        assertNotNull(serviceQuery);
        assertEquals(1, serviceQuery.getIds().size());
        assertTrue(serviceQuery.getIds().contains(100L));
    }

    @Test
    @DisplayName("应该收集列表类型外键的引用信息")
    void shouldCollectListTypeForeignKeyReferenceInfo() {
        // 准备
        List<Long> roleIds = Arrays.asList(101L, 102L, 103L);
        testUser.setRoleIdList(roleIds);

        // 执行
        Map<String, ServiceQuery> result = recordEmbedWeaver.collectReferenceInfo(testUser);

        // 验证
        assertEquals(1, result.size());

        String expectedKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery serviceQuery = result.get(expectedKey);
        assertNotNull(serviceQuery);
        assertEquals(3, serviceQuery.getIds().size());
        assertTrue(serviceQuery.getIds().containsAll(roleIds));
    }

    @Test
    @DisplayName("应该收集数组类型外键的引用信息")
    void shouldCollectArrayTypeForeignKeyReferenceInfo() {
        // 准备
        Long[] roleIds = {201L, 202L, 203L};
        testUser.setRoleIdArray(roleIds);

        // 执行
        Map<String, ServiceQuery> result = recordEmbedWeaver.collectReferenceInfo(testUser);

        // 验证
        assertEquals(1, result.size());

        String expectedKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery serviceQuery = result.get(expectedKey);
        assertNotNull(serviceQuery);
        assertEquals(3, serviceQuery.getIds().size());
        assertTrue(serviceQuery.getIds().containsAll(Arrays.asList(roleIds)));
    }

    @Test
    @DisplayName("应该编织单值结果到目标字段")
    void shouldWeaveSingleValueResultToTargetField() {
        // 准备
        testUser.setRoleId(300L);

        Map<String, ServiceQuery> queryMap = recordEmbedWeaver.collectReferenceInfo(testUser);
        String mapKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");

        TestRole expectedRole = new TestRole(300L, "Admin", 1);
        Map<String, Object> results = new HashMap<>();
        results.put("300", expectedRole);

        ServiceResult serviceResult = new ServiceResult(queryMap.get(mapKey), results);
        Map<String, ServiceResult> resultMap = new HashMap<>();
        resultMap.put(mapKey, serviceResult);

        // 执行
        recordEmbedWeaver.weave(testUser, resultMap);

        // 验证
        assertNotNull(testUser.getTestRole());
        assertEquals(expectedRole, testUser.getTestRole());
    }

    @Test
    @DisplayName("应该编织列表类型结果到目标字段")
    void shouldWeaveListTypeResultToTargetField() {
        // 准备
        List<Long> roleIds = Arrays.asList(401L, 402L, 403L);
        testUser.setRoleIdList(roleIds);

        Map<String, ServiceQuery> queryMap = recordEmbedWeaver.collectReferenceInfo(testUser);
        String mapKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");

        TestRole role1 = new TestRole(401L, "User", 2);
        TestRole role2 = new TestRole(402L, "Manager", 3);
        TestRole role3 = new TestRole(403L, "Admin", 1);

        Map<String, Object> results = new HashMap<>();
        results.put("401", role1);
        results.put("402", role2);
        results.put("403", role3);

        ServiceResult serviceResult = new ServiceResult(queryMap.get(mapKey), results);
        Map<String, ServiceResult> resultMap = new HashMap<>();
        resultMap.put(mapKey, serviceResult);

        // 执行
        recordEmbedWeaver.weave(testUser, resultMap);

        // 验证
        assertNotNull(testUser.getTestRoleList());
        assertEquals(3, testUser.getTestRoleList().size());
        assertTrue(testUser.getTestRoleList().containsAll(Arrays.asList(role1, role2, role3)));
    }

    @Test
    @DisplayName("应该编织数组类型结果到目标字段")
    void shouldWeaveArrayTypeResultToTargetField() {
        // 准备
        Long[] roleIds = {501L, 502L};
        testUser.setRoleIdArray(roleIds);

        Map<String, ServiceQuery> queryMap = recordEmbedWeaver.collectReferenceInfo(testUser);
        String mapKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");

        TestRole role1 = new TestRole(501L, "Developer", 2);
        TestRole role2 = new TestRole(502L, "Tester", 2);

        Map<String, Object> results = new HashMap<>();
        results.put("501", role1);
        results.put("502", role2);

        ServiceResult serviceResult = new ServiceResult(queryMap.get(mapKey), results);
        Map<String, ServiceResult> resultMap = new HashMap<>();
        resultMap.put(mapKey, serviceResult);

        // 执行
        recordEmbedWeaver.weave(testUser, resultMap);

        // 验证
        assertNotNull(testUser.getTestRoleArray());
        assertEquals(2, testUser.getTestRoleArray().length);
        assertEquals(role1, testUser.getTestRoleArray()[0]);
        assertEquals(role2, testUser.getTestRoleArray()[1]);
    }

    @Test
    @DisplayName("当忽略缺失配置为true时应该跳过缺失的结果")
    void shouldSkipMissingResultWhenIgnoreMissingIsTrue() {
        // 准备
        Long[] roleIds = {601L, 602L}; // 602L 在结果中不存在
        testUser.setRoleIdArray(roleIds);

        Map<String, ServiceQuery> queryMap = recordEmbedWeaver.collectReferenceInfo(testUser);
        String mapKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");

        TestRole role1 = new TestRole(601L, "ExistingRole", 1);

        Map<String, Object> results = new HashMap<>();
        results.put("601", role1);
        // 602L 对应的结果缺失

        ServiceResult serviceResult = new ServiceResult(queryMap.get(mapKey), results);
        Map<String, ServiceResult> resultMap = new HashMap<>();
        resultMap.put(mapKey, serviceResult);

        // 执行 - 不应该抛出异常
        assertDoesNotThrow(() -> recordEmbedWeaver.weave(testUser, resultMap));

        // 验证
        assertNotNull(testUser.getTestRoleArray());
        assertEquals(1, testUser.getTestRoleArray().length);
        assertEquals(role1, testUser.getTestRoleArray()[0]);
    }

    @Test
    @DisplayName("当忽略缺失配置为false且结果不存在时应该抛出异常")
    void shouldThrowExceptionWhenIgnoreMissingIsFalseAndResultNotFound() {
        // 准备 - roleId字段的ignoreMissing默认为false
        testUser.setRoleId(700L);

        Map<String, ServiceQuery> queryMap = recordEmbedWeaver.collectReferenceInfo(testUser);
        String mapKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");

        // 空结果
        Map<String, Object> results = new HashMap<>();
        ServiceResult serviceResult = new ServiceResult(queryMap.get(mapKey), results);
        Map<String, ServiceResult> resultMap = new HashMap<>();
        resultMap.put(mapKey, serviceResult);

        // 执行 & 验证
        assertThrows(ReferenceDataNotFoundException.class,
                () -> recordEmbedWeaver.weave(testUser, resultMap)
        );
    }

    @Test
    @DisplayName("当结果映射为空时应该拋出异常")
    void shouldThrowExceptionWhenResultMapIsEmpty() {
        // 准备
        testUser.setRoleId(800L);

        Map<String, ServiceResult> emptyResultMap = new HashMap<>();

        // 执行 - 不应该抛出异常
        assertThrows(ReferenceDataNotFoundException.class,
                () -> recordEmbedWeaver.weave(testUser, emptyResultMap));

        // 验证 - 目标字段应该保持为null
        assertNull(testUser.getTestRole());
    }

    @Test
    @DisplayName("当外键值为null时应该跳过收集")
    void shouldSkipCollectionWhenForeignKeyValueIsNull() {
        // 准备 - roleId为null
        testUser.setRoleId(null);

        // 执行
        Map<String, ServiceQuery> result = recordEmbedWeaver.collectReferenceInfo(testUser);

        // 验证
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("应该处理多个对象的引用信息收集")
    void shouldCollectReferenceInfoForMultipleObjects() {
        // 准备
        TestUser_RecordEmbed user1 = new TestUser_RecordEmbed();
        user1.setRoleId(901L);

        TestUser_RecordEmbed user2 = new TestUser_RecordEmbed();
        user2.setRoleId(902L);

        List<TestUser_RecordEmbed> users = Arrays.asList(user1, user2);

        // 执行
        Map<String, ServiceQuery> result = recordEmbedWeaver.collectReferenceInfo(users);

        // 验证
        assertEquals(1, result.size());

        String expectedKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery serviceQuery = result.get(expectedKey);
        assertNotNull(serviceQuery);
        assertEquals(2, serviceQuery.getIds().size());
        assertTrue(serviceQuery.getIds().contains(901L));
        assertTrue(serviceQuery.getIds().contains(902L));
    }

    @Test
    @DisplayName("应该处理空集合的外键值")
    void shouldHandleEmptyCollectionForeignKeyValue() {
        // 准备
        testUser.setRoleIdList(Collections.emptyList());

        // 执行
        Map<String, ServiceQuery> result = recordEmbedWeaver.collectReferenceInfo(testUser);

        // 验证 - 空集合应该被移除
        assertEquals(0, result.size());
    }
}
