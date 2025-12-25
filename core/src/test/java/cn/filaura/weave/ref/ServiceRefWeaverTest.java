package cn.filaura.weave.ref;

import cn.filaura.weave.entity.TestDepartment;
import cn.filaura.weave.entity.TestRole;
import cn.filaura.weave.entity.TestUser_ServiceRef;
import cn.filaura.weave.exception.ReferenceDataNotFoundException;
import cn.filaura.weave.service.DepartmentService;
import cn.filaura.weave.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("RecordFieldMappingWeaver 单元测试")
public class ServiceRefWeaverTest {

    private ServiceRefWeaver weaver;
    private Map<String, ServiceQuery> serviceQueryMap;
    private Map<String, ServiceResult> serviceResultMap;

    @BeforeEach
    void setUp() {
        weaver = new ServiceRefWeaver();
        serviceQueryMap = new HashMap<>();
        serviceResultMap = new HashMap<>();
    }

    @Test
    @DisplayName("收集字段值 - 当POJO对象有注解时 - 应该正确收集外键值到查询映射")
    void collectFieldValues_WhenPojosHaveAnnotations_ShouldCollectForeignKeyValues() {
        // 准备测试数据
        TestUser_ServiceRef user1 = new TestUser_ServiceRef();
        user1.setRoleId(1001L);
        user1.setDepartmentId(2001L);

        TestUser_ServiceRef user2 = new TestUser_ServiceRef();
        user2.setRoleId(1002L);
        user2.setDepartmentId(2002L);

        List<TestUser_ServiceRef> users = Arrays.asList(user1, user2);

        // 执行方法
        weaver.collectForeignKeyValues(users, serviceQueryMap);

        // 验证结果
        assertEquals(2, serviceQueryMap.size());

        String roleKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        String deptKey = ServiceRefProcessor.buildMapKey(DepartmentService.class, "listDepartmentByIds");

        ServiceQuery roleQuery = serviceQueryMap.get(roleKey);
        ServiceQuery deptQuery = serviceQueryMap.get(deptKey);

        assertNotNull(roleQuery);
        assertNotNull(deptQuery);

        assertEquals(RoleService.class, roleQuery.getService());
        assertEquals("listRolesByIds", roleQuery.getMethod());
        assertEquals(2, roleQuery.getIds().size());
        assertTrue(roleQuery.getIds().contains(1001L));
        assertTrue(roleQuery.getIds().contains(1002L));

        assertEquals(DepartmentService.class, deptQuery.getService());
        assertEquals("listDepartmentByIds", deptQuery.getMethod());
        assertEquals(2, deptQuery.getIds().size());
        assertTrue(deptQuery.getIds().contains(2001L));
        assertTrue(deptQuery.getIds().contains(2002L));
    }

    @Test
    @DisplayName("收集字段值 - 当POJO对象没有注解时 - 不应该收集任何值")
    void collectFieldValues_WhenPojosNoAnnotations_ShouldNotCollectAnyValues() {
        // 准备测试数据 - 使用没有注解的类
        TestUser_NoAnnotation user = new TestUser_NoAnnotation();
        user.setSomeField("test");

        // 执行方法
        weaver.collectForeignKeyValues(user, serviceQueryMap);

        // 验证结果
        assertTrue(serviceQueryMap.isEmpty());
    }

    @Test
    @DisplayName("收集字段值 - 当外键值为null时 - 不应该收集null值")
    void collectFieldValues_WhenForeignKeyIsNull_ShouldNotCollectNullValue() {
        // 准备测试数据
        TestUser_ServiceRef user = new TestUser_ServiceRef();
        user.setRoleId(null); // 外键为null
        user.setDepartmentId(2001L);

        // 执行方法
        weaver.collectForeignKeyValues(user, serviceQueryMap);

        // 验证结果
        String roleKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        String deptKey = ServiceRefProcessor.buildMapKey(DepartmentService.class, "listDepartmentByIds");

        ServiceQuery roleQuery = serviceQueryMap.get(roleKey);
        ServiceQuery deptQuery = serviceQueryMap.get(deptKey);

        // 角色查询应该存在但ids为空
        assertNotNull(roleQuery);
        assertTrue(roleQuery.getIds().isEmpty());

        // 部门查询应该正常收集
        assertNotNull(deptQuery);
        assertEquals(1, deptQuery.getIds().size());
        assertTrue(deptQuery.getIds().contains(2001L));
    }

    @Test
    @DisplayName("编织数据 - 当查询结果存在时 - 应该正确映射字段值")
    void weave_WhenServiceResultsExist_ShouldMapFieldsCorrectly() {
        // 准备测试数据
        TestUser_ServiceRef user = new TestUser_ServiceRef();
        user.setRoleId(1001L);
        user.setDepartmentId(2001L);

        // 准备查询结果
        Map<String, Object> roleResults = new HashMap<>();
        roleResults.put("1001", new TestRole(1001L, "管理员", 1));

        Map<String, Object> deptResults = new HashMap<>();
        deptResults.put("2001", new TestDepartment(2001L, "技术部"));

        String roleKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        String deptKey = ServiceRefProcessor.buildMapKey(DepartmentService.class, "listDepartmentByIds");

        ServiceQuery roleQuery = new ServiceQuery(RoleService.class, "listRolesByIds");
        ServiceQuery deptQuery = new ServiceQuery(DepartmentService.class, "listDepartmentByIds");

        serviceResultMap.put(roleKey, new ServiceResult(roleQuery, roleResults));
        serviceResultMap.put(deptKey, new ServiceResult(deptQuery, deptResults));

        // 执行方法
        weaver.weave(user, serviceResultMap);

        // 验证结果
        assertEquals("管理员", user.getRoleName());
        assertEquals(Integer.valueOf(1), user.getRoleLevel());
        assertEquals("技术部", user.getDepartmentName());
    }

