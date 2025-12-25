package cn.filaura.weave.inject;


import cn.filaura.weave.dict.DictHelper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.beans.factory.annotation.Autowired;

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
