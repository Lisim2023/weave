package cn.filaura.weave;


/**
 * 属性获取模式枚举，控制获取属性时的行为
 */
public enum GetMode {

    /** 当属性值为null时返回null */
    PRESERVE_NULL,

    /** 当属性值为null时初始化该属性 */
    INIT_IF_NULL

}
