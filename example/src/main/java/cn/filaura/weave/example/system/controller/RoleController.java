package cn.filaura.weave.example.system.controller;

import cn.filaura.weave.example.system.entity.Role;
import cn.filaura.weave.example.system.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sys/role")
public class RoleController {

    @Resource
    private RoleService roleService;



    @GetMapping("list")
    public List<Role> list() {
        return roleService.selectRoleList();
    }
}
