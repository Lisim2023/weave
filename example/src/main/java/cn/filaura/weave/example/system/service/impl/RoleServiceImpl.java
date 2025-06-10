package cn.filaura.weave.example.system.service.impl;


import cn.filaura.weave.example.consts.TableNames;
import cn.filaura.weave.example.system.entity.Role;
import cn.filaura.weave.example.system.mapper.RoleMapper;
import cn.filaura.weave.example.system.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;



    @Override
    public List<Role> selectRoleList() {
        return roleMapper.selectRoleList();
    }

    @Override
    public String getSupportedTable() {
        return TableNames.ROLE;
    }

    @Override
    public Map<Object, Map<String, Object>> queryRefData(List<Long> ids) {
        return roleMapper.queryRefData(ids);
    }
}
