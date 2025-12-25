package cn.filaura.weave.example.system.service.impl;


import cn.filaura.weave.example.system.entity.Menu;
import cn.filaura.weave.example.system.mapper.MenuMapper;
import cn.filaura.weave.example.system.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;



    @Override
    public List<Menu> selectMenuList() {
        return menuMapper.selectMenuList();
    }

    @Override
    public List<Menu> tree() {
        List<Menu> menus = menuMapper.selectMenuList();
        menus.forEach(menu -> {
            List<Menu> children = menus.stream()
                    .filter(item -> item.getParentId() != null && item.getParentId().equals(menu.getId()))
                    .toList();
            if (!children.isEmpty()) {
                menu.setChildren(children);
            }
        });

        return menus.stream()
                .filter(item -> (item.getParentId() == null))
                .toList();
    }

    @Override
    public List<Menu> listByIds(List<Long> ids) {
        return menuMapper.listByIds(ids);
    }

}
