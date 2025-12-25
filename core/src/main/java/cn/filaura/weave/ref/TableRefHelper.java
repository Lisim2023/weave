package cn.filaura.weave.ref;

import cn.filaura.weave.PojoAccessor;
import cn.filaura.weave.type.TypeConverter;

import java.util.Map;

public class TableRefHelper {

    private final TableRefProcessor tableRefProcessor = new TableRefProcessor();

    private final TableRefDataProvider tableRefDataProvider;

    public TableRefHelper(TableRefDataProvider tableRefDataProvider) {
        this.tableRefDataProvider = tableRefDataProvider;
    }

    public <T> T populateTableReferences(T pojos) {
        Map<String, TableQuery> dbQueries = tableRefProcessor.collectReferenceInfo(pojos);
        if (dbQueries == null || dbQueries.isEmpty()) {
            return pojos;
        }

        Map<String, TableResult> referenceData = tableRefDataProvider.getReferenceData(dbQueries);
        tableRefProcessor.weave(pojos, referenceData);
        return pojos;
    }

    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        tableRefProcessor.setPojoAccessor(pojoAccessor);
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        tableRefProcessor.setTypeConverter(typeConverter);
    }

}
