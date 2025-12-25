package cn.filaura.weave.example.dict;


import cn.filaura.weave.dict.DictInfo;
import cn.filaura.weave.dict.DictDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DictDataFetcher implements DictDataProvider.DataFetcher {

    @Autowired
    DictDataFetcherMapper mapper;

    @Override
    public Map<String, DictInfo> fetchDictData(List<String> dictCodes) {
        List<DictModel> dictModels = mapper.queryDictData(dictCodes);
        Map<String, Map<String, String>> dictDataMap = dictModels.stream()
                // 按字典编码（code）进行分组
                .collect(Collectors.groupingBy(
                        DictModel::getCode,
                        // 对每组内的元素，构建 value -> text 的Map
                        Collectors.toMap(
                                DictModel::getValue,
                                DictModel::getText,
                                (existing, replacement) -> existing
                        )
                ));

        Map<String, DictInfo> result = new HashMap<>(dictDataMap.size());
        dictDataMap.forEach((code, data) ->
                result.put(code, new DictInfo(code, data)));
        return result;
    }

}
