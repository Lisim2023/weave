package cn.filaura.weave.inject;


import cn.filaura.weave.dict.DictHelper;
import cn.filaura.weave.ref.ServiceRefHelper;
import cn.filaura.weave.ref.TableRefHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;


@Aspect
public class WeaveAspect {

    @Autowired(required = false)
    private DictHelper dictHelper;

    @Autowired(required = false)
    private TableRefHelper tableRefHelper;

    @Autowired(required = false)
    private ServiceRefHelper serviceRefHelper;

    @Pointcut("@annotation(cn.filaura.weave.annotation.Weave)")
    public void weave() {
    }

    @AfterReturning(value = "weave()", returning = "result")
    public void afterReturning(Object result){
        if (serviceRefHelper != null) {
            serviceRefHelper.populateServiceReferences(result);
        }
        if (tableRefHelper != null) {
            tableRefHelper.populateTableReferences(result);
        }
        if (dictHelper != null) {
            dictHelper.populateDictText(result);
        }
    }

}
