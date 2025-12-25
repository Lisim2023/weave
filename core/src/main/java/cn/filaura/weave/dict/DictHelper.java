package cn.filaura.weave.dict;



import cn.filaura.weave.PojoAccessor;
import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.exception.PojoAccessException;
import cn.filaura.weave.exception.DictDataNotFoundException;
import cn.filaura.weave.type.TypeConverter;

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

    private final DictDataProvider dictDataProvider;

    private final DictTextWeaver dictTextWeaver = new DictTextWeaver();
    private final DictValueWeaver dictValueWeaver = new DictValueWeaver();

    public DictHelper(DictDataProvider dictDataProvider) {
        this.dictDataProvider = dictDataProvider;
    }

    /**
     * <p>为传入的对象注入字典文本
     *
     * @param pojos 待处理的目标对象，可以是单个对象或集合
     * @return 处理后的的目标对象（原对象修改后返回）
     *
     */
    public <T> T populateDictText(T pojos) throws DictDataNotFoundException {
        List<String> dictCodes = dictTextWeaver.collectDictCodes(pojos);
        if (dictCodes == null || dictCodes.isEmpty()) {
            return pojos;
        }

        Map<String, DictInfo> dictInfoMap = dictDataProvider.getDictData(dictCodes);
        if (dictInfoMap == null) {
            throw new DictDataNotFoundException("Dictionary data is null for codes: " + dictCodes);
        }
        dictTextWeaver.weaveDictText(pojos, dictInfoMap);

        return pojos;
    }

    /**
     * <p>为传入的对象注入字典值
     *
     * @param pojos 待处理的目标对象，可以是单个对象或集合
     * @return 处理后的目标对象（原对象修改后返回）
     */
    public <T> T populateDictValue(T pojos)
            throws DictDataNotFoundException, PojoAccessException {
        List<String> dictCodes = dictValueWeaver.collectDictCodes(pojos);
        if (dictCodes == null || dictCodes.isEmpty()) {
            return pojos;
        }

        Map<String, DictInfo> dictInfoMap = dictDataProvider.getDictData(dictCodes);
        if (dictInfoMap == null) {
            throw new DictDataNotFoundException("Dictionary data is null for codes: " + dictCodes);
        }

        dictValueWeaver.weaveDictValue(pojos, dictInfoMap);
        return pojos;
    }


    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        dictTextWeaver.setPojoAccessor(pojoAccessor);
        dictValueWeaver.setPojoAccessor(pojoAccessor);
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        dictTextWeaver.setTypeConverter(typeConverter);
        dictValueWeaver.setTypeConverter(typeConverter);
    }
}
