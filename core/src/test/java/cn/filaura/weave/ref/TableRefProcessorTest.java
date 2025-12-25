package cn.filaura.weave.ref;

import cn.filaura.weave.entity.TestUser_TableRef;
import cn.filaura.weave.exception.ReferenceDataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TableRefProcessorTest {

    private TableRefProcessor weaver;

    @BeforeEach
    void setUp() {
        weaver = new TableRefProcessor();
    }



    @Nested
    @DisplayName("collectReferenceInfo方法测试")
    class CollectReferenceInfoTest {

        @Test
        @DisplayName("当传入带有注解的POJO列表时，应该正确收集引用信息")
        void shouldCollectReferenceInfoForAnnotatedPojos() {
            // Given
            List<TestUser_TableRef> users = Arrays.asList(
                    createTestUser(1L, 100L, 10L),
                    createTestUser(2L, 101L, 11L)
            );

            // When
            Map<String, TableQuery> result = weaver.collectReferenceInfo(users);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());

            // 检查sys_role查询
            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = result.get(roleKey);
            assertNotNull(roleQuery);
            assertEquals("sys_role", roleQuery.getTable());
            assertEquals("id", roleQuery.getKeyColumn());
            assertTrue(roleQuery.getIds().contains(100L));
            assertTrue(roleQuery.getIds().contains(101L));
            assertTrue(roleQuery.getColumns().contains("name"));
            assertTrue(roleQuery.getColumns().contains("level"));
            assertTrue(roleQuery.getColumns().contains("id"));

            // 检查sys_department查询
            String deptKey = TableRefProcessor.buildMapKey("sys_department", "department_id");
            TableQuery deptQuery = result.get(deptKey);
            assertNotNull(deptQuery);
            assertEquals("sys_department", deptQuery.getTable());
            assertEquals("department_id", deptQuery.getKeyColumn());
            assertTrue(deptQuery.getIds().contains(10L));
            assertTrue(deptQuery.getIds().contains(11L));
            assertTrue(deptQuery.getColumns().contains("name"));
            assertTrue(deptQuery.getColumns().contains("department_id"));
        }

        @Test
        @DisplayName("当传入单个POJO对象时，应该正确收集引用信息")
        void shouldCollectReferenceInfoForSinglePojo() {
            // Given
            TestUser_TableRef user = createTestUser(1L, 100L, 10L);

            // When
            Map<String, TableQuery> result = weaver.collectReferenceInfo(user);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("当传入空对象时，应该返回空映射")
        void shouldReturnEmptyMapForNullInput() {
            // When
            Map<String, TableQuery> result = weaver.collectReferenceInfo(null);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("collectFieldValues方法测试")
    class CollectFieldValuesTest {

        @Test
        @DisplayName("当传入POJO列表时，应该正确填充DbQuery映射")
        void shouldCollectFieldValuesForPojos() {
            // Given
            List<TestUser_TableRef> users = Arrays.asList(
                    createTestUser(1L, 100L, 10L),
                    createTestUser(2L, 101L, 11L)
            );

            // When
            Map<String, TableQuery> dbQueryMap = weaver.collectReferenceInfo(users);

            // Then
            assertEquals(2, dbQueryMap.size());

            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = dbQueryMap.get(roleKey);
            assertNotNull(roleQuery);
            assertEquals(2, roleQuery.getIds().size());
            assertTrue(roleQuery.getIds().contains(100L));
            assertTrue(roleQuery.getIds().contains(101L));

            String deptKey = TableRefProcessor.buildMapKey("sys_department", "department_id");
            TableQuery deptQuery = dbQueryMap.get(deptKey);
            assertNotNull(deptQuery);
            assertEquals(2, deptQuery.getIds().size());
            assertTrue(deptQuery.getIds().contains(10L));
            assertTrue(deptQuery.getIds().contains(11L));
        }

        @Test
        @DisplayName("当传入有空外键的POJO时，应该跳过空值")
        void shouldSkipNullForeignKeyValues() {
            // Given
            TestUser_TableRef user = createTestUser(1L, null, null);

            // When
            Map<String, TableQuery> dbQueryMap = weaver.collectReferenceInfo(user);

            // Then
            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = dbQueryMap.get(roleKey);
            assertNull(roleQuery);

            String deptKey = TableRefProcessor.buildMapKey("sys_department", "department_id");
            TableQuery deptQuery = dbQueryMap.get(deptKey);
            assertNull(deptQuery);

            assertTrue(dbQueryMap.isEmpty());
        }
    }

    @Nested
    @DisplayName("weave方法测试")
    class WeaveTest {

        @Test
        @DisplayName("当传入有效的结果映射时，应该正确编织数据")
        void shouldWeaveDataWithValidResultMap() {
            // Given
            TestUser_TableRef user = createTestUser(1L, 100L, 10L);

            // 准备角色结果
            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = new TableQuery("sys_role", "id");
            Map<String, Map<String, Object>> roleResults = new HashMap<>();

            Map<String, Object> roleRecord = new HashMap<>();
            roleRecord.put("name", "管理员");
            roleRecord.put("level", 1);
            roleRecord.put("id", 100L);
            roleResults.put("100", roleRecord);

            TableResult roleResult = new TableResult(roleQuery, roleResults);

            // 准备部门结果
            String deptKey = TableRefProcessor.buildMapKey("sys_department", "department_id");
            TableQuery deptQuery = new TableQuery("sys_department", "department_id");
            Map<String, Map<String, Object>> deptResults = new HashMap<>();

            Map<String, Object> deptRecord = new HashMap<>();
            deptRecord.put("name", "技术部");
            deptRecord.put("department_id", 10L);
            deptResults.put("10", deptRecord);

            TableResult deptResult = new TableResult(deptQuery, deptResults);

            Map<String, TableResult> dbResultMap = new HashMap<>();
            dbResultMap.put(roleKey, roleResult);
            dbResultMap.put(deptKey, deptResult);

            // When
            weaver.weave(user, dbResultMap);

            // Then
            assertEquals("管理员", user.getRoleName());
            assertEquals(1, user.getRoleLevel());
            assertEquals("技术部", user.getDepartmentName());
        }

        @Test
        @DisplayName("当ignoreMissing为true且结果不存在时，应该跳过而不抛出异常")
        void shouldSkipWhenIgnoreMissingAndResultNotFound() {
            // Given
            TestUser_TableRef user = createTestUser(1L, 100L, 10L);

            // 只提供角色结果，不提供部门结果（但部门设置了ignoreMissing = true）
            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = new TableQuery("sys_role", "id");
            Map<String, Map<String, Object>> roleResults = new HashMap<>();

            Map<String, Object> roleRecord = new HashMap<>();
            roleRecord.put("name", "管理员");
            roleRecord.put("level", 1);
            roleRecord.put("id", 100L);
            roleResults.put("100", roleRecord);

            TableResult roleResult = new TableResult(roleQuery, roleResults);

            Map<String, TableResult> dbResultMap = new HashMap<>();
            dbResultMap.put(roleKey, roleResult);

            // When & Then - 不应该抛出异常
            assertDoesNotThrow(() -> weaver.weave(user, dbResultMap));

            // 验证角色数据被正确编织
            assertEquals("管理员", user.getRoleName());
            assertEquals(1, user.getRoleLevel());
            // 部门名称应该保持为null（因为部门结果不存在且ignoreMissing = true）
            assertNull(user.getDepartmentName());
        }

        @Test
        @DisplayName("当ignoreMissing为false且结果不存在时，应该抛出ReferenceDataNotFoundException")
        void shouldThrowExceptionWhenIgnoreMissingFalseAndResultNotFound() {
            // Given
            TestUser_TableRef user = createTestUser(1L, 100L, 10L);

            // 不提供任何结果映射
            Map<String, TableResult> dbResultMap = new HashMap<>();

            // When & Then
            assertThrows(ReferenceDataNotFoundException.class,
                    () -> weaver.weave(user, dbResultMap));
        }

        @Test
        @DisplayName("当结果映射中存在空结果时，应该根据ignoreMissing设置处理")
        void shouldHandleNullResultsAccordingToIgnoreMissing() {
            // Given
            TestUser_TableRef user = createTestUser(1L, 100L, 10L);

            // 提供空的结果
            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = new TableQuery("sys_role", "id");
            TableResult roleResult = new TableResult(roleQuery, null); // 空结果

            Map<String, TableResult> dbResultMap = new HashMap<>();
            dbResultMap.put(roleKey, roleResult);

            // When & Then - 应该抛出异常，因为sys_role的ignoreMissing = false
            assertThrows(ReferenceDataNotFoundException.class,
                    () -> weaver.weave(user, dbResultMap));
        }

        @Test
        @DisplayName("当外键值为空时，应该跳过该规则的编织")
        void shouldSkipWeavingWhenForeignKeyIsNull() {
            // Given
            TestUser_TableRef user = createTestUser(1L, null, 10L); // roleId为null

            String deptKey = TableRefProcessor.buildMapKey("sys_department", "department_id");
            TableQuery deptQuery = new TableQuery("sys_department", "department_id");
            Map<String, Map<String, Object>> deptResults = new HashMap<>();

            Map<String, Object> deptRecord = new HashMap<>();
            deptRecord.put("name", "技术部");
            deptRecord.put("department_id", 10L);
            deptResults.put("10", deptRecord);

            TableResult deptResult = new TableResult(deptQuery, deptResults);

            Map<String, TableResult> dbResultMap = new HashMap<>();
            dbResultMap.put(deptKey, deptResult);

            // When
            weaver.weave(user, dbResultMap);

            // Then - 角色相关字段应该保持为null
            assertNull(user.getRoleName());
            assertNull(user.getRoleLevel());
            // 部门相关字段应该被正确编织
            assertEquals("技术部", user.getDepartmentName());
        }

        @Test
        @DisplayName("当键值在结果中不存在时，应该根据ignoreMissing设置处理")
        void shouldHandleMissingKeyInResults() {
            // Given
            TestUser_TableRef user = createTestUser(1L, 999L, 10L); // roleId 999不存在于结果中

            String roleKey = TableRefProcessor.buildMapKey("sys_role", "id");
            TableQuery roleQuery = new TableQuery("sys_role", "id");
            Map<String, Map<String, Object>> roleResults = new HashMap<>();

            Map<String, Object> roleRecord = new HashMap<>();
            roleRecord.put("name", "管理员");
            roleRecord.put("level", 1);
            roleRecord.put("id", 100L); // 只有100，没有999
            roleResults.put("100", roleRecord);

            TableResult roleResult = new TableResult(roleQuery, roleResults);

            Map<String, TableResult> dbResultMap = new HashMap<>();
            dbResultMap.put(roleKey, roleResult);

            // When & Then - 应该抛出异常，因为sys_role的ignoreMissing = false
            assertThrows(ReferenceDataNotFoundException.class,
                    () -> weaver.weave(user, dbResultMap));
        }
    }

    // 辅助方法：创建测试用户
    private TestUser_TableRef createTestUser(Long id, Long roleId, Long departmentId) {
        TestUser_TableRef user = new TestUser_TableRef();
        user.setRoleId(roleId);
        user.setDepartmentId(departmentId);
        return user;
    }
}