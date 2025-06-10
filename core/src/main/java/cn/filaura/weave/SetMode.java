package cn.filaura.weave;


/**
 * 属性设置模式枚举，控制设置属性时的行为
 */
public enum SetMode {

    /** 当属性不存在时跳过设置操作 */
    SKIP_IF_ABSENT,

    /** 强制要求对象中存在目标属性，否则抛出异常 */
    ENFORCE_EXISTING

}
