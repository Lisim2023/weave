package cn.filaura.weave.example.system.service.impl;


import cn.filaura.weave.example.consts.TableNames;
import cn.filaura.weave.example.system.entity.User;
import cn.filaura.weave.example.system.mapper.UserMapper;
import cn.filaura.weave.example.system.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;



    @Override
    public List<User> selectUserList() {
        return userMapper.selectUserList();
    }

    @Override
    public String getSupportedTable() {
        return TableNames.USER;
    }

    @Override
    public Map<Object, Map<String, Object>> queryRefData(List<Long> ids) {
        return userMapper.queryRefData(ids);
    }

}
