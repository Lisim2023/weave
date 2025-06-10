package cn.filaura.weave.dict;

import java.util.Collection;
import java.util.Map;

/**
 * 字典数据提供接口
 * <p>字典助手通过此接口获取数据
 */
public interface DictDataProvider {

    /**
     * 获取字典数据
     * <p>为方便检索，返回值需要额外冗余字典编码作为key，组成Map结构
     * @param dictCodes 字典编码的集合
     * @return 字典编码与字典信息对象的映射Map
     */
    Map<String, DictInfo> getDictData(Collection<String> dictCodes);
}
