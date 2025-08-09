package cn.filaura.weave.example.ref;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;
import java.util.Map;

@Mapper
public interface RefDataSource1Mapper {


    @Select("<script>" +
            "SELECT " +
            "<foreach collection='columns' item='column' separator=','>" +
            " ${column} </foreach>" +
            "FROM ${table} " +
            "WHERE id in " +
            "<foreach collection='ids' item='id' separator=',' open='(' close=')'>" +
            " #{id} </foreach>" +
            "</script>")
    List<Map<String, Object>> queryRefData(@Param("table") String table,
                                                  @Param("columns") String[] columns,
                                                  @Param("ids") List<Long> ids);

}
