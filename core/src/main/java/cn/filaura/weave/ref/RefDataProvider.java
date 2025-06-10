package cn.filaura.weave.ref;


import java.util.Collection;

/**
 * 引用数据提供接口
 */
public interface RefDataProvider {

    /**
     * 根据对象中提供的参数查询需要的数据，并将查询结果保存在对象的results中
     *
     * @param refInfos 引用信息对象的集合
     * @see RefInfo
     */
    void getRefData(Collection<RefInfo> refInfos);

}
