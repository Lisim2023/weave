package cn.filaura.weave.example.system.entity;


import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.example.consts.DictCodes;
import cn.filaura.weave.example.consts.TableNames;
import cn.filaura.weave.example.consts.UserColumns;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Role {

    private Long id;

    private String name;

    private String code;

    private String description;

    @Dict(code = DictCodes.YN)
    private Integer enabled;
    private String enabledText;

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
