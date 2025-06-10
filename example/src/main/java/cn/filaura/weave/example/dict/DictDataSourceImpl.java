package cn.filaura.weave.example.dict;

import cn.filaura.weave.dict.DictInfo;
import cn.filaura.weave.dict.DictModel;
import cn.filaura.weave.dict.DictDataSource;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 实现字典数据源接口
 * <p>查询符合条件的数据，并封装成指定的格式
 */
@Component
public class DictDataSourceImpl implements DictDataSource {

    @Resource
    private DictDataSourceMapper dictDataSourceMapper;



    @Override
    public List<DictInfo> queryDictData(Collection<String> dictCodes) {
        // 查询数据
        List<DictModel> dictModels = dictDataSourceMapper.queryDictData(dictCodes);
        // 封装数据并返回
        return DictInfo.fromDictModels(dictModels);
    }
}
