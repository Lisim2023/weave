package cn.filaura.weave.entity;


import cn.filaura.weave.annotation.TableRef;
import cn.filaura.weave.annotation.Mapping;

@TableRef(
        table = "sys_role",
        keyColumn = "id",
        mappings = {
                @Mapping(refField = "roleId", from = "name", to = "roleName"),
                @Mapping(refField = "roleId", from = "level", to = "roleLevel")
        }
)
@TableRef(
        table = "sys_department",
        keyColumn = "department_id",
        mappings = @Mapping(refField = "departmentId", from = "name", to = "departmentName"),
        ignoreMissing = true
)
public class TestUser_TableRef {
    private Long roleId;
    private String roleName;
    private Integer roleLevel;

    private Long departmentId;
    private String departmentName;

    // getters and setters

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
