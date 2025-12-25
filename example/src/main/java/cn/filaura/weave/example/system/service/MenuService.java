package cn.filaura.weave.example.system.service;

import cn.filaura.weave.example.system.entity.Menu;

import java.util.List;

public interface MenuService {

    List<Menu> selectMenuList();

    List<Menu> tree();

    List<Menu> listByIds(List<Long> ids);

}
