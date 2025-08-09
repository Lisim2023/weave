package cn.filaura.weave.example.dict;

import cn.filaura.weave.dict.DictInfo;
import cn.filaura.weave.dict.DictModel;
import cn.filaura.weave.dict.DictDataSource;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 基于数据库的字典数据源实现
 * <p>查询字典数据，并封装成指定的格式，用于支持Dict注解的翻译功能
 */
@Component
public class DictDataSourceImpl implements DictDataSource {

    @Resource
    private DictDataSourceMapper dictDataSourceMapper;


    /**
     * 根据字典编码列表批量查询字典项，并组织成所需结构。
     *
     * @param dictCodes 字典类型编码集合（如 ["user_status", "gender"]）
     * @return 包含每个字典类型及其键值对映射的 {@link DictInfo} 列表
     */
    @Override
    public List<DictInfo> queryDictData(Collection<String> dictCodes) {
        List<DictModel> dictModels = dictDataSourceMapper.queryDictData(dictCodes);
        Map<String, Map<String, String>> dictDataMap = DictInfo.groupDictModelsByCode(dictModels);
        List<DictInfo> dictInfos = new ArrayList<>();
        for (String dictCode : dictCodes) {
            dictInfos.add(new DictInfo(dictCode, dictDataMap.get(dictCode)));
        }
        return dictInfos;
    }

}
