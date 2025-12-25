package cn.filaura.weave.entity;

import cn.filaura.weave.annotation.*;
import cn.filaura.weave.service.RoleService;

import java.util.List;



public class TestUser_RecordEmbed {

    private Long roleId;
    @RecordEmbed(service = RoleService.class, method = "listRolesByIds", refField = "roleId")
    private TestRole testRole;

    private List<Long> roleIdList;
    @RecordEmbed(service = RoleService.class, method = "listRolesByIds", refField = "roleIdList")
    private List<TestRole> testRoleList;

    private Long[] roleIdArray;
    @RecordEmbed(service = RoleService.class, method = "listRolesByIds", refField = "roleIdArray", ignoreMissing = true)
    private TestRole[] testRoleArray;

    // getters and setters
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public TestRole getTestRole() {
        return testRole;
    }

    public void setTestRole(TestRole testRole) {
        this.testRole = testRole;
    }

    public List<Long> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public List<TestRole> getTestRoleList() {
        return testRoleList;
    }

    public void setTestRoleList(List<TestRole> testRoleList) {
        this.testRoleList = testRoleList;
    }

    public Long[] getRoleIdArray() {
        return roleIdArray;
    }

    public void setRoleIdArray(Long[] roleIdArray) {
        this.roleIdArray = roleIdArray;
    }

    public TestRole[] getTestRoleArray() {
        return testRoleArray;
    }

    public void setTestRoleArray(TestRole[] testRoleArray) {
        this.testRoleArray = testRoleArray;
    }
}
