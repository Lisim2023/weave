package cn.filaura.weave.example.system.mapper;

import cn.filaura.weave.example.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface RoleMapper {

    List<Role> selectRoleList();


    List<Role> listByIds(@Param("ids") List<Long> ids);
}
