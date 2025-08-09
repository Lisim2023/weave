package cn.filaura.weave.dict;

import java.util.Collection;
import java.util.List;


/**
 * 字典数据源接口
 */
public interface DictDataSource {

    /**
     * 批量查询字典数据
     *
     * @param dictCodes 需要查询的字典标识码集合
     * @return 字典信息列表，每个DictInfo包含一个字典标识码以及一组键值对Map
     */
    List<DictInfo> queryDictData(Collection<String> dictCodes);
}
