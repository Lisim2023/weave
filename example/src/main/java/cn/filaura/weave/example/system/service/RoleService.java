package cn.filaura.weave.example.system.service;


import cn.filaura.weave.example.system.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> selectRoleList();

    List<Role> listByIds(List<Long> ids);
}
