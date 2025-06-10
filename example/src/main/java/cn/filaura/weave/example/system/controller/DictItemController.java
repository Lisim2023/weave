package cn.filaura.weave.example.system.controller;

import cn.filaura.weave.example.system.entity.DictItem;
import cn.filaura.weave.example.system.service.DictItemService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sys/dictItem")
public class DictItemController {

    @Resource
    private DictItemService dictItemService;



    @GetMapping("list")
    public List<DictItem> selectDictItemList() {
        return dictItemService.selectDictItemList();
    }
}
