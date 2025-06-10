package cn.filaura.weave.example.dict;

import cn.filaura.weave.dict.DictModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DictDataSourceMapper {

    List<DictModel> queryDictData(@Param("dictCodes") Collection<String> dictCodes);

}
