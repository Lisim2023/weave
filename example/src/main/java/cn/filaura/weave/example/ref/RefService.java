package cn.filaura.weave.example.ref;

import java.util.List;
import java.util.Map;


/**
 * 自定义引用服务接口
 * <p>定义提供引用服务的标准操作。
 * <p>为每个需要被引用的表实现此接口，以便通过一致的操作查询数据。
 */
public interface RefService {

    /**
     * 支持的表名，需要与@Ref注解标注的一致
     * @return 表名
     */
    String getSupportedTable();

    /**
     * 通过id集合查询数据
     * @param ids 主键值集合
     * @return 符合条件的数据记录列表
     */
    List<?> queryRefData(List<Long> ids);

}
