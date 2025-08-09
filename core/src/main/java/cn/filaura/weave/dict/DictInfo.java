package cn.filaura.weave.dict;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 字典数据封装类
 */
public class DictInfo {

    /** 字典编码，字典的唯一标识码 */
    private String code;

    /**
     * 字典键值对映射
     * <p>key为字典值，value为字典文本
     */
    private Map<String, String> data;



    public DictInfo(String code) {
        this(code, new HashMap<>());
    }

    public DictInfo(String code, Map<String, String> data) {
        this.code = code;
        this.data = data;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }



    /**
     * 将 {@link DictInfo} 集合转换为Map（冗余字典编码作为Key）
     *
     * @param dictInfos 字典对象集合
     * @return 字典编码到字典对象的映射
     */
    public static Map<String, DictInfo> toCodeMap(Collection<DictInfo> dictInfos) {
        if (dictInfos == null || dictInfos.isEmpty()) {
            return Collections.emptyMap();
        }
        return dictInfos.stream()
                .filter(dictInfo -> dictInfo != null && dictInfo.getData() != null && !dictInfo.getData().isEmpty())
                .collect(Collectors.toMap(
                        DictInfo::getCode,
                        Function.identity(),
                        (existing, replacement) -> existing)
                );
    }

    /**
     * 将扁平化的字典模型列表按字典编码分组，并转换为双层Map结构。
     *
     * @param dictModels 从数据库查询出的字典记录列表
     * @return 分组后的嵌套映射：Map<dictCode, Map<value, text>>
     */
    public static Map<String, Map<String, String>> groupDictModelsByCode(List<DictModel> dictModels) {
        return dictModels.stream()
                // 按字典编码（code）进行分组
                .collect(Collectors.groupingBy(
                        DictModel::getCode,
                        // 对每组内的元素，构建 value -> text 的Map
                        Collectors.toMap(
                                DictModel::getValue,
                                DictModel::getText,
                                (existing, replacement) -> existing
                        )
                ));
    }

}
