package edu.usc.secondkill.common.dynamicquery;

import java.util.List;

public interface DynamicQuery {

    /**
     * CRUD
     */
    public void save(Object entity);
    public void update(Object entity);
    public <T> void delete(Class<T> entityClass, Object entityId);
    public <T> void delete(Class<T> entityClass, Object[] entityIds);

    /**
     * query items, return List
     * @param nativeSql
     * @param params
     * @return  List<T>
     * @Date	2018/12/18
     *
     * 2018/12/18 YanJF
     *
     */
    <T> List<T> nativeQueryList(String nativeSql, Object... params);
    <T> List<T> nativeQueryListMap(String nativeSql,Object... params);
    <T> List<T> nativeQueryListModel(Class<T> resultClass, String nativeSql, Object... params);
    Object nativeQueryObject(String nativeSql, Object... params);
    Object[] nativeQueryArray(String nativeSql, Object... params);
    int nativeExecuteUpdate(String nativeSql, Object... params);


}
