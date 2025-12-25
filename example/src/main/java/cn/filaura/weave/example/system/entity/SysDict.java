package cn.filaura.weave.example.system.entity;



import cn.filaura.weave.annotation.Mapping;
import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.annotation.ServiceRef;
import cn.filaura.weave.example.consts.DictCodes;
import cn.filaura.weave.example.system.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ServiceRef(
        service = UserService.class,
        mappings = {
                @Mapping(refField = "createBy", from = "nickname", to = "createByUsername"),
                @Mapping(refField = "updateBy", from = "nickname", to = "updateByUsername")
        }
)
@Data
public class SysDict {

    private Long id;

    private String name;

    private String code;

    private String description;

    @Dict(code = DictCodes.YN)
    private Integer enabled;
    private String enabledText;

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
