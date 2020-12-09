package com.jtframework.datasource.common;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ModelDaoService<T> {


    public void insert(T model) throws BusinessException;

    public T load(String id) throws BusinessException;

    public int delete(String id) throws BusinessException;

    public List<T> selectAll() throws BusinessException;

    /**
     * 根据kv查询多条数据
     *
     * @param key
     * @param value
     * @return
     * @throws BusinessException
     */
    public List<T> selectListByKV(String key,String value) throws BusinessException;

    /**
     * 根据kv查询单条数据
     *
     * @param key
     * @param value
     * @return
     * @throws BusinessException
     */
    public T selectOneByKV(String key,String value) throws BusinessException;

    /**
     * 根据 map kv查询多条数据
     *
     * @param params
     * @return
     * @throws BusinessException
     */
    public List<T> selectListForMap(Map<String,String> params) throws BusinessException;

    /**
     * 根据kv查询单条数据
     *
     * @param params
     * @return
     * @throws BusinessException
     */
    public T selectOneByMap(Map<String,Object> params) throws BusinessException;

    /**
     * 分页查询，默认查询前10条
     *
     * @return
     * @throws SQLException
     */
    PageVO<T> defalutPageQuery() throws SQLException;
}
