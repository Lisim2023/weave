package cn.filaura.weave.ref;

import cn.filaura.weave.PojoAccessor;
import cn.filaura.weave.type.TypeConverter;

import java.util.Map;

public class ServiceRefHelper {

    private final ServiceRefProcessor serviceRefProcessor = new ServiceRefProcessor();

    private final ServiceRefDataProvider serviceRefDataProvider;

    public ServiceRefHelper(ServiceRefDataProvider serviceRefDataProvider) {
        this.serviceRefDataProvider = serviceRefDataProvider;
    }

    public <T> T populateServiceReferences(T pojos) {
        Map<String, ServiceQuery> serviceQueries = serviceRefProcessor.collectReferenceInfo(pojos);
        if (serviceQueries.isEmpty()) {
            return pojos;
        }

        Map<String, ServiceResult> referenceData =
                serviceRefDataProvider.getReferenceData(serviceQueries);
        serviceRefProcessor.weave(pojos, referenceData);
        return pojos;
    }

    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        serviceRefProcessor.setPojoAccessor(pojoAccessor);
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        serviceRefProcessor.setTypeConverter(typeConverter);
    }

}
