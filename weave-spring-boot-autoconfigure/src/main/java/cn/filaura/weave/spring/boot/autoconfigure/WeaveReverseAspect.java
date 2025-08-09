package cn.filaura.weave.spring.boot.autoconfigure;


import cn.filaura.weave.dict.DictHelper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 逆向数据关联切面类，用于处理标注 {@code @WeaveReverse} 注解的方法参数。
 * <p>
 * 该切面在目标方法执行前拦截，对方法参数执行逆向转换（字典文本到字典值）。
 * 主要用于数据导入场景。
 * </p>
 */
@Aspect
public class WeaveReverseAspect {

    @Autowired
    private DictHelper dictHelper;

    @Pointcut("@annotation(cn.filaura.weave.annotation.WeaveReverse)")
    public void reverse() {
    }

    @Around("reverse()")
    public Object processWeaveReverse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        dictHelper.populateDictValue(args);
        return joinPoint.proceed(args);
    }

}
