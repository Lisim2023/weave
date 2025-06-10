package cn.filaura.weave.dict;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 直接从字典数据源查询数据
 *
 * @see DictDataProvider
 */
public class DirectDataSourceDictDataProvider implements DictDataProvider {

    private final DictDataSource dictDataSource;



    public DirectDataSourceDictDataProvider(DictDataSource dictDataSource) {
        this.dictDataSource = dictDataSource;
    }



    @Override
    public Map<String, DictInfo> getDictData(Collection<String> dictCodes) {
        List<DictInfo> dictInfos = dictDataSource.queryDictData(dictCodes);
        return DictInfo.toCodeMap(dictInfos);
    }

}