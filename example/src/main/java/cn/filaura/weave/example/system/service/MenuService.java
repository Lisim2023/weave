package cn.filaura.weave.example.system.service;

import cn.filaura.weave.example.ref.RefService;
import cn.filaura.weave.example.system.entity.Menu;

import java.util.List;

public interface MenuService extends RefService {

    List<Menu> selectMenuList();

    List<Menu> tree();

    List<Menu> listMenuByIes(List<Long> ids);

}
