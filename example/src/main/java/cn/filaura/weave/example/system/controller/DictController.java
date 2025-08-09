package cn.filaura.weave.example.system.controller;


import cn.filaura.weave.example.system.entity.SysDict;
import cn.filaura.weave.example.system.service.DictService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("sys/dict")
public class DictController {

    @Resource
    private DictService dictService;



    @GetMapping("list")
    public List<SysDict> selectDictList() {
        return dictService.selectDictList();
    }

}