    @Test
    @DisplayName("编织数据 - 当查询结果为空时 - 应该抛出ReferenceDataNotFoundException异常")
    void weave_WhenServiceResultsAreNull_ShouldThrowException() {
        // 准备测试数据
        TestUser_ServiceRef user = new TestUser_ServiceRef();
        user.setRoleId(1001L);

        // serviceResultMap为空或包含null结果

        // 执行并验证异常
        ReferenceDataNotFoundException exception = assertThrows(
                ReferenceDataNotFoundException.class,
                () -> weaver.weave(user, serviceResultMap)
        );

        assertTrue(exception.getMessage().contains("Result is null for"));
    }

    @Test
    @DisplayName("编织数据 - 当外键在结果中不存在且ignoreMissing为true时 - 应该跳过该映射")
    void weave_WhenForeignKeyNotFoundAndIgnoreMissingTrue_ShouldSkipMapping() {
        // 准备测试数据
        TestUser_ServiceRef user = new TestUser_ServiceRef();
        user.setDepartmentId(9999L); // 不存在的部门ID

        // 准备空的部门查询结果
        Map<String, Object> deptResults = new HashMap<>();
        String deptKey = ServiceRefProcessor.buildMapKey(DepartmentService.class, "listDepartmentByIds");
        ServiceQuery deptQuery = new ServiceQuery(DepartmentService.class, "listDepartmentByIds");
        serviceResultMap.put(deptKey, new ServiceResult(deptQuery, deptResults));

        // 执行方法 - 不应该抛出异常，因为ignoreMissing = true
        assertDoesNotThrow(() -> weaver.weave(user, serviceResultMap));

        // 验证字段没有被设置
        assertNull(user.getDepartmentName());
    }

    @Test
    @DisplayName("编织数据 - 当外键在结果中不存在且ignoreMissing为false时 - 应该抛出ReferenceDataNotFoundException异常")
    void weave_WhenForeignKeyNotFoundAndIgnoreMissingFalse_ShouldThrowException() {
        // 准备测试数据 - 角色映射的ignoreMissing默认为false
        TestUser_ServiceRef user = new TestUser_ServiceRef();
        user.setRoleId(9999L); // 不存在的角色ID

        // 准备空的角色查询结果
        Map<String, Object> roleResults = new HashMap<>();
        String roleKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery roleQuery = new ServiceQuery(RoleService.class, "listRolesByIds");
        serviceResultMap.put(roleKey, new ServiceResult(roleQuery, roleResults));

        // 执行并验证异常
        ReferenceDataNotFoundException exception = assertThrows(
                ReferenceDataNotFoundException.class,
                () -> weaver.weave(user, serviceResultMap)
        );

        assertTrue(exception.getMessage().contains("Key 9999 not found in"));
    }

    @Test
    @DisplayName("编织数据 - 当映射的源字段值为null时 - 不应该设置目标字段")
    void weave_WhenSourceFieldValueIsNull_ShouldNotSetTargetField() {
        // 准备测试数据 - 角色名称字段为null
        TestUser_ServiceRef user = new TestUser_ServiceRef();
        user.setRoleId(1001L);

        TestRole roleWithNullName = new TestRole(1001L, null, 1); // name字段为null

        Map<String, Object> roleResults = new HashMap<>();
        roleResults.put("1001", roleWithNullName);

        String roleKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery roleQuery = new ServiceQuery(RoleService.class, "listRolesByIds");
        serviceResultMap.put(roleKey, new ServiceResult(roleQuery, roleResults));

        // 执行方法
        weaver.weave(user, serviceResultMap);

        // 验证结果 - roleName应该为null，但roleLevel应该被设置
        assertNull(user.getRoleName());
        assertEquals(Integer.valueOf(1), user.getRoleLevel());
    }

    @Test
    @DisplayName("编织数据 - 当处理对象集合时 - 应该为所有对象正确映射字段")
    void weave_WhenProcessingCollection_ShouldMapFieldsForAllObjects() {
        // 准备测试数据
        TestUser_ServiceRef user1 = new TestUser_ServiceRef();
        user1.setRoleId(1001L);

        TestUser_ServiceRef user2 = new TestUser_ServiceRef();
        user2.setRoleId(1002L);

        List<TestUser_ServiceRef> users = Arrays.asList(user1, user2);

        // 准备查询结果
        Map<String, Object> roleResults = new HashMap<>();
        roleResults.put("1001", new TestRole(1001L, "管理员", 1));
        roleResults.put("1002", new TestRole(1002L, "用户", 2));

        String roleKey = ServiceRefProcessor.buildMapKey(RoleService.class, "listRolesByIds");
        ServiceQuery roleQuery = new ServiceQuery(RoleService.class, "listRolesByIds");
        serviceResultMap.put(roleKey, new ServiceResult(roleQuery, roleResults));

        // 执行方法
        weaver.weave(users, serviceResultMap);

        // 验证结果
        assertEquals("管理员", user1.getRoleName());
        assertEquals(Integer.valueOf(1), user1.getRoleLevel());

        assertEquals("用户", user2.getRoleName());
        assertEquals(Integer.valueOf(2), user2.getRoleLevel());
    }

    // 用于测试的没有注解的类
    static class TestUser_NoAnnotation {
        private String someField;

        public String getSomeField() { return someField; }
        public void setSomeField(String someField) { this.someField = someField; }
    }
}
