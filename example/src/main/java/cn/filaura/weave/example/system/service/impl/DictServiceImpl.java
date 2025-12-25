package cn.filaura.weave.example.system.service.impl;

import cn.filaura.weave.example.system.entity.SysDict;
import cn.filaura.weave.example.system.mapper.DictMapper;
import cn.filaura.weave.example.system.service.DictService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class DictServiceImpl implements DictService {

    @Resource
    private DictMapper dictMapper;



    @Override
    public List<SysDict> selectDictList() {
        return dictMapper.selectDictList();
    }

    @Override
    public List<SysDict> listByIds(List<Long> ids) {
        return dictMapper.listByIds(ids);
    }

}
