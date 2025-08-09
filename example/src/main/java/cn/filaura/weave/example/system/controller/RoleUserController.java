package cn.filaura.weave.example.system.controller;


import cn.filaura.weave.example.system.entity.RoleUser;
import cn.filaura.weave.example.system.service.RoleUserService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sys/roleUser")
public class RoleUserController {

    @Resource
    private RoleUserService roleUserService;



    @GetMapping("list")
    public List<RoleUser> selectRoleUserList() {
        return roleUserService.selectRoleUserList();
    }

}
