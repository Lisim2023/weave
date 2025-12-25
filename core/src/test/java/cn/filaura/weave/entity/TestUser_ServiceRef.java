package cn.filaura.weave.entity;



import cn.filaura.weave.annotation.Mapping;
import cn.filaura.weave.annotation.ServiceRef;
import cn.filaura.weave.service.DepartmentService;
import cn.filaura.weave.service.RoleService;


@ServiceRef(
        service = RoleService.class,
        method = "listRolesByIds",
        mappings = {
                @Mapping(refField = "roleId", from = "name", to = "roleName"),
                @Mapping(refField = "roleId", from = "level", to = "roleLevel")
        }
)
@ServiceRef(
        service = DepartmentService.class,
        method = "listDepartmentByIds",
        ignoreMissing = true,
        mappings = @Mapping(refField = "departmentId", from = "name", to = "departmentName")
)
public class TestUser_ServiceRef {

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
