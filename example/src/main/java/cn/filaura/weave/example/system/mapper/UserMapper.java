package cn.filaura.weave.example.system.mapper;

import cn.filaura.weave.example.system.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface UserMapper {

    List<User> selectUserList();

    List<User> listByIds(@Param("ids") List<Long> ids);

}
