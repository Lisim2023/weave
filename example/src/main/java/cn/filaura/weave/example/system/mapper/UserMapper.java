package cn.filaura.weave.example.system.mapper;

import cn.filaura.weave.annotation.Weave;
import cn.filaura.weave.example.consts.UserColumns;
import cn.filaura.weave.example.system.entity.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Weave
    List<User> selectUserList();

    @MapKey(UserColumns.ID)
    Map<Object, Map<String, Object>> queryRefData(@Param("ids") List<Long> ids);
}
