package cn.filaura.weave.example.system.mapper;

import cn.filaura.weave.annotation.Weave;
import cn.filaura.weave.example.system.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface UserMapper {

    @Weave
    List<User> selectUserList();

    @Weave
    List<User> selectUsersWithRoleIds();

    List<User> listUserByIds(@Param("ids") List<Long> ids);

}
