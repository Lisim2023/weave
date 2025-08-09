package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.dict.DictHelper;
import cn.filaura.weave.ref.RefHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 数据关联切面类，用于处理标注 {@code @Weave} 注解的方法返回结果。
 * <p>
 * 该切面在目标方法成功返回后执行，自动注入引用数据和字典文本到返回对象中。
 * 通过 {@link DictHelper} 和 {@link RefHelper} 实现具体填充逻辑，二者均为非强制依赖。
 * </p>
 */
@Aspect
public class WeaveAspect {

    @Autowired(required = false)
    private DictHelper dictHelper;

    @Autowired(required = false)
    private RefHelper refHelper;

    @Pointcut("@annotation(cn.filaura.weave.annotation.Weave)")
    public void weave() {
    }

    @AfterReturning(value = "weave()", returning = "result")
    public void afterReturning(Object result){
        if (refHelper != null) {
            refHelper.populateRefData(result);
        }
        if (dictHelper != null) {
            dictHelper.populateDictText(result);
        }
    }

}
