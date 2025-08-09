package cn.filaura.weave.example.system.controller;


import cn.filaura.weave.example.system.entity.User;
import cn.filaura.weave.example.system.service.UserService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("sys/user")
public class UserController {

    @Resource
    private UserService userService;



    @GetMapping("list")
    public List<User> selectUserList() {
        return userService.selectUserList();
    }

    @GetMapping("collectionRefTest")
    public List<User> collectionRefTest() {
        return userService.selectUsersWithRoleIds();
    }

    @GetMapping("reverseDictTest")
    public List<User> reverseDictTest() {
        List<User> users = new ArrayList<>();
        users.add(new User("男", "影视,7"));
        users.add(new User("女", "跑步,游戏,阅读"));
        users.add(new User("男", "桌球,跳绳"));
        users.add(new User("女", "16,音乐"));
        users.add(new User("男", "篮球,编程"));
        users.add(new User("女", "排球,5"));
        users.add(new User(null,null));
        userService.saveUsers(users);
        return users;
    }

}
