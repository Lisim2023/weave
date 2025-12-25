package cn.filaura.weave.dict;


import java.util.HashMap;
import java.util.Map;

public class TestConfig {

    static Map<String, DictInfo> createTestDictInfoMap() {
        Map<String, DictInfo> dictInfoMap = new HashMap<>();

        // 性别字典
        DictInfo genderDict = new DictInfo("gender");
        Map<String, String> genderData = new HashMap<>();
        genderData.put("M", "男");
        genderData.put("F", "女");
        genderDict.setData(genderData);
        dictInfoMap.put("gender", genderDict);

        // 状态字典
        DictInfo statusDict = new DictInfo("status");
        Map<String, String> statusData = new HashMap<>();
        statusData.put("1", "激活");
        statusData.put("0", "禁用");
        statusData.put("2", "待审核");
        statusDict.setData(statusData);
        dictInfoMap.put("status", statusDict);

        // 角色字典
        DictInfo roleDict = new DictInfo("roles");
        Map<String, String> roleData = new HashMap<>();
        roleData.put("ADMIN", "管理员");
        roleData.put("USER", "普通用户");
        roleData.put("GUEST", "访客");
        roleDict.setData(roleData);
        dictInfoMap.put("roles", roleDict);

        // 部门字典
        DictInfo deptDict = new DictInfo( "departments");
        Map<String, String> deptData = new HashMap<>();
        deptData.put("D001", "技术部");
        deptData.put("D002", "市场部");
        deptData.put("D003", "财务部");
        deptDict.setData(deptData);
        dictInfoMap.put("departments", deptDict);

        return dictInfoMap;
    }
}
