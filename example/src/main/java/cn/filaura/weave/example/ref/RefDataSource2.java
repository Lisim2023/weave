package cn.filaura.weave.example.ref;


import cn.filaura.weave.ref.RefDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 引用数据源接口实现2
 * <p>基于 {@link RefService} 路由机制的引用数据源实现
 *
 * <p>自动收集所有RefService实现形成表名→服务的映射关系，
 * 通过表名路由到对应的RefService实现，提供统一的引用数据访问入口。
 *
 * @see RefService
 */
//@Component
public class RefDataSource2 implements RefDataSource {

    /**
     * 表名到RefService的路由映射表
     * Key: 表名（如 "sys_user"）
     * Value: 对应的数据查询服务
     */
    private final Map<String, RefService> refServiceMap = new HashMap<>();



    @Autowired
    public RefDataSource2(List<RefService> refServices) {
        // 遍历所有RefService实现并注册到路由映射表
        for (RefService service : refServices) {
            String tableName = service.getSupportedTable();
            refServiceMap.put(tableName, service);
        }
    }



    @Override
    public List<?> queryRefData(String table, Collection<String> columns, String key, Collection<String> values) {
        // 通过表名得到对应的引用服务实例
        RefService refService = refServiceMap.get(table);
        if (refService == null) {
            throw new RuntimeException("没有为表 '" + table + "' 配置引用服务");
        }

        // 将主键值转换为Long型并排序
        List<Long> ids = values.stream().map(Long::valueOf).sorted().toList();

        // 委托具体服务执行查询
        return refService.queryRefData(ids);
    }
}
