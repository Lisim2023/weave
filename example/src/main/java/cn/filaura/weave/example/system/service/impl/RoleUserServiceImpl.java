package cn.filaura.weave.example.system.service.impl;

import cn.filaura.weave.example.system.entity.RoleUser;
import cn.filaura.weave.example.system.mapper.RoleUserMapper;
import cn.filaura.weave.example.system.service.RoleUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleUserServiceImpl implements RoleUserService {

    @Resource
    private RoleUserMapper roleUserMapper;



    @Override
    public List<RoleUser> selectRoleUserList() {
        return roleUserMapper.selectRoleUserList();
    }
}
