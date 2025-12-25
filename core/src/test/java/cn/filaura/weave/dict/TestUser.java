package cn.filaura.weave.dict;

import cn.filaura.weave.annotation.Cascade;
import cn.filaura.weave.annotation.Dict;

import java.util.List;

public class TestUser {

    @Dict(code = "gender")
    private String genderCode;
    private String genderCodeText;

    @Dict(code = "status", targetField = "statusDisplay")
    private Integer status;
    private String statusDisplay;

    @Dict(code = "roles")
    private List<String> roleCodes;
    private List<String> roleCodesText;

    @Dict(code = "departments", targetField = "deptTexts")
    private String[] deptCodes;
    private String[] deptTexts;

    @Dict(code = "special")
    private String code;
    private String codeText;

    @Cascade
    private TestUser manager;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public String getGenderCodeText() {
        return genderCodeText;
    }

    public void setGenderCodeText(String genderCodeText) {
        this.genderCodeText = genderCodeText;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public List<String> getRoleCodesText() {
        return roleCodesText;
    }

    public void setRoleCodesText(List<String> roleCodesText) {
        this.roleCodesText = roleCodesText;
    }

    public String[] getDeptCodes() {
        return deptCodes;
    }

    public void setDeptCodes(String[] deptCodes) {
        this.deptCodes = deptCodes;
    }

    public String[] getDeptTexts() {
        return deptTexts;
    }

    public void setDeptTexts(String[] deptTexts) {
        this.deptTexts = deptTexts;
    }

    public TestUser getManager() {
        return manager;
    }

    public void setManager(TestUser manager) {
        this.manager = manager;
    }

}
