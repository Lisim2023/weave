package cn.filaura.weave.example.ref;

import cn.filaura.weave.example.consts.*;
import cn.filaura.weave.ref.RefDataSource;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 引用数据源接口实现1
 * <p>基于动态 SQL 的通用引用数据源实现
 *
 * @see RefService
 */
@Component
public class RefDataSource1 implements RefDataSource {

    @Resource
    private RefDataSource1Mapper refDataSource1Mapper;

    /**
     * 表名-列名映射
     * Key: 表名(如"sys_user")
     * Value: 该表需要查询的列名数组
     */
    private final Map<String, String[]> columnsMap = new HashMap<>();



    /**
     * 构造函数初始化表结构元数据
     *
     * <p>预先注册各表需要查询的字段，避免SQL注入风险并确保只查询必要字段</p>
     */
    public RefDataSource1() {
        // 注册用户表字段
        columnsMap.put(TableNames.USER, new String[]{UserColumns.ID, UserColumns.NICKNAME, UserColumns.USERNAME, UserColumns.GENDER, UserColumns.HOBBIES});
        // 注册字典表字段
        columnsMap.put(TableNames.DICT, new String[]{DictColumns.ID, DictColumns.CODE, DictColumns.NAME, DictColumns.DESCRIPTION});
        // 注册菜单表字段
        columnsMap.put(TableNames.MENU, new String[]{MenuColumns.ID, MenuColumns.TITLE});
        // 注册角色表字段
        columnsMap.put(TableNames.ROLE, new String[]{RoleColumns.ID, RoleColumns.NAME});
    }



    /**
     * 执行动态SQL查询引用数据
     */
    @Override
    public List<?> queryRefData(String table,
                                Collection<String> columns,
                                String key,
                                Collection<String> values) {
        // 表名有效性验证
        String[] columnNames = columnsMap.get(table);
        if (columnNames == null) {
            throw new IllegalArgumentException("Invalid table name: " + table);
        }

        // 将主键值转换为需要的类型并排序
        List<Long> ids = values.stream().map(Long::valueOf).sorted().toList();

        // 执行动态SQL查询
        return refDataSource1Mapper.queryRefData(table, columnNames, ids);
    }

}
