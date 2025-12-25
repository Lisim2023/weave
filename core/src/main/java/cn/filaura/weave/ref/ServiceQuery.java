package cn.filaura.weave.ref;

import java.util.*;


/**
 * 服务查询条件封装类
 * <p>
 * 用于封装需要调用的服务信息、方法以及查询参数。
 * </p>
 */
public class ServiceQuery {

    /** 服务接口/类类型 */
    private Class<?> service;

    /** 服务名称标识 */
    private String serviceName;

    /** 需要调用的方法名 */
    private String method;

    /** 结果映射中用作键的字段名 */
    private String keyField;

    /** 需要查询的ID集合 */
    private Set<Object> ids = new HashSet<>();


    public ServiceQuery() {
    }

    public ServiceQuery(Class<?> service, String method) {
        this.service = service;
        this.method = method;
    }



    public Class<?> getService() {
        return service;
    }

    public void setService(Class<?> service) {
        this.service = service;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public Set<Object> getIds() {
        return ids;
    }

    public void setIds(Set<Object> ids) {
        this.ids = ids;
    }

}
