package cn.filaura.weave.example.system.service;


import cn.filaura.weave.example.system.entity.SysDict;

import java.util.List;

public interface DictService {

    List<SysDict> selectDictList();

    List<SysDict> listByIds(List<Long> ids);

}
