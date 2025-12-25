package cn.filaura.weave.ref;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Mapper
public interface TableRefMapper {


    @Select("<script>" +
            "SELECT " +
            "<foreach collection='columns' item='column' separator=','>" +
            " ${column} </foreach>" +
            "FROM ${table} " +
            "WHERE ${keyColumn} in " +
            "<foreach collection='ids' item='id' separator=',' open='(' close=')'>" +
            " #{id} </foreach>" +
            "</script>")
    List<Map<String, Object>> queryReferenceData(@Param("table") String table,
                                                 @Param("columns") Set<String> columns,
                                                 @Param("keyColumn") String keyColumn,
                                                 @Param("ids") List<Object> ids);
}
