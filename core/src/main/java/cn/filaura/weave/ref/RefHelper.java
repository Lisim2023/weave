package cn.filaura.weave.ref;


import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.exception.ConvertException;
import cn.filaura.weave.exception.RefDataNotFoundException;

import java.util.Map;

/**
 * 引用助手
 * <p>引用功能的操作入口，提供向对象中注入引用数据及参数设置等功能。
 *
 * <p>该类依赖于 {@link RefDataProvider} 提供引用数据
 *
 * @see Ref
 */
public class RefHelper {

    private final RefWeaver refWeaver = new RefWeaver();

    private final RefDataProvider refDataProvider;



    /**
     * @param refDataSource 引用数据源
     */
    public RefHelper(RefDataSource refDataSource) {
        this(new DirectDataSourceRefDataProvider(refDataSource));
    }

    /**
     * @param refDataProvider 引用数据提供接口实例
     */
    public RefHelper(RefDataProvider refDataProvider) {
        this.refDataProvider = refDataProvider;
    }



    /**
     * 为传入的对象注入引用数据
     *
     * @param beans 需要处理的目标对象，可以是单个对象或集合
     * @return 处理后的目标对象（原对象修改后返回）
     */
    public <T> T populateRefData(T beans)
            throws RefDataNotFoundException, BeanAccessException, ConvertException {
        Map<String, RefInfo> refInfoMap = refWeaver.collectRefInfo(beans);
        if (refInfoMap == null || refInfoMap.isEmpty()) {
            return beans;
        }

        refDataProvider.getRefData(refInfoMap.values());
        refWeaver.populateRefData(beans, refInfoMap);
        return beans;
    }



    /**
     * 获取空值显示文本
     * @return 当前使用的空值占位文本
     */
    public String getNullDisplayText() {
        return refWeaver.getNullDisplayText();
    }

    /**
     * 设置空值显示文本，当被引用的字段为null时显示
     * @param nullDisplayText 新的空值占位文本
     */
    public void setNullDisplayText(String nullDisplayText) {
        refWeaver.setNullDisplayText(nullDisplayText);
    }


    public String getGlobalPrimaryKey() {
        return refWeaver.getGlobalPrimaryKey();
    }

    public void setGlobalPrimaryKey(String globalPrimaryKey) {
        refWeaver.setGlobalPrimaryKey(globalPrimaryKey);
    }

    public BeanAccessor getBeanAccessor() {
        return refWeaver.getBeanAccessor();
    }

    public void setBeanAccessor(BeanAccessor beanAccessor) {
        refWeaver.setBeanAccessor(beanAccessor);
    }
}
