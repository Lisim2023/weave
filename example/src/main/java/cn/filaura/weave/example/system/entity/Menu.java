package cn.filaura.weave.example.system.entity;


import cn.filaura.weave.annotation.*;
import cn.filaura.weave.example.consts.DictCodes;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@TableRef(
        table = "sys_menu",
        mappings = @Mapping(refField = "parentId", from = "title", to = "parentTitle")
)
@TableRef(
        table = "sys_user",
        mappings = {
                @Mapping(refField = "createBy", from = "nickname", to = "createByUsername"),
                @Mapping(refField = "updateBy", from = "nickname", to = "updateByUsername"),
        }
)
@Data
public class Menu {

    private Long id;

    private String title;

    private Long parentId;
    private String parentTitle;

    private Double orderNum;

    private String path;

    private String component;

    @Dict(code = DictCodes.MENU_TYPE)
    private Integer type;
    private String typeText;

    private String perms;

    private String icon;

    @Dict(code = DictCodes.YN)
    private Integer enabled;
    private String enabledText;

    @Cascade
    private List<Menu> children;

    private String createBy;
    private String createByUsername;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String updateBy;
    private String updateByUsername;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer delFlag;

}
