package cn.filaura.weave.dict;


import java.util.*;

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


    public DictInfo() {
    }

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
