package cn.filaura.weave.ref;

import cn.filaura.weave.PojoAccessor;
import cn.filaura.weave.type.TypeConverter;


import java.util.*;

public class TableRefProcessor {

    private final TableRefWeaver tableRefWeaver = new TableRefWeaver();

    public static String buildMapKey(String tableName, String keyName) {
        return tableName + "_" + keyName;
    }

    public Map<String, TableQuery> collectReferenceInfo(Object pojos) {
        Map<String, TableQuery> dbQueryMap = new HashMap<>();
        tableRefWeaver.collectFieldValues(pojos, dbQueryMap);
        AbstractReferenceWeaver.removeIncompleteRefQuery(dbQueryMap, TableQuery::getIds);
        return dbQueryMap;
    }

    public void weave(Object pojos, Map<String, TableResult> dbResultMap) {
        tableRefWeaver.weave(pojos, dbResultMap);
    }

    public void setPojoAccessor(PojoAccessor pojoAccessor) {
        tableRefWeaver.setPojoAccessor(pojoAccessor);
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        tableRefWeaver.setTypeConverter(typeConverter);
    }

}
