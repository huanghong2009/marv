package com.jtframework.datasource.mongodb;

import com.jtframework.base.exception.BusinessException;
import com.jtframework.datasource.common.ModelDaoService;

public interface MongoModelDaoService  extends ModelDaoService {
    /**
     * 保存，有id 修改，无id 插入
     *
     * @param model
     * @throws BusinessException
     */
    public void save(Object model) throws BusinessException;
}
