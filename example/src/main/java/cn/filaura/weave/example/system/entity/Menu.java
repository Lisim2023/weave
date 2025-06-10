package cn.filaura.weave.example.system.entity;


import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.annotation.Cascade;
import cn.filaura.weave.example.consts.DictCodes;
import cn.filaura.weave.example.consts.MenuColumns;
import cn.filaura.weave.example.consts.TableNames;
import cn.filaura.weave.example.consts.UserColumns;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class Menu {

    private Long id;

    private String title;

    @Ref(table = TableNames.MENU, columns = MenuColumns.TITLE)
    private Long parentId;
    private String parentIdRefTitle;

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

    @Ref(table = TableNames.USER, columns = UserColumns.NICKNAME)
    private String createBy;
    private String createByRefNickname;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Ref(table = TableNames.USER, columns = UserColumns.NICKNAME)
    private String updateBy;
    private String updateByRefNickname;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer delFlag;


}
