package cn.filaura.weave.example.system.service.impl;


import cn.filaura.weave.example.system.entity.Role;
import cn.filaura.weave.example.system.mapper.RoleMapper;
import cn.filaura.weave.example.system.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;



    @Override
    public List<Role> selectRoleList() {
        return roleMapper.selectRoleList();
    }

    @Override
    public List<Role> listByIds(List<Long> ids) {
        return roleMapper.listByIds(ids);
    }

}
