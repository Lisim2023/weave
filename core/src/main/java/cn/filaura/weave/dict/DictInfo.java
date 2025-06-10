package cn.filaura.weave.dict;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 字典数据封装类
 */
public class DictInfo {

    /**
     * 对{@link DictModel}格式的字典数据进行封装
     * <p>根据字典编码对字典数据进行分组，每组生成一个 DictInfo 对象，
     *
     * @param dictModels 原始字典模型集合
     * @return 字典信息对象列表
     */
    public static List<DictInfo> fromDictModels(Collection<? extends DictModel> dictModels) {
        if (dictModels == null || dictModels.isEmpty()) {
            return Collections.emptyList();
        }
        return dictModels.stream()
                .collect(Collectors.groupingBy(DictModel::getCode))
                .entrySet().stream()
                .map(entry -> {
                    String code = entry.getKey();
                    Map<String, String> data = entry.getValue().stream()
                            .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (oldVal, newVal) -> oldVal));
                    return new DictInfo(code, data);
                })
                .collect(Collectors.toList());
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
                .collect(Collectors.toMap(DictInfo::getCode, Function.identity(), (existing, replacement) -> existing));
    }



    /** 字典编码，字典的唯一标识码 */
    private String code;

    /**
     * 字典数据
     * <p>默认情况下，key为字典值，value为字典文本
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
}
