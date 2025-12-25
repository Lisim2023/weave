package cn.filaura.weave.example.system.entity;



import cn.filaura.weave.annotation.RecordEmbed;
import cn.filaura.weave.example.system.service.RoleService;
import cn.filaura.weave.example.system.service.UserService;
import lombok.Data;


@Data
public class RoleUser {

    private Long id;

    private Long roleId;

    @RecordEmbed(service = RoleService.class)
    private Role role;


    private Long userId;

    @RecordEmbed(service = UserService.class)
    private User user;

}
