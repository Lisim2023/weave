package cn.filaura.weave.example.system.service.impl;

import cn.filaura.weave.example.system.entity.DictItem;
import cn.filaura.weave.example.system.mapper.DictItemMapper;
import cn.filaura.weave.example.system.service.DictItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DictItemServiceImpl implements DictItemService {

    @Resource
    private DictItemMapper dictItemMapper;



    @Override
    public List<DictItem> selectDictItemList() {
        return dictItemMapper.selectDictItemList();
    }

}
