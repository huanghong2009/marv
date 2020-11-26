package com.jtframework.datasource.mysql;

import com.jtframework.base.exception.BusinessException;

import java.util.List;

public interface MysqlModelDaoService<T> {


    public void insert(T model) throws BusinessException;

    public T load(String id) throws BusinessException;

    public int delete(String id) throws BusinessException;

    public int update(T model) throws BusinessException;

    public List<T> selectAll() throws BusinessException;


}
