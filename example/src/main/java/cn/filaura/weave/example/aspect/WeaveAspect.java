package cn.filaura.weave.example.aspect;

import cn.filaura.weave.dict.DictHelper;
import cn.filaura.weave.ref.RefHelper;
import jakarta.annotation.Resource;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WeaveAspect {

    @Resource
    private RefHelper refHelper;

    @Resource
    private DictHelper dictHelper;



    @Pointcut("@annotation(cn.filaura.weave.annotation.Weave)")
    public void weave() {
    }

    @AfterReturning(value = "weave()", returning = "result")
    public void afterReturning(Object result){
        // 先引用后字典
        refHelper.populateRefData(result);
        dictHelper.populateDictText(result);
    }

}
