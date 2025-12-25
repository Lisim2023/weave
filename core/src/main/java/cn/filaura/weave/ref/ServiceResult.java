package cn.filaura.weave.ref;

import java.util.Map;


/**
 * 服务查询结果封装类
 * <p>
 * 封装了服务查询条件及其对应的查询结果，确保查询条件与结果的对应关系。
 * </p>
 * <p>初始化后不可修改</p>
 */
public class ServiceResult {

    /** 原始的服务查询条件 */
    private final ServiceQuery serviceQuery;

    /**
     * 查询结果映射
     * <p>Key为对象的ID，Value为完整对象</p>
     */
    private final Map<String, Object> results;

    public ServiceResult(ServiceQuery serviceQuery, Map<String, Object> results) {
        this.serviceQuery = serviceQuery;
        this.results = results;
    }

    public ServiceQuery getServiceQuery() {
        return serviceQuery;
    }

    public Map<String, Object> getResults() {
        return results;
    }
}
