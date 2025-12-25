package cn.filaura.weave.dict;



import cn.filaura.weave.exception.DictDataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DictValueWeaverTest {

    private DictValueWeaver dictValueWeaver;
    private Map<String, DictInfo> testDictInfoMap;

    @BeforeEach
    void setUp() {
        dictValueWeaver = new DictValueWeaver();
        testDictInfoMap = TestConfig.createTestDictInfoMap();
    }

    @Test
    @DisplayName("测试单个字段的字典值解析")
    void testResolveDictValuesForSingleField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setGenderCodeText("女");
        user.setStatusDisplay("禁用");

        // 执行测试
        dictValueWeaver.weaveDictValue(user, testDictInfoMap);

        // 验证结果
        assertEquals("F", user.getGenderCode());
        assertEquals(Integer.valueOf(0), user.getStatus());
    }

    @Test
    @DisplayName("测试集合字段的字典值解析")
    void testResolveDictValuesForCollectionField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setRoleCodesText(Arrays.asList("管理员", "访客"));

        // 执行测试
        dictValueWeaver.weaveDictValue(user, testDictInfoMap);

        // 验证结果
        assertNotNull(user.getRoleCodes());
        assertEquals(2, user.getRoleCodes().size());
        assertEquals("ADMIN", user.getRoleCodes().get(0));
        assertEquals("GUEST", user.getRoleCodes().get(1));
    }

    @Test
    @DisplayName("测试数组字段的字典值解析")
    void testResolveDictValuesForArrayField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setDeptTexts(new String[]{"市场部", "技术部"});

        // 执行测试
        dictValueWeaver.weaveDictValue(user, testDictInfoMap);

        // 验证结果
        assertNotNull(user.getDeptCodes());
        assertEquals(2, user.getDeptCodes().length);
        assertEquals("D002", user.getDeptCodes()[0]);
        assertEquals("D001", user.getDeptCodes()[1]);
    }

    @Test
    @DisplayName("测试嵌套对象的字典值解析")
    void testResolveDictValuesForNestedObject() {
        // 准备测试数据
        TestUser manager = new TestUser();
        manager.setGenderCodeText("女");

        TestUser user = new TestUser();
        user.setGenderCodeText("男");
        user.setManager(manager);

        // 执行测试
        dictValueWeaver.weaveDictValue(user, testDictInfoMap);

        // 验证结果
        assertEquals("M", user.getGenderCode());
        assertEquals("F", manager.getGenderCode());
    }

    @Test
    @DisplayName("测试空值字段的字典值解析")
    void testResolveDictValuesForNullField() {
        // 准备测试数据
        TestUser user = new TestUser();
        user.setGenderCodeText(null);
        user.setStatusDisplay("激活");

        // 执行测试
        dictValueWeaver.weaveDictValue(user, testDictInfoMap);

        // 验证结果 - 空值字段不应处理
        assertNull(user.getGenderCode());
        assertEquals(Integer.valueOf(1), user.getStatus());
    }

    @Test
    @DisplayName("测试字典文本不存在时抛出异常")
    void testResolveDictValuesWhenDictTextNotFound() {
        // 准备测试数据 - 使用不存在的字典文本
        TestUser user = new TestUser();
        user.setGenderCodeText("未知性别");

        // 执行测试并验证异常
        DictDataNotFoundException exception = assertThrows(
                DictDataNotFoundException.class,
                () -> dictValueWeaver.weaveDictValue(user, testDictInfoMap)
        );

        assertTrue(exception.getMessage().contains("Dictionary text '未知性别' not found"));
    }

    @Test
    @DisplayName("测试对字典文本字段输入字典值时的处理")
    void testResolveDictValuesWhenValueEqualsText() {
        TestUser user = new TestUser();
        // 将字典值输入到字典文本字段
        user.setGenderCodeText("M");
        user.setStatusDisplay("2");
        user.setRoleCodesText(Arrays.asList("USER", "GUEST"));
        user.setDeptTexts(new String[]{"D001", "D003"});

        // 执行测试
        dictValueWeaver.weaveDictValue(user, testDictInfoMap);

        // 验证结果 - 将字典值输入到字典文本字段时，能正确设置到字典值字段
        assertEquals("M", user.getGenderCode());
        assertEquals(2, user.getStatus());
        assertEquals("USER", user.getRoleCodes().get(0));
        assertEquals("GUEST", user.getRoleCodes().get(1));
        assertEquals("D001", user.getDeptCodes()[0]);
        assertEquals("D003", user.getDeptCodes()[1]);
    }

    @Test
    @DisplayName("测试批量对象的字典值解析")
    void testResolveDictValuesForBatchObjects() {
        // 准备测试数据
        List<TestUser> users = new ArrayList<>();

        TestUser user1 = new TestUser();
        user1.setGenderCodeText("男");
        user1.setStatusDisplay("激活");

        TestUser user2 = new TestUser();
        user2.setGenderCodeText("女");
        user2.setStatusDisplay("待审核");

        users.add(user1);
        users.add(user2);

        // 执行测试
        dictValueWeaver.weaveDictValue(users, testDictInfoMap);

        // 验证结果
        assertEquals("M", user1.getGenderCode());
        assertEquals(Integer.valueOf(1), user1.getStatus());
        assertEquals("F", user2.getGenderCode());
        assertEquals(Integer.valueOf(2), user2.getStatus());
    }
}
