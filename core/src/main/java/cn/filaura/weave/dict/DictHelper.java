package cn.filaura.weave.dict;



import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.exception.DictDataNotFoundException;
import java.util.*;


/**
 * 字典助手
 * <p>数据字典功能的操作入口，提供获取字典数据、向对象中注入字典数据以及参数设置等功能。
 *
 * <p>该类依赖于 {@link DictDataProvider} 提供字典数据
 *
 * @see Dict
 * @see DictDataProvider
 */
public class DictHelper {

    private final DictWeaver dictWeaver = new DictWeaver();
    private final DictDataProvider dictDataProvider;



    /**
     * @param dictDataSource 字典数据源
     */
    public DictHelper(DictDataSource dictDataSource) {
        this(new DirectDataSourceDictDataProvider(dictDataSource));
    }

    /**
     * @param dictDataProvider 字典数据提供
     */
    public DictHelper(DictDataProvider dictDataProvider) {
        this.dictDataProvider = dictDataProvider;
    }



    /**
     * <p>为传入的对象注入字典文本
     *
     * @param beans 待处理的目标对象，可以是单个对象或集合
     * @return 处理后的的目标对象（原对象修改后返回）
     *
     */
    public <T> T populateDictText(T beans)
            throws DictDataNotFoundException, BeanAccessException {
        Set<String> dictCodes = dictWeaver.collectDictCodes(beans);
        if (dictCodes == null || dictCodes.isEmpty()) {
            return beans;
        }

        Map<String, DictInfo> dict = dictDataProvider.getDictData(dictCodes);
        if (dict == null) {
            throw new DictDataNotFoundException("Dictionary data is null for codes: " + dictCodes);
        }

        dictWeaver.populateDictText(beans, dict);
        return beans;
    }

    /**
     * <p>为传入的对象注入字典值
     *
     * @param beans 待处理的目标对象，可以是单个对象或集合
     * @return 处理后的目标对象（原对象修改后返回）
     */
    public <T> T populateDictValue(T beans)
            throws DictDataNotFoundException, BeanAccessException {
        Set<String> dictCodes = dictWeaver.collectDictCodes(beans);
        if (dictCodes == null || dictCodes.isEmpty()) {
            return beans;
        }

        Map<String, DictInfo> dict = dictDataProvider.getDictData(dictCodes);
        if (dict == null) {
            throw new DictDataNotFoundException("Dictionary data is null for codes: " + dictCodes);
        }

        dictWeaver.populateDictValue(beans, dict);
        return beans;
    }


    /**
     * 获取字典属性名后缀
     * @return 字典属性名后缀
     */
    public String getFieldNameSuffix() {
        return dictWeaver.getFieldNameSuffix();
    }

    /**
     * 设置字典属性名后缀
     * @param fieldNameSuffix 新的属性名后缀
     */
    public void setFieldNameSuffix(String fieldNameSuffix) {
        dictWeaver.setFieldNameSuffix(fieldNameSuffix);
    }

    /**
     * 获取属性值分隔符
     * @return 分隔符
     */
    public String getDelimiter() {
        return dictWeaver.getDelimiter();
    }

    /**
     * 设置属性值分隔符
     * @param delimiter 分隔符
     */
    public void setDelimiter(String delimiter) {
        dictWeaver.setDelimiter(delimiter);
    }


    public BeanAccessor getBeanAccessor() {
        return dictWeaver.getBeanAccessor();
    }

    public void setBeanAccessor(BeanAccessor beanAccessor) {
        dictWeaver.setBeanAccessor(beanAccessor);
    }
}
