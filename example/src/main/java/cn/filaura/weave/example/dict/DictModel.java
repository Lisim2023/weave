package cn.filaura.weave.example.dict;


/**
 * 字典数据模板
 */
public class DictModel {

    /** 字典编码 */
    private String code;

    /** 字典值 */
    private String value;

    /** 字典文本 */
    private String text;



    public DictModel() {
    }

    public DictModel(String code, String value, String text) {
        this.code = code;
        this.value = value;
        this.text = text;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
