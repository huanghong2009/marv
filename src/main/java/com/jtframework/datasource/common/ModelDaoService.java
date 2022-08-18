package com.jtframework.datasource.common;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.base.query.ParamsDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModelDaoService<T extends BaseModel> {


    public void insert(T model) throws Exception;

    public void insert(List<T> model) throws Exception;

    /**
     * 覆盖性修改
     *
     * @param model
     * @throws BusinessException
     */
    public long update(T model) throws Exception;

    public <T> T load(String id) throws Exception;


    /**
     * 根据id批量查询
     */
    public List<T> load(Set<String> ids) throws Exception;

    public long delete(String id) throws Exception;


    public long delete(List<String> id) throws Exception;

    public long delete(Set<String> id) throws Exception;

    public List<T> selectAll() throws Exception;

    /**
     * 根据kv查询多条数据
     *
     * @param key
     * @param value
     * @return
     * @throws BusinessException
     */
    public List<T> selectListByKV(String key, Object value) throws Exception;

    /**
     * 根据kv查询单条数据
     *
     * @param key
     * @param value
     * @return
     * @throws BusinessException
     */
    public <T> T selectOneByKV(String key, Object value) throws Exception;

    /**
     * 根据 map kv查询多条数据
     *
     * @param params
     * @return
     * @throws BusinessException
     */
    public List<T> selectListForMap(Map<String, String> params) throws Exception;

    /**
     * 根据kv查询单条数据
     *
     * @param params
     * @return
     * @throws BusinessException
     */
    public <T> T selectOneByMap(Map<String, Object> params) throws Exception;


    /**
     * 根据id 修改一个key value
     *
     * @throws SQLException
     */
    long updateKVById(String id, String key, Object value) throws Exception;


    /**
     * 根据key value 修改一个key value
     *
     * @throws SQLException
     */
    long updateKVByKV(String whereKey,String whereValue, String updateKey, Object updateValue) throws Exception;

    /**
     * 根据id 修改一个key value
     *
     * @throws SQLException
     */
    long updateMapById(String id, Map<String,Object> updateParmas) throws  Exception;

    /**
     * 根据map 修改一个mao
     *
     * @throws SQLException
     */
    long updateMapByMap(Map<String,Object> whereParmas,Map<String,Object> updateParmas) throws  Exception;



    /**
     * 根据kv 删除全部
     * @return
     * @throws Exception
     */
    long deleteAllByKV(String key,Object value)throws  Exception;



    /**
     * 根据map 删除全部
     * @return
     * @throws Exception
     */
    long deleteAllByMap(Map<String, Object> map)throws  Exception;

}
