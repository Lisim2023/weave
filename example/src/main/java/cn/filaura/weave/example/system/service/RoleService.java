package cn.filaura.weave.example.system.service;

import cn.filaura.weave.example.ref.RefService;
import cn.filaura.weave.example.system.entity.Role;

import java.util.List;

public interface RoleService extends RefService {

    List<Role> selectRoleList();
}
