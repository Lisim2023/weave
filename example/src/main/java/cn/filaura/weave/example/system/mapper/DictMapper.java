package cn.filaura.weave.example.system.mapper;


import cn.filaura.weave.example.system.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictMapper {

    List<SysDict> selectDictList();

    List<SysDict> listByIds(@Param("ids") List<Long> ids);

}
