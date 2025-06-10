package cn.filaura.weave.ref;


import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.PropertyDescriptorBeanAccessor;
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

    private final RefDataProvider refDataProvider;
    private final RefWeaver refWeaver;



    /**
     * @param refDataSource 引用数据源
     */
    public RefHelper(RefDataSource refDataSource) {
        this(new DirectDataSourceRefDataProvider(refDataSource));
    }

    /**
     * @param refDataSource 引用数据源
     * @param beanAccessor 属性访问器
     */
    public RefHelper(RefDataSource refDataSource, BeanAccessor beanAccessor) {
        this(new DirectDataSourceRefDataProvider(refDataSource), beanAccessor);
    }

    /**
     * @param refDataProvider 引用数据提供接口实例
     */
    public RefHelper(RefDataProvider refDataProvider) {
        this(refDataProvider, new PropertyDescriptorBeanAccessor());
    }

    /**
     * @param refDataProvider 引用数据提供接口实例
     * @param beanAccessor 属性访问器
     */
    public RefHelper(RefDataProvider refDataProvider, BeanAccessor beanAccessor) {
        this.refDataProvider = refDataProvider;
        this.refWeaver = new RefWeaver(beanAccessor);
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
        refWeaver.weave(beans, refInfoMap);
        return beans;
    }



    /**
     * 获取属性值分隔符
     * @return 当前使用的分隔符
     */
    public String getDelimiter() {
        return RefWeaver.getDelimiter();
    }

    /**
     * 设置属性值分隔符
     * @param delimiter 新的分隔符
     */
    public void setDelimiter(String delimiter) {
        RefWeaver.setDelimiter(delimiter);
    }

    /**
     * 获取属性名称中缀
     * @return 当前使用的属性名中缀
     */
    public String getFieldNameInfix() {
        return RefWeaver.getFieldNameInfix();
    }

    /**
     * 设置属性名中缀
     * @param fieldNameInfix 新的属性名中缀
     */
    public void setFieldNameInfix(String fieldNameInfix) {
        RefWeaver.setFieldNameInfix(fieldNameInfix);
    }

    /**
     * 获取空值显示文本
     * @return 当前使用的空值占位文本
     */
    public String getNullDisplayText() {
        return RefWeaver.getNullDisplayText();
    }

    /**
     * 设置空值显示文本
     * @param nullDisplayText 新的空值占位文本，当引用数据为null时显示
     */
    public void setNullDisplayText(String nullDisplayText) {
        RefWeaver.setNullDisplayText(nullDisplayText);
    }
}
