package cn.filaura.weave.example.system.entity;



import cn.filaura.weave.annotation.*;
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
public class User {

    public User() {
    }

    public User(String genderText, String hobbiesText) {
        this.genderText = genderText;
        this.hobbiesText = hobbiesText;
    }



    private Long id;

    private String username;

    private String nickname;

    private String password;

    @Dict(code = DictCodes.GENDER)
    private Integer gender;
    private String genderText;

    @Dict(code = DictCodes.HOBBIES)
    private String hobbies;
    private String hobbiesText;

    private String avatar;

    private String email;

    private String address;

    private Integer enabled;

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
