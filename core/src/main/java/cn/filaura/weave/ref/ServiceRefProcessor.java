package cn.filaura.weave.ref;

import cn.filaura.weave.PojoAccessor;
import cn.filaura.weave.type.TypeConverter;

import java.util.HashMap;
import java.util.Map;

public class ServiceRefProcessor {

    private final RecordEmbedWeaver recordEmbedWeaver = new RecordEmbedWeaver();
    private final ServiceRefWeaver serviceRefWeaver = new ServiceRefWeaver();

    public static String buildMapKey(Class<?> service, String method) {
        return service.getSimpleName() + "_" + method;
    }

    public Map<String, ServiceQuery> collectReferenceInfo(Object pojos) {
        Map<String, ServiceQuery> serviceQueries = new HashMap<>();
        recordEmbedWeaver.collectForeignKeyValues(pojos, serviceQueries);
        serviceRefWeaver.collectForeignKeyValues(pojos, serviceQueries);
        AbstractReferenceWeaver.removeIncompleteRefQuery(serviceQueries, ServiceQuery::getIds);
        return serviceQueries;
    }

    public void weave(Object pojos, Map<String, ServiceResult> serviceResultMap) {
        serviceRefWeaver.weave(pojos, serviceResultMap);
        recordEmbedWeaver.weave(pojos, serviceResultMap);
    }

    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        serviceRefWeaver.setPojoAccessor(pojoAccessor);
        recordEmbedWeaver.setPojoAccessor(pojoAccessor);
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        serviceRefWeaver.setTypeConverter(typeConverter);
        recordEmbedWeaver.setTypeConverter(typeConverter);
    }

}
