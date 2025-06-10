package cn.filaura.weave.example.system.controller;


import cn.filaura.weave.example.system.entity.Menu;
import cn.filaura.weave.example.system.service.MenuService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("sys/menu")
public class MenuController {

    @Resource
    private MenuService menuService;



    @GetMapping("tree")
    public List<Menu> tree() {
        return menuService.tree();
    }

    @GetMapping("list")
    public List<Menu> list() {
        return menuService.selectMenuList();
    }

}
