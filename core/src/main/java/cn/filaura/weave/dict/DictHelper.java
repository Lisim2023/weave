package cn.filaura.weave.dict;


import cn.filaura.weave.MapUtils;
import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.BeanAccessor;
import cn.filaura.weave.PropertyDescriptorBeanAccessor;
import cn.filaura.weave.exception.BeanAccessException;
import cn.filaura.weave.type.ConvertUtil;
import cn.filaura.weave.type.Converter;
import cn.filaura.weave.exception.ConvertException;
import cn.filaura.weave.exception.DictDataNotFoundException;

import java.util.*;
import java.util.stream.Collectors;


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

    private final DictDataProvider dictDataProvider;
    private final DictWeaver dictWeaver;


    /**
     * @param dictDataSource 字典数据源
     */
    public DictHelper(DictDataSource dictDataSource) {
        this(new DirectDataSourceDictDataProvider(dictDataSource));
    }

    /**
     * @param dictDataSource 字典数据源
     * @param beanAccessor 属性访问器
     */
    public DictHelper(DictDataSource dictDataSource, BeanAccessor beanAccessor) {
        this(new DirectDataSourceDictDataProvider(dictDataSource), beanAccessor);
    }

    /**
     * @param dictDataProvider 字典数据提供
     */
    public DictHelper(DictDataProvider dictDataProvider) {
        this(dictDataProvider, new PropertyDescriptorBeanAccessor());
    }

    /**
     * @param dictDataProvider 字典数据提供
     * @param beanAccessor 属性访问器
     */
    public DictHelper(DictDataProvider dictDataProvider, BeanAccessor beanAccessor) {
        this.dictDataProvider = dictDataProvider;
        this.dictWeaver = new DictWeaver(beanAccessor);
    }



    /**
     * 获取字典数据，并将其中的字典值转换为type指定的类型
     * @param dictCode 字典编码
     * @param type 字典值数据类型，可用的类型见{@link ConvertUtil}
     * @return Map结构的字典数据，其key为字典值，value为字典文本
     */
    public <T> Map<T, String> getDict(String dictCode, Class<T> type)
            throws ConvertException {
        return getDict(Collections.singletonList(dictCode), type).get(dictCode);
    }

    /**
     * 批量获取字典数据，并将其中的字典值转换为type指定的类型
     * @param dictCodes 字典编码集合
     * @param type 字典值数据类型，支持的类型见{@link ConvertUtil}
     * @return 嵌套Map结构表示的多组字典数据，外层的key为字典编码，内层的key为字典值，value为字典文本
     */
    public <T> Map<String, Map<T, String>> getDict(Collection<String> dictCodes, Class<T> type)
            throws ConvertException {
        Map<String, DictInfo> rawDictData = dictDataProvider.getDictData(dictCodes);
        Converter<T> converter = ConvertUtil.getConverter(type);
        if (converter == null) {
            throw new ConvertException("Unsupported conversion type: " + type);
        }

        Map<String, Map<T, String>> result = new HashMap<>(rawDictData.size());
        for (Map.Entry<String, DictInfo> entry : rawDictData.entrySet()) {
            String dictCode = entry.getKey();
            DictInfo dictInfo = entry.getValue();

            Map<T, String> convertedData = dictInfo.getData().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> converter.convert(e.getKey()),
                            Map.Entry::getValue
                    ));
            result.put(dictCode, convertedData);
        }

        return result;
    }

    /**
     * 获取反转的字典数据，并将其中的字典值转换为type指定的类型
     * @param dictCode 字典编码
     * @param type 字典值数据类型，可用的类型见{@link ConvertUtil}
     * @return Map结构的字典数据，其key为字典文本，value为字典值
     */
    public <T> Map<String, T> getReversedDict(String dictCode, Class<T> type)
            throws ConvertException {
        return MapUtils.invertMap(getDict(dictCode, type));
    }

    /**
     * 批量获取反转的字典数据，并将其中的字典值转换为type指定的类型
     * @param dictCodes 字典编码
     * @param type 字典值数据类型，可用的类型见{@link ConvertUtil}
     * @return 嵌套Map结构表示的多个字典数据，外层的key为字典编码，内层的key为字典文本，value为字典值
     */
    public <T> Map<String, Map<String, T>> getReversedDict(Collection<String> dictCodes, Class<T> type)
            throws ConvertException {
        Map<String, Map<T, String>> dict = getDict(dictCodes, type);
        if (dict == null) {
            return null;
        }
        Map<String, Map<String, T>> reverse = new HashMap<>(dict.size());
        dict.forEach((key, value) -> {
            reverse.put(key, MapUtils.invertMap(value));
        });
        return reverse;
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

}
