package cn.filaura.weave.example.ref;

import cn.filaura.weave.example.consts.*;
import cn.filaura.weave.ref.RefDataSource;
import cn.filaura.weave.ref.RefInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 引用数据源接口实现1
 *
 * <p>使用一条动态SQL完成数据查询
 *
 * @see RefService
 */
@Component
public class RefDataSource1 implements RefDataSource {

    @Resource
    private RefDataSource1Mapper refDataSource1Mapper;


    /**
     * 存储每张表对应的列名数组。
     * <p>每次查询相同表时使用相同的列，确保缓存一致
     */
    private final Map<String, String[]> columnsMap = new HashMap<>();



    public RefDataSource1() {
        columnsMap.put(TableNames.USER, new String[]{UserColumns.ID, UserColumns.NICKNAME, UserColumns.USERNAME, UserColumns.GENDER, UserColumns.HOBBIES});
        columnsMap.put(TableNames.DICT, new String[]{DictColumns.ID, DictColumns.CODE, DictColumns.NAME, DictColumns.DESCRIPTION});
        columnsMap.put(TableNames.MENU, new String[]{MenuColumns.ID, MenuColumns.TITLE});
        columnsMap.put(TableNames.ROLE, new String[]{RoleColumns.ID, RoleColumns.NAME});
    }



    @Override
    public RefInfo queryRefData(String table, Collection<String> columns, String key, Collection<String> values) {
        String[] columnNames = columnsMap.get(table);
        if (columnNames == null) {
            throw new IllegalArgumentException("Invalid table name: " + table);
        }

        // 将主键值转换为Long型并排序
        List<Long> ids = values.stream().map(Long::valueOf).sorted().toList();
        // 查询数据
        Map<Object, Map<String, Object>> data = refDataSource1Mapper.queryRefData(table, columnNames, ids);
        // 封装数据并返回
        return new RefInfo(table, key, RefInfo.mapKeyToString(data));
    }
}
