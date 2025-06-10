package cn.filaura.weave.example.system.mapper;

import cn.filaura.weave.annotation.Weave;
import cn.filaura.weave.example.consts.RoleColumns;
import cn.filaura.weave.example.system.entity.Role;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper {

    @Weave
    List<Role> selectRoleList();

    @MapKey(RoleColumns.ID)
    Map<Object, Map<String, Object>> queryRefData(@Param("ids") List<Long> ids);
}
