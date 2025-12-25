package cn.filaura.weave.example.system.mapper;


import cn.filaura.weave.example.system.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<Menu> selectMenuList();


    List<Menu> listByIds(@Param("ids") List<Long> ids);
}
