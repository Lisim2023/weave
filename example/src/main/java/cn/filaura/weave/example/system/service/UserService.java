package cn.filaura.weave.example.system.service;



import cn.filaura.weave.example.system.entity.User;

import java.util.List;


public interface UserService {

    List<User> selectUserList();

    List<User> listByIds(List<Long> ids);

    void saveUsers(List<User> users);
}
