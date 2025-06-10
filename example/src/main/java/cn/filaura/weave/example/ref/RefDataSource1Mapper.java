package cn.filaura.weave.example.ref;


import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

@Mapper
public interface RefDataSource1Mapper {

    @MapKey("id")
    Map<Object, Map<String, Object>> queryRefData(@Param("table") String table,
                                                  @Param("columns") String[] columns,
                                                  @Param("ids") List<Long> ids);

}
