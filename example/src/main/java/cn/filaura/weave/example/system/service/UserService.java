package cn.filaura.weave.example.system.service;



import cn.filaura.weave.example.ref.RefService;
import cn.filaura.weave.example.system.entity.User;

import java.util.List;


public interface UserService extends RefService {

    List<User> selectUserList();

    List<User> selectUsersWithRoleIds();

    List<User> listUserByIds(List<Long> ids);

    void saveUsers(List<User> users);
}
