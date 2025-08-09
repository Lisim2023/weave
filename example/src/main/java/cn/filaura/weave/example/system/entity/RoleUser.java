package cn.filaura.weave.example.system.entity;


import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.example.consts.TableNames;
import lombok.Data;

@Data
public class RoleUser {

    private Long id;

    @Ref(table = TableNames.ROLE, mapTo = "role")
    private Long roleId;
    private Role role;

    @Ref(table = TableNames.USER, mapTo = "user")
    private Long userId;
    private User user;

}
