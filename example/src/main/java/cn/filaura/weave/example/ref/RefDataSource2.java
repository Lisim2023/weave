package cn.filaura.weave.example.ref;


import cn.filaura.weave.ref.RefDataSource;
import cn.filaura.weave.ref.RefInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 引用数据源接口实现2
 *
 * <p>通过为不同表注册专属查询服务{@link RefService}来完成查询
 *
 * @see RefService
 */
@Component
public class RefDataSource2 implements RefDataSource {

    private static final Logger logger = LoggerFactory.getLogger(RefDataSource2.class);

    private final Map<String, RefService> refServiceMap = new HashMap<>();



    @Autowired
    public RefDataSource2(List<RefService> refServices) {
        // 注册RefService
        for (RefService service : refServices) {
            String tableName = service.getSupportedTable();
            refServiceMap.put(tableName, service);
            logger.info("注册RefService: 表名={}, 服务类={}", tableName, service.getClass().getSimpleName());
        }
    }



    @Override
    public RefInfo queryRefData(String table, Collection<String> columns, String key, Collection<String> values) {
        // 通过表名得到对应的RefService实例
        RefService refService = refServiceMap.get(table);
        if (refService == null) {
            throw new RuntimeException("没有为表 '" + table + "' 配置refService");
        }
        // 将主键值转换为Long型并排序
        List<Long> ids = values.stream().map(Long::valueOf).sorted().toList();
        // 查询数据
        Map<Object, Map<String, Object>> data = refService.queryRefData(ids);
        // 封装数据并返回
        return new RefInfo(table, key, RefInfo.mapKeyToString(data));
    }
}
