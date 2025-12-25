package cn.filaura.weave.example.system.service.impl;


import cn.filaura.weave.annotation.WeaveReverse;
import cn.filaura.weave.example.system.entity.User;
import cn.filaura.weave.example.system.mapper.UserMapper;
import cn.filaura.weave.example.system.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;



    @Override
    public List<User> selectUserList() {
        return userMapper.selectUserList();
    }

    @Override
    public List<User> listByIds(List<Long> ids) {
        return userMapper.listByIds(ids);
    }

    @WeaveReverse
    @Override
    public void saveUsers(List<User> users) {
        //
    }
}
