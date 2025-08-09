package cn.filaura.weave.example.system.mapper;


import cn.filaura.weave.annotation.Weave;
import cn.filaura.weave.example.system.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictMapper {

    @Weave
    List<SysDict> selectDictList();


    List<SysDict> listDictByIds(@Param("ids") List<Long> ids);

}
