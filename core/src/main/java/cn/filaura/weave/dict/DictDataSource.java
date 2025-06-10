package cn.filaura.weave.dict;

import java.util.Collection;
import java.util.List;


/**
 * 字典数据源接口
 * <p>以字典编码为参数查询数据，并封装成指定的格式。
 */
public interface DictDataSource {

    /**
     * 查询字典编码对应的字典数据
     * @param dictCodes 字典编码集合
     * @return 符合条件的字典数据列表
     */
    List<DictInfo> queryDictData(Collection<String> dictCodes);
}
