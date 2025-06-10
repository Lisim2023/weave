package cn.filaura.weave.example.ref;

import java.util.List;
import java.util.Map;


/**
 * 自定义引用服务接口
 * <p>为每个需要被引用的表实现此接口，并注册为SpringBean，以便通过一致的操作查询数据
 */
public interface RefService {

    /**
     * 支持的表名，需要与@Ref注解标注的一致
     * @return 表名
     */
    String getSupportedTable();

    /**
     * 通过id集合查询数据，数据以Map结构表示
     * @param ids id集合
     * @return 引用数据
     */
    Map<Object, Map<String, Object>> queryRefData(List<Long> ids);

}
