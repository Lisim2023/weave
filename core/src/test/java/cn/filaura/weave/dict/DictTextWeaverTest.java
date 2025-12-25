package cn.filaura.weave.dict;


import cn.filaura.weave.exception.DictDataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DictTextWeaverTest {

    private DictTextWeaver dictTextWeaver;
    private Map<String, DictInfo> testDictInfoMap;

    @BeforeEach
    void setUp() {
        dictTextWeaver = new DictTextWeaver();
        testDictInfoMap = TestConfig.createTestDictInfoMap();
    }

    @Test
    @DisplayName("测试单个字段的字典文本注入")
    void testInjectDictTextForSingleField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setGenderCode("M");
        user.setStatus(1);

        // 执行测试
        dictTextWeaver.weaveDictText(user, testDictInfoMap);

        // 验证结果
        assertEquals("男", user.getGenderCodeText());
        assertEquals("激活", user.getStatusDisplay());
    }

    @Test
    @DisplayName("测试集合字段的字典文本注入")
    void testInjectDictTextForCollectionField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setRoleCodes(Arrays.asList("ADMIN", "USER"));

        // 执行测试
        dictTextWeaver.weaveDictText(user, testDictInfoMap);

        // 验证结果
        assertNotNull(user.getRoleCodesText());
        assertEquals(2, user.getRoleCodesText().size());
        assertEquals("管理员", user.getRoleCodesText().get(0));
        assertEquals("普通用户", user.getRoleCodesText().get(1));
    }

    @Test
    @DisplayName("测试数组字段的字典文本注入")
    void testInjectDictTextForArrayField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setDeptCodes(new String[]{"D001", "D003"});

        // 执行测试
        dictTextWeaver.weaveDictText(user, testDictInfoMap);

        // 验证结果
        assertNotNull(user.getDeptTexts());
        assertEquals(2, user.getDeptTexts().length);
        assertEquals("技术部", user.getDeptTexts()[0]);
        assertEquals("财务部", user.getDeptTexts()[1]);
    }

    @Test
    @DisplayName("测试嵌套对象的字典文本注入")
    void testInjectDictTextForNestedObject() {
        // 准备测试数据
        TestUser manager = new TestUser();
        manager.setGenderCode("F");

        TestUser user = new TestUser();
        user.setGenderCode("M");
        user.setManager(manager);

        // 执行测试
        dictTextWeaver.weaveDictText(user, testDictInfoMap);

        // 验证结果
        assertEquals("男", user.getGenderCodeText());
        assertEquals("女", manager.getGenderCodeText());
    }

    @Test
    @DisplayName("测试空值字段的字典文本注入")
    void testInjectDictTextForNullField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setGenderCode(null);
        user.setStatus(1);

        // 执行测试
        dictTextWeaver.weaveDictText(user, testDictInfoMap);

        // 验证结果 - 空值字段不应处理
        assertNull(user.getGenderCodeText());
        assertEquals("激活", user.getStatusDisplay());
    }

    @Test
    @DisplayName("测试字典数据不存在时抛出异常")
    void testInjectDictTextWhenDictDataNotFound() {
        // 准备测试数据 - 使用不存在的字典代码
        TestUser user = new TestUser();
        user.setGenderCode("X"); // 不存在的字典值

        // 执行测试并验证异常
        assertThrows(
                DictDataNotFoundException.class,
                () -> dictTextWeaver.weaveDictText(user, testDictInfoMap)
        );

    }

    @Test
    @DisplayName("测试字典信息不存在时抛出异常")
    void testInjectDictTextWhenDictInfoNotFound() {
        // 准备测试数据 - 创建不包含所需字典信息的映射
        Map<String, DictInfo> emptyDictInfoMap = new HashMap<>();

        TestUser user = new TestUser();
        user.setGenderCode("M");

        // 执行测试并验证异常
        assertThrows(
                DictDataNotFoundException.class,
                () -> dictTextWeaver.weaveDictText(user, emptyDictInfoMap)
        );
    }

    @Test
    @DisplayName("测试批量对象的字典文本注入")
    void testInjectDictTextForBatchObjects() {
        // 准备测试数据
        List<TestUser> users = new ArrayList<>();

        TestUser user1 = new TestUser();
        user1.setGenderCode("M");
        user1.setStatus(0);

        TestUser user2 = new TestUser();
        user2.setGenderCode("F");
        user2.setStatus(2);

        users.add(user1);
        users.add(user2);

        // 执行测试
        dictTextWeaver.weaveDictText(users, testDictInfoMap);

        // 验证结果
        assertEquals("男", user1.getGenderCodeText());
        assertEquals("禁用", user1.getStatusDisplay());
        assertEquals("女", user2.getGenderCodeText());
        assertEquals("待审核", user2.getStatusDisplay());
    }
}
