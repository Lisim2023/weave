package cn.filaura.weave.example.system.entity;


import cn.filaura.weave.annotation.Cascade;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.example.consts.TableNames;
import lombok.Data;

@Data
public class RoleUser {

    private Long id;

    @Ref(table = TableNames.ROLE, targetBean = "role")
    private Long roleId;

    private Role role;

    @Ref(table = TableNames.USER, targetBean = "user")
    private Long userId;

    @Cascade
    private User user;

}
