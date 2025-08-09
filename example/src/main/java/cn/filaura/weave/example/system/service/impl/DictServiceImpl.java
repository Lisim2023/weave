package cn.filaura.weave.example.system.service.impl;

import cn.filaura.weave.example.consts.TableNames;
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
    public List<SysDict> listDictByIds(List<Long> ids) {
        return dictMapper.listDictByIds(ids);
    }

    @Override
    public String getSupportedTable() {
        return TableNames.DICT;
    }

    @Override
    public List<SysDict> queryRefData(List<Long> ids) {
        return listDictByIds(ids);
    }

}
