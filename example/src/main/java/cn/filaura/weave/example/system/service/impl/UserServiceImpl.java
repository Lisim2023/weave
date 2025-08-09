package cn.filaura.weave.example.system.service.impl;


import cn.filaura.weave.annotation.WeaveReverse;
import cn.filaura.weave.example.consts.TableNames;
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
    public List<User> selectUsersWithRoleIds() {
        return userMapper.selectUsersWithRoleIds();
    }

    @Override
    public List<User> listUserByIds(List<Long> ids) {
        return userMapper.listUserByIds(ids);
    }

    @Override
    public String getSupportedTable() {
        return TableNames.USER;
    }

    @Override
    public List<User> queryRefData(List<Long> ids) {
        return listUserByIds(ids);
    }

    @WeaveReverse
    @Override
    public void saveUsers(List<User> users) {
        //
    }
}
