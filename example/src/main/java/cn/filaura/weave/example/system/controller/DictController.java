package cn.filaura.weave.example.system.controller;


import cn.filaura.weave.dict.DictHelper;
import cn.filaura.weave.example.system.entity.SysDict;
import cn.filaura.weave.example.system.service.DictService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("sys/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @Resource
    private DictHelper dictHelper;




    @GetMapping("list")
    public List<SysDict> selectDictList() {
        return dictService.selectDictList();
    }

    @GetMapping("getDict")
    public Map<Integer, String> getDict(@RequestParam(name = "code") String code) {
        return dictHelper.getDict(code, Integer.class);
    }

    @GetMapping("getDictBatch")
    public Map<String, Map<Integer, String>> getDictBatch(@RequestParam(name = "codes") String codes) {
        List<String> dictCodes = Arrays.stream(codes.split(",")).toList();
        return dictHelper.getDict(dictCodes, Integer.class);
    }

    @GetMapping("getReversedDict")
    public Map<String, Integer> getReversedDict(@RequestParam(name = "code") String code) {
        return dictHelper.getReversedDict(code, Integer.class);
    }

    @GetMapping("getReversedDictBatch")
    public Map<String, Map<String, Integer>> getReversedDictBatch(@RequestParam(name = "codes") String codes) {
        List<String> dictCodes = Arrays.stream(codes.split(",")).toList();
        return dictHelper.getReversedDict(dictCodes, Integer.class);
    }


}
