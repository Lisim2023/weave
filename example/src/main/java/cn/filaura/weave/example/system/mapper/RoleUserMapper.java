package cn.filaura.weave.example.system.mapper;

import cn.filaura.weave.annotation.Weave;
import cn.filaura.weave.example.system.entity.RoleUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleUserMapper {

    @Weave
    List<RoleUser> selectRoleUserList();
}
