package cn.filaura.weave.example.system.mapper;


import cn.filaura.weave.annotation.Weave;
import cn.filaura.weave.example.system.entity.DictItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictItemMapper {

    @Weave
    List<DictItem> selectDictItemList();

}
