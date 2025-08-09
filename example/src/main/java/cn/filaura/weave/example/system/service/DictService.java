package cn.filaura.weave.example.system.service;


import cn.filaura.weave.example.ref.RefService;
import cn.filaura.weave.example.system.entity.SysDict;

import java.util.List;

public interface DictService extends RefService {

    List<SysDict> selectDictList();

    List<SysDict> listDictByIds(List<Long> ids);

}
